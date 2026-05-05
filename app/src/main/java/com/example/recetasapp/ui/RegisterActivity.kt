package com.example.recetasapp.ui

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.recetasapp.R
import com.example.recetasapp.data.AppDatabase
import com.example.recetasapp.model.User
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.etRegisterName)
        val etUsername = findViewById<EditText>(R.id.etRegisterUsername)
        val etEmail = findViewById<EditText>(R.id.etRegisterEmail)
        val etPassword = findViewById<EditText>(R.id.etRegisterPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        val database = AppDatabase.getDatabase(this)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (name.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Por favor, introduce un correo electrónico válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val existingUserByUsername = database.userDao().getUserByUsername(username)
                val existingUserByEmail = database.userDao().getUserByEmail(email)

                if (existingUserByUsername != null) {
                    Toast.makeText(this@RegisterActivity, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show()
                } else if (existingUserByEmail != null) {
                    Toast.makeText(this@RegisterActivity, "El correo electrónico ya está registrado", Toast.LENGTH_SHORT).show()
                } else {
                    val newUser = User(username, password, email, name)
                    database.userDao().registerUser(newUser)
                    Toast.makeText(this@RegisterActivity, "Registro completado con éxito", Toast.LENGTH_SHORT).show()
                    finish() // Volver al login
                }
            }
        }

        tvGoToLogin.setOnClickListener {
            finish()
        }
    }
}
