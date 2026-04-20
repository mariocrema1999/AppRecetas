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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recetasapp.R
import com.example.recetasapp.data.AppDatabase
import com.example.recetasapp.model.DEFAULT_RECIPES
import com.example.recetasapp.model.Recipe
import com.example.recetasapp.model.RecipeCategory
import com.example.recetasapp.utils.AudioManager
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private lateinit var audioManager: AudioManager
    private lateinit var database: AppDatabase
    private var searchView: SearchView? = null
    private lateinit var cgFilters: ChipGroup
    
    private var allRecipes = listOf<Recipe>()
    private var isShowingOnlyMyRecipes = false
    private var currentUserId: String? = null
    private var userAllergens = setOf<String>()

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
        super.onCreate(savedInstanceState)
        
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        currentUserId = sharedPref.getString("logged_user", null)
        
        if (currentUserId == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        audioManager = AudioManager(this)
        database = AppDatabase.getDatabase(this)

        setupUI()
        observeRecipes()
    }

    private fun setupUI() {
        recyclerView = findViewById(R.id.recyclerViewRecipes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecipeAdapter(emptyList()) { recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE", recipe)
            startActivity(intent)
        }
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

    private fun observeRecipes() {
        lifecycleScope.launch {
            database.recipeDao().getVisibleRecipes(currentUserId ?: "").collectLatest { recipesFromDb ->
                if (recipesFromDb.isEmpty()) {
                    withContext(Dispatchers.IO) {
                        DEFAULT_RECIPES.forEach { database.recipeDao().insertRecipe(it) }
                    }
                } else {
                    allRecipes = recipesFromDb
                    filterRecipes(searchView?.query?.toString())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userAllergens = sharedPrefs.getStringSet("selected_allergens", emptySet()) ?: emptySet()
        filterRecipes(searchView?.query?.toString())
    }

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
                R.id.chipFilterCeliacos -> RecipeCategory.CELIACOS
                R.id.chipFilterDiabeticos -> RecipeCategory.DIABETICOS
                R.id.chipFilterPostres -> RecipeCategory.POSTRES
                else -> null
            }
        }

        val filteredList = allRecipes.filter { recipe ->
            val matchesQuery = query.isNullOrBlank() || recipe.name.contains(query, ignoreCase = true)
            val matchesCategories = selectedCategories.isEmpty() || selectedCategories.all { it in (recipe.categories ?: emptyList()) }
            val matchesMyRecipes = !isShowingOnlyMyRecipes || recipe.creatorId == currentUserId
            
            val recipeAllergensNames = recipe.allergens?.map { it.name }?.toSet() ?: emptySet()
            val hasUserAllergen = userAllergens.any { it in recipeAllergensNames }
            
            matchesQuery && matchesCategories && matchesMyRecipes && !hasUserAllergen
        }
        
        adapter.updateRecipes(filteredList)
        updateEmptyState(filteredList.isEmpty())
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        val emptyView = findViewById<View>(R.id.emptyStateView)
        val tvEmptyMessage = findViewById<TextView>(R.id.tvEmptyMessage)
        
        if (isEmpty) {
            emptyView?.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            tvEmptyMessage?.text = if (isShowingOnlyMyRecipes) "No tienes recetas propias aún" else "No se encontraron recetas"
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, UserProfileActivity::class.java))
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
        audioManager.stop()
    }
}
