package com.example.recetasapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
    private var recipes = mutableListOf<Recipe>()

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val newRecipe = result.data?.getParcelableExtra<Recipe>("NEW_RECIPE")
            if (newRecipe != null) {
                recipes.add(newRecipe)
                recipeManager.saveRecipes(recipes)
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

        recipes = recipeManager.loadRecipes()
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

        // Registrar el RecyclerView para el menú contextual
        registerForContextMenu(recyclerView)

        findViewById<FloatingActionButton>(R.id.fabAddRecipe).setOnClickListener {
            val intent = Intent(this, RecipeFormActivity::class.java)
            startForResult.launch(intent)
        }

        checkEmptyState()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = adapter.longClickedPosition
        if (position == -1) return super.onContextItemSelected(item)

        return when (item.itemId) {
            R.id.contextual1 -> { // Coincide con el ID en menu_contextual.xml
                // Eliminar la receta
                recipes.removeAt(position)
                // Notificar al adaptador con animación
                adapter.notifyItemRemoved(position)
                // Guardar los cambios
                recipeManager.saveRecipes(recipes)
                // Comprobar si la lista está vacía
                checkEmptyState()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun checkEmptyState() {
        val emptyView = findViewById<View>(R.id.emptyStateView)
        emptyView.visibility = if (recipes.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (recipes.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.stop()
    }
}
