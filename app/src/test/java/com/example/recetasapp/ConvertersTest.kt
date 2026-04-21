package com.example.recetasapp

import com.example.recetasapp.model.Allergen
import com.example.recetasapp.model.Converters
import com.example.recetasapp.model.RecipeCategory
import com.example.recetasapp.model.RecipeIngredient
import com.example.recetasapp.model.Step
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun testIngredientListConversion() {
        val list = listOf(RecipeIngredient("Harina", "500g"), RecipeIngredient("Huevo"))
        val json = converters.fromIngredientList(list)
        val result = converters.toIngredientList(json)
        assertEquals(list, result)
    }

    @Test
    fun testStepListConversion() {
        val list = listOf(Step("Paso 1", 10), Step("Paso 2"))
        val json = converters.fromStepList(list)
        val result = converters.toStepList(json)
        assertEquals(list, result)
    }

    @Test
    fun testCategoryListConversion() {
        val list = listOf(RecipeCategory.CARNE, RecipeCategory.POSTRES)
        val json = converters.fromCategoryList(list)
        val result = converters.toCategoryList(json)
        assertEquals(list, result)
    }

    @Test
    fun testAllergenListConversion() {
        val list = listOf(Allergen.GLUTEN, Allergen.LACTEOS)
        val json = converters.fromAllergenList(list)
        val result = converters.toAllergenList(json)
        assertEquals(list, result)
    }

    @Test
    fun testNullAllergenListConversion() {
        val json = converters.fromAllergenList(null)
        val result = converters.toAllergenList(json)
        assertEquals(null, result)
    }
}
