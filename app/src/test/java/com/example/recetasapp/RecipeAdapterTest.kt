package com.example.recetasapp

import androidx.recyclerview.widget.RecyclerView
import com.example.recetasapp.model.Recipe
import com.example.recetasapp.ui.RecipeAdapter
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock

class RecipeAdapterTest {

    @Test
    fun testItemCount() {
        val recipes = listOf(
            Recipe("1", "Receta 1", "Desc 1", "", 10, 1, emptyList(), emptyList()),
            Recipe("2", "Receta 2", "Desc 2", "", 20, 2, emptyList(), emptyList())
        )
        val adapter = RecipeAdapter(recipes) { }
        
        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun testUpdateRecipes() {
        val initialRecipes = listOf(
            Recipe("1", "Receta 1", "Desc 1", "", 10, 1, emptyList(), emptyList())
        )
        val adapter = RecipeAdapter(initialRecipes) { }
        
        // Añadimos un observador mock para que DiffUtil no de error al intentar notificar cambios
        val observer = mock(RecyclerView.AdapterDataObserver::class.java)
        adapter.registerAdapterDataObserver(observer)
        
        val newRecipes = listOf(
            Recipe("1", "Receta 1", "Desc 1", "", 10, 1, emptyList(), emptyList()),
            Recipe("2", "Receta 2", "Desc 2", "", 20, 2, emptyList(), emptyList())
        )
        
        adapter.updateRecipes(newRecipes)
        
        assertEquals(2, adapter.itemCount)
        assertEquals("Receta 2", adapter.recipes[1].name)
    }
}
