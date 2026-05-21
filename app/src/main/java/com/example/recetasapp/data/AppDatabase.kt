package com.example.recetasapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recetasapp.model.Converters
import com.example.recetasapp.model.Recipe
import com.example.recetasapp.model.User
import com.example.recetasapp.model.Favorite

/**
 * Base de datos principal de la aplicación utilizando Room.
 * Define las entidades y los convertidores de tipos necesarios.
 */
@Database(entities = [User::class, Recipe::class, Favorite::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Proporciona acceso a las operaciones de la tabla de usuarios.
     */
    abstract fun userDao(): UserDao

    /**
     * Proporciona acceso a las operaciones de la tabla de recetas.
     */
    abstract fun recipeDao(): RecipeDao

    /**
     * Proporciona acceso a las operaciones de la tabla de favoritos.
     */
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia única (Singleton) de la base de datos.
         *
         * @param context Contexto de la aplicación.
         * @return Instancia de [AppDatabase].
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
