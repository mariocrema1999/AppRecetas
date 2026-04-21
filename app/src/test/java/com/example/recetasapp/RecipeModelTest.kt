package com.example.recetasapp

import com.example.recetasapp.model.Allergen
import com.example.recetasapp.model.Recipe
import com.example.recetasapp.model.RecipeCategory
import com.example.recetasapp.model.RecipeIngredient
import com.example.recetasapp.model.Step
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeModelTest {

    private val gson = Gson()

    @Test
    fun testRecipeSerialization() {
        val recipe = Recipe(
            id = "test-1",
            name = "Test Recipe",
            description = "Description",
            image = "url",
            prepTime = 30,
            servings = 2,
            ingredients = listOf(RecipeIngredient("Ingredient 1", "100g")),
            steps = listOf(Step("Step 1", 5)),
            categories = listOf(RecipeCategory.CARNE),
            allergens = listOf(Allergen.GLUTEN)
        )

        val json = gson.toJson(recipe)
        val type = object : TypeToken<Recipe>() {}.type
        val decodedRecipe: Recipe = gson.fromJson(json, type)

        assertEquals(recipe.id, decodedRecipe.id)
        assertEquals(recipe.name, decodedRecipe.name)
        assertEquals(recipe.ingredients.size, decodedRecipe.ingredients.size)
        assertEquals(recipe.ingredients[0].name, decodedRecipe.ingredients[0].name)
        assertEquals(recipe.categories[0], decodedRecipe.categories[0])
        assertEquals(recipe.allergens?.get(0), decodedRecipe.allergens?.get(0))
    }

    @Test
    fun testRecipeWithNullAllergens() {
        val recipe = Recipe(
            id = "test-2",
            name = "No Allergens",
            description = "Desc",
            image = "",
            prepTime = 10,
            servings = 1,
            ingredients = emptyList(),
            steps = emptyList(),
            allergens = null
        )

        val json = gson.toJson(recipe)
        val decodedRecipe: Recipe = gson.fromJson(json, Recipe::class.java)

        assertTrue(decodedRecipe.allergens == null)
    }

    @Test
    fun testRecipeDatabaseFields() {
        val recipe = Recipe(
            id = "db-test",
            name = "Receta con Usuario",
            description = "Desc",
            image = "",
            prepTime = 5,
            servings = 1,
            ingredients = emptyList(),
            steps = emptyList(),
            creatorId = "user_123", // Probamos el ID del creador
            isPublic = false        // Probamos la privacidad
        )

        val json = gson.toJson(recipe)
        val decodedRecipe: Recipe = gson.fromJson(json, Recipe::class.java)

        assertEquals("user_123", decodedRecipe.creatorId)
        assertEquals(false, decodedRecipe.isPublic)
    }
}
