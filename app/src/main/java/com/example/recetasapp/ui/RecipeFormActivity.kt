package com.example.recetasapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.recetasapp.R
import com.example.recetasapp.model.Allergen
import com.example.recetasapp.model.Recipe
import com.example.recetasapp.model.RecipeCategory
import com.example.recetasapp.model.RecipeIngredient
import com.example.recetasapp.model.Step
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class RecipeFormActivity : AppCompatActivity() {

    private val ingredients = mutableListOf<RecipeIngredient>()
    private val steps = mutableListOf<Step>()
    private var imageInternalPath: String? = null
    private var editingRecipeId: String? = null

    private lateinit var llIngredientsContainer: LinearLayout
    private lateinit var llStepsContainer: LinearLayout
    private lateinit var btnSave: Button
    private lateinit var etName: EditText
    private lateinit var etDesc: EditText
    private lateinit var etTime: EditText
    private lateinit var etServings: EditText

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            imageInternalPath = saveImageToInternalStorage(uri)
            findViewById<Button>(R.id.etRecipeImage).text = "Imagen seleccionada"
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_form)
        setSupportActionBar(findViewById(R.id.toolbar))
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        llIngredientsContainer = findViewById(R.id.llIngredientsContainer)
        llStepsContainer = findViewById(R.id.llStepsContainer)
        btnSave = findViewById(R.id.btnSaveRecipe)
        etName = findViewById(R.id.etRecipeName)
        etDesc = findViewById(R.id.etRecipeDescription)
        etTime = findViewById(R.id.etPrepTime)
        etServings = findViewById(R.id.etServings)

        setupInputs()
        setupAllergenChips()

        // Verificar si estamos editando
        val recipeToEdit = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EDIT_RECIPE", Recipe::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Recipe>("EDIT_RECIPE")
        }

        if (recipeToEdit != null) {
            fillFormForEdit(recipeToEdit)
        } else {
            supportActionBar?.title = "Nueva Receta"
        }
    }

    private fun fillFormForEdit(recipe: Recipe) {
        supportActionBar?.title = "Editar Receta"
        editingRecipeId = recipe.id
        etName.setText(recipe.name)
        etDesc.setText(recipe.description)
        etTime.setText(recipe.prepTime.toString())
        etServings.setText(recipe.servings.toString())
        
        if (recipe.image.startsWith("/")) {
            imageInternalPath = recipe.image
            findViewById<Button>(R.id.etRecipeImage).text = "Imagen seleccionada"
        }

        // Categorías
        recipe.categories.forEach { category ->
            val chipId = when (category) {
                RecipeCategory.CARNE -> R.id.chipCarne
                RecipeCategory.PESCADO -> R.id.chipPescado
                RecipeCategory.ARROCES -> R.id.chipArroces
                RecipeCategory.VERDURAS_HORTALIZAS -> R.id.chipVerduras
                RecipeCategory.LEGUMBRES -> R.id.chipLegumbres
                RecipeCategory.HUEVOS -> R.id.chipHuevos
                RecipeCategory.PASTAS -> R.id.chipPastas
                RecipeCategory.SOPAS_CREMAS -> R.id.chipSopas
                RecipeCategory.CELIACOS -> R.id.chipCeliacos
                RecipeCategory.DIABETICOS -> R.id.chipDiabeticos
                RecipeCategory.POSTRES -> R.id.chipPostres
            }
            findViewById<Chip>(chipId).isChecked = true
        }

        // Alérgenos
        val cgAllergens = findViewById<ChipGroup>(R.id.cgAllergens)
        recipe.allergens?.forEach { allergen ->
            for (i in 0 until cgAllergens.childCount) {
                val chip = cgAllergens.getChildAt(i) as Chip
                if (chip.tag == allergen) {
                    chip.isChecked = true
                }
            }
        }

        ingredients.addAll(recipe.ingredients)
        refreshIngredientsUI()
        
        steps.addAll(recipe.steps)
        refreshStepsUI()
        
        validate()
    }

    private fun setupAllergenChips() {
        val cgAllergens = findViewById<ChipGroup>(R.id.cgAllergens)
        Allergen.values().forEach { allergen ->
            val chip = Chip(this).apply {
                text = allergen.displayName
                isCheckable = true
                tag = allergen
                setChipIconResource(allergen.iconResId)
                isChipIconVisible = true
            }
            cgAllergens.addView(chip)
        }
    }

    private fun minPreparationTime(steps: List<Step>): Int {
        var minTime = 0
        for(step in steps) {
            if(step.timeMinutes != null){
                minTime += step.timeMinutes
            }
        }
        return minTime
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = "recipe_img_${UUID.randomUUID()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("SaveImage", "Image saved to: ${file.absolutePath}")
            return file.absolutePath
        } catch (e: IOException) {
            Log.e("SaveImage", "Error saving image", e)
            return null
        }
    }

    private fun validate() {
        btnSave.isEnabled = !TextUtils.isEmpty(etName.text) &&
                            !TextUtils.isEmpty(etDesc.text) &&
                            steps.isNotEmpty()
    }

    private fun refreshIngredientsUI() {
        llIngredientsContainer.removeAllViews()
        ingredients.forEachIndexed { index, ingredient ->
            val view = LayoutInflater.from(this).inflate(R.layout.item_ingredient_removable, llIngredientsContainer, false)
            val tvContent = view.findViewById<TextView>(R.id.tvIngredientContent)
            val btnRemove = view.findViewById<ImageButton>(R.id.btnRemoveIngredient)

            tvContent.text = if (ingredient.quantity.isNullOrBlank()) "• ${ingredient.name}" else "• ${ingredient.name}: ${ingredient.quantity}"
            
            btnRemove.setOnClickListener {
                ingredients.removeAt(index)
                refreshIngredientsUI()
                validate()
            }
            llIngredientsContainer.addView(view)
        }
    }

    private fun refreshStepsUI() {
        llStepsContainer.removeAllViews()
        steps.forEachIndexed { index, step ->
            val view = LayoutInflater.from(this).inflate(R.layout.item_step_removable, llStepsContainer, false)
            val tvContent = view.findViewById<TextView>(R.id.tvStepContent)
            val btnRemove = view.findViewById<ImageButton>(R.id.btnRemoveStep)

            val timeStr = if (step.timeMinutes != null) " (${step.timeMinutes} min)" else ""
            tvContent.text = "${index + 1}. ${step.description}$timeStr"
            
            btnRemove.setOnClickListener {
                steps.removeAt(index)
                refreshStepsUI()
                validate()
            }
            llStepsContainer.addView(view)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupInputs() {
        val etImage = findViewById<Button>(R.id.etRecipeImage)
        etImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val etIngredientQuantity = findViewById<EditText>(R.id.etIngredientQuantity)
        val etIngredient = findViewById<EditText>(R.id.etNewIngredient)
        val btnAddIng = findViewById<ImageButton>(R.id.btnAddIngredient)

        val etStepDesc = findViewById<EditText>(R.id.etStepDesc)
        val etStepTime = findViewById<EditText>(R.id.etStepTime)
        val btnAddStep = findViewById<Button>(R.id.btnAddStep)

        val cgAllergens = findViewById<ChipGroup>(R.id.cgAllergens)

        btnAddIng.setOnClickListener {
            val text = etIngredient.text.toString()
            val quantity = etIngredientQuantity.text.toString()
            if (text.isNotBlank()) {
                ingredients.add(RecipeIngredient(text, quantity))
                etIngredient.setText("")
                etIngredientQuantity.setText("")
                refreshIngredientsUI()
                validate()
            }
        }

        btnAddStep.setOnClickListener {
            val desc = etStepDesc.text.toString()
            val time = etStepTime.text.toString().toIntOrNull()
            if (desc.isNotBlank()) {
                steps.add(Step(desc, time))
                etStepDesc.setText("")
                etStepTime.setText("")
                refreshStepsUI()
                validate()
            }
        }

        btnSave.setOnClickListener {
            val minTime = minPreparationTime(steps)
            val prepTime = etTime.text.toString().toIntOrNull() ?: 0

            if (minTime <= prepTime) {
                val selectedCategories = mutableListOf<RecipeCategory>()
                if (findViewById<Chip>(R.id.chipCarne).isChecked) selectedCategories.add(RecipeCategory.CARNE)
                if (findViewById<Chip>(R.id.chipPescado).isChecked) selectedCategories.add(RecipeCategory.PESCADO)
                if (findViewById<Chip>(R.id.chipArroces).isChecked) selectedCategories.add(RecipeCategory.ARROCES)
                if (findViewById<Chip>(R.id.chipVerduras).isChecked) selectedCategories.add(RecipeCategory.VERDURAS_HORTALIZAS)
                if (findViewById<Chip>(R.id.chipLegumbres).isChecked) selectedCategories.add(RecipeCategory.LEGUMBRES)
                if (findViewById<Chip>(R.id.chipHuevos).isChecked) selectedCategories.add(RecipeCategory.HUEVOS)
                if (findViewById<Chip>(R.id.chipPastas).isChecked) selectedCategories.add(RecipeCategory.PASTAS)
                if (findViewById<Chip>(R.id.chipSopas).isChecked) selectedCategories.add(RecipeCategory.SOPAS_CREMAS)
                if (findViewById<Chip>(R.id.chipCeliacos).isChecked) selectedCategories.add(RecipeCategory.CELIACOS)
                if (findViewById<Chip>(R.id.chipDiabeticos).isChecked) selectedCategories.add(RecipeCategory.DIABETICOS)
                if (findViewById<Chip>(R.id.chipPostres).isChecked) selectedCategories.add(RecipeCategory.POSTRES)

                val selectedAllergens = mutableListOf<Allergen>()
                for (i in 0 until cgAllergens.childCount) {
                    val chip = cgAllergens.getChildAt(i) as Chip
                    if (chip.isChecked) {
                        selectedAllergens.add(chip.tag as Allergen)
                    }
                }

                // Si estamos editando y no hemos cambiado la imagen, mantenemos la que tenía
                val defaultImagePath = "android.resource://${packageName}/drawable/fotopredeterminada"
                
                // Buscamos la imagen original si estamos editando
                var finalImage = imageInternalPath ?: defaultImagePath
                
                // Si estamos editando y no seleccionamos imagen nueva, pero ya tenía una URL o path
                val recipeToEdit = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("EDIT_RECIPE", Recipe::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra<Recipe>("EDIT_RECIPE")
                }
                
                if (imageInternalPath == null && recipeToEdit != null) {
                    finalImage = recipeToEdit.image
                }

                val recipe = Recipe(
                    id = editingRecipeId ?: UUID.randomUUID().toString(),
                    name = etName.text.toString(),
                    description = etDesc.text.toString(),
                    image = finalImage,
                    prepTime = prepTime,
                    servings = etServings.text.toString().toIntOrNull() ?: 1,
                    ingredients = ingredients,
                    steps = steps,
                    categories = selectedCategories,
                    allergens = selectedAllergens
                )

                val resultIntent = Intent()
                resultIntent.putExtra("NEW_RECIPE", recipe)
                resultIntent.putExtra("IS_EDIT", editingRecipeId != null)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                etTime.error = "Tiempo de preparación mínimo: $minTime minutos"
                Toast.makeText(this, "El tiempo de los pasos ($minTime min) supera al tiempo total de la receta ($prepTime min)", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
