package com.example.recetasapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recetasapp.model.User

/**
 * Interfaz de Acceso a Datos (DAO) para la entidad [User].
 * Define las operaciones permitidas sobre la tabla de usuarios.
 */
@Dao
interface UserDao {
    /**
     * Busca un usuario por su nombre de usuario.
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    /**
     * Busca un usuario por su correo electrónico.
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    /**
     * Busca un usuario que coincida con el identificador proporcionado (ya sea nombre de usuario o email).
     */
    @Query("SELECT * FROM users WHERE username = :identifier OR email = :identifier LIMIT 1")
    suspend fun getUserByUsernameOrEmail(identifier: String): User?

    /**
     * Registra un nuevo usuario en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: User)

    /**
     * Actualiza campos específicos del usuario basándose en su nombre de usuario antiguo.
     */
    @Query("UPDATE users SET username = :newUsername, password = :newPassword, displayName = :newName WHERE username = :oldUsername")
    suspend fun updateUserInfo(oldUsername: String, newUsername: String, newPassword: String, newName: String)
}
