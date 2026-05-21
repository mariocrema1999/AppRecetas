package com.example.recetasapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa a un usuario en la base de datos.
 *
 * @property username Nombre de usuario único que actúa como clave primaria.
 * @property password Contraseña del usuario.
 * @property email Correo electrónico del usuario.
 * @property displayName Nombre que se mostrará en la interfaz de usuario.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val password: String,
    val email: String,
    val displayName: String
)
