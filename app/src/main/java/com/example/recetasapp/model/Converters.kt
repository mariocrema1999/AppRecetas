package com.example.recetasapp.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromIngredientList(value: List<RecipeIngredient>): String = gson.toJson(value)

    @TypeConverter
    fun toIngredientList(value: String): List<RecipeIngredient> {
        val listType = object : TypeToken<List<RecipeIngredient>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromStepList(value: List<Step>): String = gson.toJson(value)

    @TypeConverter
    fun toStepList(value: String): List<Step> {
        val listType = object : TypeToken<List<Step>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromCategoryList(value: List<RecipeCategory>): String = gson.toJson(value)

    @TypeConverter
    fun toCategoryList(value: String): List<RecipeCategory> {
        val listType = object : TypeToken<List<RecipeCategory>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromAllergenList(value: List<Allergen>?): String? = value?.let { gson.toJson(it) }

    @TypeConverter
    fun toAllergenList(value: String?): List<Allergen>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Allergen>>() {}.type
        return gson.fromJson(value, listType)
    }
}
