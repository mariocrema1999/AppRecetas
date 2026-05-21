package com.example.recetasapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.recetasapp.R
import com.example.recetasapp.data.AppDatabase
import com.example.recetasapp.model.Allergen
import com.example.recetasapp.model.Restrictions
import com.example.recetasapp.model.User
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Actividad que gestiona el perfil del usuario.
 * Permite ver y editar los datos personales (nombre, usuario, contraseña)
 * y configurar preferencias de alérgenos y restricciones dietéticas.
 */
class UserProfileActivity : AppCompatActivity() {
    
    private lateinit var cgAllergens: ChipGroup
    private lateinit var cgRestrictions: ChipGroup
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var logoutButton: Button
    private lateinit var btnEditProfile: View
    
    private val PREFS_NAME = "user_prefs"

    private val KEY_RESTRICTIONS = "selected_restrictions"
    private val KEY_ALLERGENS = "selected_allergens"
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)
        
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Perfil de Usuario"

        // Usamos el root view si R.id.main no se encuentra para evitar NullPointerException
        val mainView = findViewById<View>(R.id.main) ?: window.decorView.findViewById(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        profileName = findViewById(R.id.profileName)
        profileEmail = findViewById(R.id.profileEmail)
        cgAllergens = findViewById(R.id.cgAllergens)
        cgRestrictions = findViewById(R.id.cgRestrictions)
        logoutButton = findViewById(R.id.logoutButton)
        btnEditProfile = findViewById(R.id.btnEditProfile)

        loadUserData()
        setupAllergenChips()
        setupRestrictionsChips()
        
        logoutButton.setOnClickListener {
            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btnEditProfile.setOnClickListener {
            showEditDialog()
        }
    }

    /**
     * Carga los datos del usuario actual desde la base de datos basándose en el nombre de usuario guardado en sesión.
     */
    private fun loadUserData() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("logged_user", null)
        
        if (username != null) {
            val database = AppDatabase.getDatabase(this)
            lifecycleScope.launch {
                val user = database.userDao().getUserByUsername(username)
                if (user != null) {
                    currentUser = user
                    profileName.text = user.displayName
                    profileEmail.text = user.email
                }
            }
        }
    }

    /**
     * Muestra un diálogo para que el usuario pueda editar su información personal.
     */
    private fun showEditDialog() {
        val user = currentUser ?: return
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etEditName)
        val etUsername = dialogView.findViewById<TextInputEditText>(R.id.etEditUsername)
        val etPassword = dialogView.findViewById<TextInputEditText>(R.id.etEditPassword)

        etName.setText(user.displayName)
        etUsername.setText(user.username)
        etPassword.setText(user.password)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val newName = etName.text.toString().trim()
                val newUsername = etUsername.text.toString().trim()
                val newPassword = etPassword.text.toString()

                if (newName.isBlank() || newUsername.isBlank() || newPassword.isBlank()) {
                    Toast.makeText(this, "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                saveUserData(newUsername, newPassword, newName)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Guarda los cambios del perfil en la base de datos y actualiza la sesión si es necesario.
     *
     * @param newUsername Nuevo nombre de usuario.
     * @param newPassword Nueva contraseña.
     * @param newName Nuevo nombre a mostrar.
     */
    private fun saveUserData(newUsername: String, newPassword: String, newName: String) {
        val oldUsername = currentUser?.username ?: return
        val database = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            try {
                // Si el username cambia, comprobar que el nuevo no existe
                if (newUsername != oldUsername) {
                    val existing = database.userDao().getUserByUsername(newUsername)
                    if (existing != null) {
                        Toast.makeText(this@UserProfileActivity, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }

                withContext(Dispatchers.IO) {
                    database.userDao().updateUserInfo(oldUsername, newUsername, newPassword, newName)
                }

                // Actualizar SharedPreferences si el username cambió
                if (newUsername != oldUsername) {
                    getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("logged_user", newUsername)
                        .apply()
                }

                Toast.makeText(this@UserProfileActivity, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                loadUserData()
            } catch (e: Exception) {
                Toast.makeText(this@UserProfileActivity, "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Configura el ChipGroup de alérgenos cargando el estado guardado en preferencias.
     */
    private fun setupAllergenChips() {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val selectedAllergens = sharedPrefs.getStringSet(KEY_ALLERGENS, emptySet()) ?: emptySet()

        Allergen.values().forEach { allergen ->
            val chip = Chip(this).apply {
                text = allergen.displayName
                isCheckable = true
                tag = allergen
                setChipIconResource(allergen.iconResId)
                isChipIconVisible = true
                isChecked = selectedAllergens.contains(allergen.name)
                
                setOnCheckedChangeListener { _, isChecked ->
                    saveSelectedAllergens()
                }
            }
            cgAllergens.addView(chip)
        }
    }

    /**
     * Guarda la selección actual de alérgenos en SharedPreferences.
     */
    private fun saveSelectedAllergens() {
        val selectedSet = mutableSetOf<String>()
        for (i in 0 until cgAllergens.childCount) {
            val chip = cgAllergens.getChildAt(i) as Chip
            if (chip.isChecked) {
                val allergen = chip.tag as Allergen
                selectedSet.add(allergen.name)
            }
        }
        
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(KEY_ALLERGENS, selectedSet)
            .apply()
    }

    /**
     * Configura el ChipGroup de restricciones cargando el estado guardado en preferencias.
     */
    private fun setupRestrictionsChips() {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val selectedRestrictions = sharedPrefs.getStringSet(KEY_RESTRICTIONS, emptySet()) ?: emptySet()

        Restrictions.values().forEach { restriction ->
            val chip = Chip(this).apply {
                text = restriction.displayName
                isCheckable = true
                tag = restriction
                isChipIconVisible = true
                isChecked = selectedRestrictions.contains(restriction.name)

                setOnCheckedChangeListener { _, isChecked ->
                    saveSelectedRestrictions()
                }
            }
            cgRestrictions.addView(chip)
        }
    }

    /**
     * Guarda la selección actual de restricciones en SharedPreferences.
     */
    private fun saveSelectedRestrictions() {
        val selectedSet = mutableSetOf<String>()
        for (i in 0 until cgRestrictions.childCount) {
            val chip = cgRestrictions.getChildAt(i) as Chip
            if (chip.isChecked) {
                val restriction = chip.tag as Restrictions
                selectedSet.add(restriction.name)
            }
        }

        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(KEY_RESTRICTIONS, selectedSet)
            .apply()
    }
}
