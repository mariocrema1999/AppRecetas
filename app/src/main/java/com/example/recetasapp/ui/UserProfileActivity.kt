package com.example.recetasapp.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.recetasapp.R
import com.example.recetasapp.model.Allergen
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class UserProfileActivity : AppCompatActivity() {
    
    private lateinit var cgAllergens: ChipGroup
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

        cgAllergens = findViewById(R.id.cgAllergens)
        setupAllergenChips()
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
