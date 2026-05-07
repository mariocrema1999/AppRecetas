package com.example.recetasapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.recetasapp.R
import com.example.recetasapp.data.AppDatabase
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Comprobar si ya hay una sesión iniciada
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val loggedUser = sharedPref.getString("logged_user", null)
        if (loggedUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val etIdentifier = findViewById<EditText>(R.id.etLoginUsername)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        val database = AppDatabase.getDatabase(this)

        btnLogin?.setOnClickListener {
            val identifier = etIdentifier?.text?.toString() ?: ""
            val password = etPassword?.text?.toString() ?: ""

            if (identifier.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // Buscamos al usuario por nombre de usuario o por email
                val user = database.userDao().getUserByUsernameOrEmail(identifier)
                if (user != null && user.password == password) {
                    // Guardamos siempre el username real en las preferencias para mantener la consistencia
                    sharedPref.edit().putString("logged_user", user.username).apply()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Usuario/Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvGoToRegister?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
