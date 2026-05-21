package com.example.recetasapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.recetasapp.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de Acceso a Datos (DAO) para la entidad [Recipe].
 * Define las operaciones permitidas sobre la tabla de recetas.
 */
@Dao
interface RecipeDao {
    /**
     * Obtiene las recetas visibles para un usuario específico (públicas o creadas por él).
     * @param userId Identificador del usuario.
     * @return Flow con la lista de recetas filtradas.
     */
    @Query("SELECT * FROM recipes WHERE isPublic = 1 OR creatorId = :userId")
    fun getVisibleRecipes(userId: String): Flow<List<Recipe>>

    /**
     * Inserta una receta. Si ya existe, la reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    /**
     * Inserta una lista de recetas.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<Recipe>)

    /**
     * Actualiza una receta existente.
     */
    @Update
    suspend fun updateRecipe(recipe: Recipe)

    /**
     * Elimina una receta de la base de datos.
     */
    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    /**
     * Actualiza la valoración de una receta añadiendo una nueva puntuación.
     * @param recipeId ID de la receta a valorar.
     * @param rating Puntuación otorgada.
     */
    @Query("UPDATE recipes SET ratingSum = ratingSum + :rating, ratingCount = ratingCount + 1 WHERE id = :recipeId")
    suspend fun updateRecipeRating(recipeId: String, rating: Float)
}
