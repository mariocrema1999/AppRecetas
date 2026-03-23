package com.example.recetasapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.recetasapp.R
import com.example.recetasapp.model.Recipe
import com.example.recetasapp.model.Step
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class RecipeFormActivity : AppCompatActivity() {

    private val ingredients = mutableListOf<String>()
    private val steps = mutableListOf<Step>()
    private var imageInternalPath: String? = null

    // 1. Registrar el launcher como una propiedad de la clase
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            // 2. Guardar la imagen en el almacenamiento interno y obtener su ruta
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
        supportActionBar?.title = "Nueva Receta"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupInputs()
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

    private fun setupInputs() {
        // ... View bindings ...
        val etName = findViewById<EditText>(R.id.etRecipeName)
        val etDesc = findViewById<EditText>(R.id.etRecipeDescription)
        val etImage = findViewById<Button>(R.id.etRecipeImage)

        // 3. El botón ahora solo lanza el selector de medios
        etImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val etTime = findViewById<EditText>(R.id.etPrepTime)
        val etServings = findViewById<EditText>(R.id.etServings)

        val etIngredient = findViewById<EditText>(R.id.etNewIngredient)
        val btnAddIng = findViewById<ImageButton>(R.id.btnAddIngredient)
        val tvIngPreview = findViewById<TextView>(R.id.tvIngredientsPreview)

        val etStepDesc = findViewById<EditText>(R.id.etStepDesc)
        val etStepTime = findViewById<EditText>(R.id.etStepTime)
        val btnAddStep = findViewById<Button>(R.id.btnAddStep)
        val tvStepPreview = findViewById<TextView>(R.id.tvStepsPreview)

        val btnSave = findViewById<Button>(R.id.btnSaveRecipe)

        // Logic to enable save
        fun validate() {
            btnSave.isEnabled = !TextUtils.isEmpty(etName.text) &&
                                !TextUtils.isEmpty(etDesc.text) &&
                                steps.isNotEmpty()
        }

        btnAddIng.setOnClickListener {
            val text = etIngredient.text.toString()
            if (text.isNotBlank()) {
                ingredients.add(text)
                etIngredient.setText("")
                tvIngPreview.text = ingredients.joinToString("\n") { "• $it" }
            }
        }

        btnAddStep.setOnClickListener {
            val desc = etStepDesc.text.toString()
            val time = etStepTime.text.toString().toIntOrNull()
            if (desc.isNotBlank()) {
                steps.add(Step(desc, time))
                etStepDesc.setText("")
                etStepTime.setText("")
                tvStepPreview.text = steps.mapIndexed { i, s -> "${i+1}. ${s.description}" }.joinToString("\n\n")
                validate()
            }
        }

        btnSave.setOnClickListener {
             val recipe = Recipe(
                 id = UUID.randomUUID().toString(),
                 name = etName.text.toString(),
                 description = etDesc.text.toString(),
                 // 4. Usar la ruta de la imagen guardada
                 image = imageInternalPath ?: "https://via.placeholder.com/150",
                 prepTime = etTime.text.toString().toIntOrNull() ?: 0,
                 servings = etServings.text.toString().toIntOrNull() ?: 1,
                 ingredients = ingredients,
                 steps = steps
             )

             val resultIntent = Intent()
             resultIntent.putExtra("NEW_RECIPE", recipe)
             setResult(RESULT_OK, resultIntent)
             finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
