package com.example.recetasapp.utils

import android.content.Context
import com.example.recetasapp.model.Recipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class RecipeManager(private val context: Context) {

    private val fileName = "recipes.json"
    private val gson = Gson()

    fun saveRecipes(recipes: List<Recipe>) {
        val json = gson.toJson(recipes)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    fun loadRecipes(): MutableList<Recipe> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return mutableListOf()

        val json = file.readText()
        val type = object : TypeToken<MutableList<Recipe>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }
}
