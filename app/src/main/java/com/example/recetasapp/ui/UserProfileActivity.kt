package com.example.recetasapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.recetasapp.R
import com.example.recetasapp.data.AppDatabase
import com.example.recetasapp.model.Allergen
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class UserProfileActivity : AppCompatActivity() {
    
    private lateinit var cgAllergens: ChipGroup
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var logoutButton: Button
    
    private val PREFS_NAME = "user_prefs"
    private val KEY_ALLERGENS = "selected_allergens"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)
        
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Perfil de Usuario"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        profileName = findViewById(R.id.profileName)
        profileEmail = findViewById(R.id.profileEmail)
        cgAllergens = findViewById(R.id.cgAllergens)
        logoutButton = findViewById(R.id.logoutButton)

        loadUserData()
        setupAllergenChips()
        
        logoutButton.setOnClickListener {
            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadUserData() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("logged_user", null)
        
        if (username != null) {
            val database = AppDatabase.getDatabase(this)
            lifecycleScope.launch {
                val user = database.userDao().getUserByUsername(username)
                if (user != null) {
                    profileName.text = user.displayName
                    profileEmail.text = user.email
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

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
}
