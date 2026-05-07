package com.example.recetasapp.data

import androidx.room.*
import com.example.recetasapp.model.Favorite
import com.example.recetasapp.model.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: Favorite)

    @Delete
    suspend fun removeFavorite(favorite: Favorite)

    @Query("SELECT recipeId FROM user_favorites WHERE userId = :userId")
    fun getFavoriteRecipeIds(userId: String): Flow<List<String>>

    @Query("SELECT * FROM recipes INNER JOIN user_favorites ON recipes.id = user_favorites.recipeId WHERE user_favorites.userId = :userId")
    fun getFavoriteRecipes(userId: String): Flow<List<Recipe>>
}
