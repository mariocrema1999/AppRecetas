package com.example.recetasapp.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recetasapp.R
import com.example.recetasapp.data.AppDatabase
import com.example.recetasapp.model.Allergen
import com.example.recetasapp.model.DEFAULT_RECIPES
import com.example.recetasapp.model.Recipe
import com.example.recetasapp.model.RecipeCategory
import com.example.recetasapp.model.Favorite
import com.example.recetasapp.model.Restrictions
import com.example.recetasapp.utils.AudioManager
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Actividad principal de la aplicación.
 * Muestra la lista de recetas, permite filtrar por categorías, buscar por nombre,
 * y gestionar favoritos. También proporciona acceso al perfil de usuario y creación de recetas.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private lateinit var audioManager: AudioManager
    private lateinit var database: AppDatabase
    private var searchView: SearchView? = null
    private lateinit var cgFilters: ChipGroup
    
    private var allRecipes = listOf<Recipe>()
    private var favoriteIds = setOf<String>()
    private var isShowingOnlyMyRecipes = false
    private var isShowingOnlyFavorites = false
    private var currentUserId: String? = null
    private var userAllergens = setOf<String>()
    private var userRestrictions = setOf<String>()

    /**
     * Lanzador para recibir resultados de RecipeFormActivity al crear o editar una receta.
     */
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val recipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableExtra("NEW_RECIPE", Recipe::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra<Recipe>("NEW_RECIPE")
            }
            
            recipe?.let {
                lifecycleScope.launch(Dispatchers.IO) {
                    val recipeToSave = it.copy(creatorId = it.creatorId ?: currentUserId)
                    database.recipeDao().insertRecipe(recipeToSave)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        currentUserId = sharedPref.getString("logged_user", null)
        
        // Redirigir al login si no hay sesión iniciada
        if (currentUserId == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById(R.id.toolbar))

        audioManager = AudioManager(this)
        database = AppDatabase.getDatabase(this)

        setupUI()
        observeData()
    }

    /**
     * Configura los elementos de la interfaz de usuario.
     */
    private fun setupUI() {
        recyclerView = findViewById(R.id.recyclerViewRecipes)
        
        val columns = resources.getInteger(R.integer.recipe_grid_columns)
        recyclerView.layoutManager = if (columns > 1) {
            GridLayoutManager(this, columns)
        } else {
            LinearLayoutManager(this)
        }
        
        adapter = RecipeAdapter(
            _recipes = emptyList(),
            favoriteRecipeIds = emptySet(),
            onFavoriteClick = { recipe -> toggleFavorite(recipe) },
            onClick = { recipe ->
                val intent = Intent(this, RecipeDetailActivity::class.java)
                intent.putExtra("RECIPE", recipe)
                startActivity(intent)
            }
        )
        
        recyclerView.adapter = adapter
        registerForContextMenu(recyclerView)

        searchView = findViewById(R.id.searchView)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterRecipes(newText)
                return true
            }
        })

        cgFilters = findViewById(R.id.cgFilters)
        cgFilters.setOnCheckedStateChangeListener { _, _ ->
            filterRecipes(searchView?.query?.toString())
        }

        findViewById<ExtendedFloatingActionButton>(R.id.fabMisRecetas).setOnClickListener {
            isShowingOnlyMyRecipes = !isShowingOnlyMyRecipes
            (it as ExtendedFloatingActionButton).text = if (isShowingOnlyMyRecipes) "Ver Todas" else "Mis recetas"
            filterRecipes(searchView?.query?.toString())
        }

        findViewById<FloatingActionButton>(R.id.fabAddRecipe).setOnClickListener {
            startForResult.launch(Intent(this, RecipeFormActivity::class.java))
        }
    }

    /**
     * Alterna el estado de favorito de una receta para el usuario actual.
     */
    private fun toggleFavorite(recipe: Recipe) {
        val userId = currentUserId ?: return
        lifecycleScope.launch(Dispatchers.IO) {
            val favorite = Favorite(userId, recipe.id)
            if (favoriteIds.contains(recipe.id)) {
                database.favoriteDao().removeFavorite(favorite)
            } else {
                database.favoriteDao().addFavorite(favorite)
            }
        }
    }

    /**
     * Observa los cambios en las recetas y favoritos de la base de datos.
     */
    private fun observeData() {
        lifecycleScope.launch {
            val userId = currentUserId ?: ""
            combine(
                database.recipeDao().getVisibleRecipes(userId),
                database.favoriteDao().getFavoriteRecipeIds(userId)
            ) { recipesFromDb, favIds ->
                Pair(recipesFromDb, favIds.toSet())
            }.collectLatest { (recipesFromDb, favIds) ->
                if (recipesFromDb.isEmpty()) {
                    withContext(Dispatchers.IO) {
                        database.recipeDao().insertRecipes(DEFAULT_RECIPES)
                    }
                } else {
                    allRecipes = recipesFromDb.sortedByDescending { it.averageRating }
                    favoriteIds = favIds
                    filterRecipes(searchView?.query?.toString())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userAllergens = sharedPrefs.getStringSet("selected_allergens", emptySet()) ?: emptySet()
        userRestrictions = sharedPrefs.getStringSet("selected_restrictions", emptySet()) ?: emptySet()
        filterRecipes(searchView?.query?.toString())
    }

    /**
     * Filtra la lista de recetas basándose en la búsqueda, categorías y preferencias del usuario.
     * @param query Texto de búsqueda.
     */
    private fun filterRecipes(query: String?) {
        if (!::cgFilters.isInitialized) return

        val selectedCategories = cgFilters.checkedChipIds.mapNotNull { chipId ->
            when (chipId) {
                R.id.chipFilterCarne -> RecipeCategory.CARNE
                R.id.chipFilterPescado -> RecipeCategory.PESCADO
                R.id.chipFilterArroces -> RecipeCategory.ARROCES
                R.id.chipFilterVerduras -> RecipeCategory.VERDURAS_HORTALIZAS
                R.id.chipFilterLegumbres -> RecipeCategory.LEGUMBRES
                R.id.chipFilterHuevos -> RecipeCategory.HUEVOS
                R.id.chipFilterPastas -> RecipeCategory.PASTAS
                R.id.chipFilterSopas -> RecipeCategory.SOPAS_CREMAS
                R.id.chipFilterPostres -> RecipeCategory.POSTRES
                else -> null
            }
        }

        val filteredList = allRecipes.filter { recipe ->
            val matchesQuery = query.isNullOrBlank() || recipe.name.contains(query, ignoreCase = true)
            val matchesCategories = selectedCategories.isEmpty() || selectedCategories.all { it in (recipe.categories ?: emptyList()) }
            val matchesMyRecipes = !isShowingOnlyMyRecipes || recipe.creatorId == currentUserId
            val matchesFavorites = !isShowingOnlyFavorites || favoriteIds.contains(recipe.id)
            
            val recipeAllergenNames = recipe.allergens?.map { it.name }?.toSet() ?: emptySet()
            val hasUserAllergen = userAllergens.any { it in recipeAllergenNames }
            
            val recipeCategories = recipe.categories ?: emptyList()
            val recipeAllergens = recipe.allergens ?: emptyList()
            
            var fulfillsRestrictions = true
            
            // Lógica de filtrado por restricciones dietéticas
            if (userRestrictions.contains(Restrictions.VEGANOS.name)) {
                val isAnimalProduct = recipeCategories.any { it == RecipeCategory.CARNE || it == RecipeCategory.PESCADO || it == RecipeCategory.HUEVOS } ||
                                    recipeAllergens.any { it == Allergen.LACTEOS || it == Allergen.HUEVOS || it == Allergen.PESCADO || it == Allergen.CRUSTACEOS || it == Allergen.MOLUSCOS }
                if (isAnimalProduct) fulfillsRestrictions = false
            }
            
            if (fulfillsRestrictions && userRestrictions.contains(Restrictions.VEGETARIANOS.name)) {
                val hasMeatOrFish = recipeCategories.any { it == RecipeCategory.CARNE || it == RecipeCategory.PESCADO } ||
                                   recipeAllergens.any { it == Allergen.PESCADO || it == Allergen.CRUSTACEOS || it == Allergen.MOLUSCOS }
                if (hasMeatOrFish) fulfillsRestrictions = false
            }
            
            if (fulfillsRestrictions && userRestrictions.contains(Restrictions.PESCETARIANOS.name)) {
                val hasMeat = recipeCategories.any { it == RecipeCategory.CARNE }
                if (hasMeat) fulfillsRestrictions = false
            }

            if (fulfillsRestrictions && userRestrictions.contains(Restrictions.CELIACOS.name)) {
                if (recipeAllergens.contains(Allergen.GLUTEN)) fulfillsRestrictions = false
            }
            
            matchesQuery && matchesCategories && matchesMyRecipes && matchesFavorites && !hasUserAllergen && fulfillsRestrictions
        }
        
        adapter.updateRecipes(filteredList, favoriteIds)
        updateEmptyState(filteredList.isEmpty())
    }

    /**
     * Muestra o oculta la vista de "sin resultados" según el estado de la lista.
     */
    private fun updateEmptyState(isEmpty: Boolean) {
        val emptyView = findViewById<View>(R.id.emptyStateView)
        val tvEmptyMessage = findViewById<TextView>(R.id.tvEmptyMessage)
        
        if (isEmpty) {
            emptyView?.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            tvEmptyMessage?.text = when {
                isShowingOnlyFavorites -> "No tienes recetas favoritas"
                isShowingOnlyMyRecipes -> "No tienes recetas propias aún"
                else -> "No se encontraron recetas"
            }
        } else {
            emptyView?.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = adapter.longClickedPosition
        val selectedRecipe = adapter.recipes.getOrNull(position) ?: return super.onContextItemSelected(item)

        return when (item.itemId) {
            R.id.action_edit -> {
                if (selectedRecipe.creatorId == currentUserId) {
                    val intent = Intent(this, RecipeFormActivity::class.java)
                    intent.putExtra("EDIT_RECIPE", selectedRecipe)
                    startForResult.launch(intent)
                } else {
                    Toast.makeText(this, "Solo puedes editar tus propias recetas", Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.contextual1 -> { 
                if (selectedRecipe.creatorId == currentUserId) {
                    lifecycleScope.launch(Dispatchers.IO) { database.recipeDao().deleteRecipe(selectedRecipe) }
                } else {
                    Toast.makeText(this, "Solo puedes eliminar tus propias recetas", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val favoritesItem = menu?.findItem(R.id.action_favorites)
        favoritesItem?.title = if (isShowingOnlyFavorites) "Ver todas las recetas" else "Mis recetas favoritas"
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorites -> {
                isShowingOnlyFavorites = !isShowingOnlyFavorites
                item.title = if (isShowingOnlyFavorites) "Ver todas las recetas" else "Mis recetas favoritas"
                filterRecipes(searchView?.query?.toString())
                true
            }
            R.id.action_profile -> {
                startActivity(Intent(this, UserProfileActivity::class.java))
                true
            }
            R.id.action_info -> {
                startActivity(Intent(this, AppInfoActivity::class.java))
                true
            }
            R.id.action_logout -> {
                getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().clear().apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.hardStop()
    }
}
