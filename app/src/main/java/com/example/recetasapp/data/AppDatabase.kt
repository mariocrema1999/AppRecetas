package com.example.recetasapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recetasapp.model.Converters
import com.example.recetasapp.model.Recipe
import com.example.recetasapp.model.User

@Database(entities = [User::class, Recipe::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_database"
                )
                .fallbackToDestructiveMigration() // Esto borrará los datos antiguos al cambiar la versión, evitando el crash
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
