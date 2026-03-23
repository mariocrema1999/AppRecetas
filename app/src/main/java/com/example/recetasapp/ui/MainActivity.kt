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
import com.example.recetasapp.utils.AudioManager
import com.example.recetasapp.utils.RecipeManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private lateinit var audioManager: AudioManager
    private lateinit var recipeManager: RecipeManager
    private lateinit var searchView: SearchView
    private var recipes = mutableListOf<Recipe>()

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
                // Al añadir, resetear el filtro para ver la nueva receta
                searchView.setQuery("", false)
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

        // Configurar el buscador
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

        // Registrar el RecyclerView para el menú contextual
        registerForContextMenu(recyclerView)

        findViewById<FloatingActionButton>(R.id.fabAddRecipe).setOnClickListener {
            val intent = Intent(this, RecipeFormActivity::class.java)
            startForResult.launch(intent)
        }

        checkEmptyState()
    }

    private fun filterRecipes(query: String?) {
        val filteredList = if (query.isNullOrBlank()) {
            recipes
        } else {
            recipes.filter { it.name.contains(query, ignoreCase = true) }
        }
        adapter.updateRecipes(filteredList)
        
        // Mostrar mensaje de "no hay resultados" si el filtro no devuelve nada
        val emptyView = findViewById<View>(R.id.emptyStateView)
        val tvEmptyMessage = findViewById<TextView>(R.id.tvEmptyMessage)
        
        if (filteredList.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            tvEmptyMessage.text = if (query.isNullOrBlank()) "No hay recetas aún" else "No se encontraron recetas"
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
                val currentShownList = if (currentQuery.isBlank()) {
                    recipes
                } else {
                    recipes.filter { it.name.contains(currentQuery, ignoreCase = true) }
                }

                if (position < currentShownList.size) {
                    val recipeToRemove = currentShownList[position]
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
