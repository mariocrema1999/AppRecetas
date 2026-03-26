package com.example.recetasapp.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recetasapp.R
import com.example.recetasapp.model.DEFAULT_RECIPES
import com.example.recetasapp.model.Recipe
import com.example.recetasapp.model.RecipeCategory
import com.example.recetasapp.utils.AudioManager
import com.example.recetasapp.utils.RecipeManager
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private lateinit var audioManager: AudioManager
    private lateinit var recipeManager: RecipeManager
    private lateinit var searchView: SearchView
    private lateinit var cgFilters: ChipGroup
    private var recipes = mutableListOf<Recipe>()
    private var isShowingOnlyMyRecipes = false

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val newRecipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableExtra("NEW_RECIPE", Recipe::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra<Recipe>("NEW_RECIPE")
            }
            
            if (newRecipe != null) {
                recipes.add(newRecipe)
                recipeManager.saveRecipes(recipes)
                searchView.setQuery("", false)
                cgFilters.clearCheck()
                isShowingOnlyMyRecipes = false
                adapter.updateRecipes(recipes)
                checkEmptyState()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        Log.d("MainActivity", "onCreate called")
        audioManager = AudioManager(this)
        recipeManager = RecipeManager(this)

        recipes = recipeManager.loadRecipes().toMutableList()
        if (recipes.isEmpty()) {
            recipes.addAll(DEFAULT_RECIPES)
            recipeManager.saveRecipes(recipes)
        }

        recyclerView = findViewById(R.id.recyclerViewRecipes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RecipeAdapter(recipes) { recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE", recipe)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterRecipes(newText)
                return true
            }
        })

        cgFilters = findViewById(R.id.cgFilters)
        cgFilters.setOnCheckedStateChangeListener { _, _ ->
            filterRecipes(searchView.query.toString())
        }

        registerForContextMenu(recyclerView)

        findViewById<ExtendedFloatingActionButton>(R.id.fabMisRecetas).setOnClickListener {
            isShowingOnlyMyRecipes = !isShowingOnlyMyRecipes
            // Opcional: Cambiar el texto o color del botón para indicar estado
            (it as ExtendedFloatingActionButton).text = if (isShowingOnlyMyRecipes) "Ver Todas" else "Mis recetas"
            filterRecipes(searchView.query.toString())
        }

        findViewById<FloatingActionButton>(R.id.fabAddRecipe).setOnClickListener {
            val intent = Intent(this, RecipeFormActivity::class.java)
            startForResult.launch(intent)
        }

        checkEmptyState()
    }

    private fun filterRecipes(query: String?) {
        val selectedChipIds = cgFilters.checkedChipIds
        
        val selectedCategories = selectedChipIds.mapNotNull { chipId ->
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

        val defaultIds = DEFAULT_RECIPES.map { it.id }.toSet()

        val filteredList = recipes.filter { recipe ->
            val matchesQuery = query.isNullOrBlank() || recipe.name.contains(query, ignoreCase = true)
            
            val recipeCategories = recipe.categories ?: emptyList()
            val matchesCategories = if (selectedCategories.isEmpty()) {
                true
            } else {
                selectedCategories.any { it in recipeCategories }
            }
            
            val matchesMyRecipes = if (isShowingOnlyMyRecipes) {
                recipe.id !in defaultIds
            } else {
                true
            }
            
            matchesQuery && matchesCategories && matchesMyRecipes
        }
        
        adapter.updateRecipes(filteredList)
        
        val emptyView = findViewById<View>(R.id.emptyStateView)
        val tvEmptyMessage = findViewById<TextView>(R.id.tvEmptyMessage)
        
        if (filteredList.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            val message = when {
                isShowingOnlyMyRecipes && query.isNullOrBlank() && selectedCategories.isEmpty() -> "No has creado recetas propias aún"
                else -> "No se encontraron recetas"
            }
            tvEmptyMessage.text = message
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = adapter.longClickedPosition
        if (position == -1) return super.onContextItemSelected(item)

        return when (item.itemId) {
            R.id.contextual1 -> { 
                val currentQuery = searchView.query.toString()
                val filteredList = adapter.recipes
                if (position < filteredList.size) {
                    val recipeToRemove = filteredList[position]
                    recipes.remove(recipeToRemove)
                    recipeManager.saveRecipes(recipes)
                    filterRecipes(currentQuery)
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun checkEmptyState() {
        filterRecipes(searchView.query?.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.stop()
    }
}
