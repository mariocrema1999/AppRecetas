package com.example.recetasapp.data

import androidx.room.*
import com.example.recetasapp.model.Favorite
import com.example.recetasapp.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de Acceso a Datos (DAO) para la entidad [Favorite].
 * Gestiona la relación de recetas favoritas de los usuarios.
 */
@Dao
interface FavoriteDao {
    /**
     * Añade una receta a la lista de favoritos de un usuario.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: Favorite)

    /**
     * Elimina una receta de la lista de favoritos de un usuario.
     */
    @Delete
    suspend fun removeFavorite(favorite: Favorite)

    /**
     * Obtiene los IDs de todas las recetas marcadas como favoritas por un usuario.
     * @param userId ID del usuario.
     * @return Flow con la lista de IDs de recetas.
     */
    @Query("SELECT recipeId FROM user_favorites WHERE userId = :userId")
    fun getFavoriteRecipeIds(userId: String): Flow<List<String>>

    /**
     * Obtiene los objetos [Recipe] completos que un usuario ha marcado como favoritos.
     * @param userId ID del usuario.
     * @return Flow con la lista de objetos Recipe.
     */
    @Query("SELECT * FROM recipes INNER JOIN user_favorites ON recipes.id = user_favorites.recipeId WHERE user_favorites.userId = :userId")
    fun getFavoriteRecipes(userId: String): Flow<List<Recipe>>
}
