package com.example.recetasapp.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Convertidores personalizados para permitir que Room almacene tipos de datos complejos
 * (como listas de objetos) convirtiéndolos a formato JSON.
 */
class Converters {
    private val gson = Gson()

    /**
     * Convierte una lista de ingredientes a una cadena JSON.
     */
    @TypeConverter
    fun fromIngredientList(value: List<RecipeIngredient>): String = gson.toJson(value)

    /**
     * Convierte una cadena JSON de vuelta a una lista de ingredientes.
     */
    @TypeConverter
    fun toIngredientList(value: String): List<RecipeIngredient> {
        val listType = object : TypeToken<List<RecipeIngredient>>() {}.type
        return gson.fromJson(value, listType)
    }

    /**
     * Convierte una lista de pasos a una cadena JSON.
     */
    @TypeConverter
    fun fromStepList(value: List<Step>): String = gson.toJson(value)

    /**
     * Convierte una cadena JSON de vuelta a una lista de pasos.
     */
    @TypeConverter
    fun toStepList(value: String): List<Step> {
        val listType = object : TypeToken<List<Step>>() {}.type
        return gson.fromJson(value, listType)
    }

    /**
     * Convierte una lista de categorías a una cadena JSON.
     */
    @TypeConverter
    fun fromCategoryList(value: List<RecipeCategory>): String = gson.toJson(value)

    /**
     * Convierte una cadena JSON de vuelta a una lista de categorías.
     */
    @TypeConverter
    fun toCategoryList(value: String): List<RecipeCategory> {
        val listType = object : TypeToken<List<RecipeCategory>>() {}.type
        return gson.fromJson(value, listType)
    }

    /**
     * Convierte una lista de alérgenos a una cadena JSON.
     */
    @TypeConverter
    fun fromAllergenList(value: List<Allergen>?): String? = value?.let { gson.toJson(it) }

    /**
     * Convierte una cadena JSON de vuelta a una lista de alérgenos.
     */
    @TypeConverter
    fun toAllergenList(value: String?): List<Allergen>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Allergen>>() {}.type
        return gson.fromJson(value, listType)
    }

    /**
     * Convierte una lista de restricciones a una cadena JSON.
     */
    @TypeConverter
    fun fromRestrictionsList(value: List<Restrictions>?): String? = value?.let { gson.toJson(it) }

    /**
     * Convierte una cadena JSON de vuelta a una lista de restricciones.
     */
    @TypeConverter
    fun toRestrictionsList(value: String?): List<Restrictions>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Restrictions>>() {}.type
        return gson.fromJson(value, listType)
    }
}
