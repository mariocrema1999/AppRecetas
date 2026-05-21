package com.example.recetasapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Entidad que representa la relación de "favorito" entre un usuario y una receta.
 * Implementa una relación muchos-a-muchos simplificada.
 *
 * @property userId Identificador del usuario que marca la receta como favorita.
 * @property recipeId Identificador de la receta marcada como favorita.
 */
@Entity(
    tableName = "user_favorites",
    primaryKeys = ["userId", "recipeId"],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipeId")]
)
data class Favorite(
    val userId: String,
    val recipeId: String
)
