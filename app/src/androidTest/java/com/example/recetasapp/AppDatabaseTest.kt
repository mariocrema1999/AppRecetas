package com.example.recetasapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.recetasapp.data.AppDatabase
import com.example.recetasapp.data.RecipeDao
import com.example.recetasapp.data.UserDao
import com.example.recetasapp.model.Recipe
import com.example.recetasapp.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var userDao: UserDao
    private lateinit var recipeDao: RecipeDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Usamos una base de datos en memoria para que se borre tras el test
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        userDao = db.userDao()
        recipeDao = db.recipeDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runBlocking {
        val user = User("paco123", "password", "paco@mail.com", "Paco")
        userDao.registerUser(user)
        val byName = userDao.getUserByUsername("paco123")
        assertEquals(byName?.displayName, "Paco")
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetVisibleRecipes() = runBlocking {
        val recipe = Recipe(
            id = "test_id",
            name = "Receta de Test",
            description = "Desc",
            image = "",
            prepTime = 10,
            servings = 1,
            ingredients = emptyList(),
            steps = emptyList(),
            creatorId = "paco123",
            isPublic = true
        )
        recipeDao.insertRecipe(recipe)
        
        // Comprobamos que la receta es visible para el usuario "paco123"
        val recipes = recipeDao.getVisibleRecipes("paco123").first()
        assertEquals(recipes.size, 1)
        assertEquals(recipes[0].name, "Receta de Test")
    }
}
