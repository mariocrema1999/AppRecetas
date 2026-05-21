package com.example.recetasapp.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.recetasapp.R
import com.example.recetasapp.data.AppDatabase
import com.example.recetasapp.model.Recipe
import com.example.recetasapp.model.Step
import com.example.recetasapp.utils.AudioManager
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Actividad que muestra los detalles de una receta seleccionada.
 * Incluye ingredientes, pasos interactivos con temporizadores, y sistema de valoración.
 */
class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var audioManager: AudioManager
    private lateinit var stepsContainer: LinearLayout
    private lateinit var database: AppDatabase
    private lateinit var currentRecipe: Recipe
    
    private var completedStepsCount = 0
    private var totalStepsCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        audioManager = AudioManager(this)
        database = AppDatabase.getDatabase(this)

        val recipe = intent.getParcelableExtra<Recipe>("RECIPE")

        if (recipe != null) {
            currentRecipe = recipe
            setupUI(recipe)
        } else {
            finish()
        }
    }

    /**
     * Configura la interfaz de usuario con los datos de la receta.
     * @param recipe Objeto receta a mostrar.
     */
    private fun setupUI(recipe: Recipe) {
        supportActionBar?.title = recipe.name

        findViewById<TextView>(R.id.tvDetailDescription).text = recipe.description
        findViewById<TextView>(R.id.tvDetailTime).text = "${recipe.prepTime} min"
        findViewById<TextView>(R.id.tvDetailServings).text = "${recipe.servings}"
        
        val tvRating = findViewById<TextView>(R.id.tvDetailRatingText)
        if (recipe.ratingCount > 0) {
            tvRating.text = String.format(Locale.getDefault(), "%.1f", recipe.averageRating)
        } else {
            tvRating.text = "-"
        }

        // Alérgenos
        val allergensContainer = findViewById<LinearLayout>(R.id.llDetailAllergensContainer)
        recipe.allergens?.forEach { allergen ->
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.allergen_icon_size),
                    resources.getDimensionPixelSize(R.dimen.allergen_icon_size)
                ).apply {
                    setMargins(0, 0, resources.getDimensionPixelSize(R.dimen.allergen_icon_margin), 0)
                }
                setImageResource(allergen.iconResId)
                contentDescription = allergen.displayName
            }
            allergensContainer.addView(imageView)
        }

        // Ingredientes
        val ingredientsText = recipe.ingredients.joinToString("\n") { ingredient ->
            if (ingredient.quantity.isNullOrBlank()) {
                "• ${ingredient.name}"
            } else {
                "• ${ingredient.name}: ${ingredient.quantity}"
            }
        }
        findViewById<TextView>(R.id.tvIngredientsList).text = ingredientsText

        // Imagen
        Glide.with(this).load(recipe.image).into(findViewById(R.id.ivDetailImage))

        // Pasos
        stepsContainer = findViewById(R.id.stepsContainer)
        totalStepsCount = recipe.steps.size
        completedStepsCount = 0
        
        recipe.steps.forEachIndexed { index, step ->
            addStepView(index + 1, step)
        }

        // Lógica de valoración
        val cvRatingAction = findViewById<MaterialCardView>(R.id.cvRatingAction)
        val rbRecipeRating = findViewById<RatingBar>(R.id.rbRecipeRating)
        val btnSaveRating = findViewById<Button>(R.id.btnSaveRating)

        btnSaveRating.setOnClickListener {
            val rating = rbRecipeRating.rating
            if (rating > 0) {
                lifecycleScope.launch {
                    database.recipeDao().updateRecipeRating(recipe.id, rating)
                    Toast.makeText(this@RecipeDetailActivity, "¡Gracias por valorar!", Toast.LENGTH_SHORT).show()
                    cvRatingAction.visibility = View.GONE
                    
                    // Actualización local del texto de valoración
                    val newCount = recipe.ratingCount + 1
                    val newSum = recipe.ratingSum + rating
                    tvRating.text = String.format(Locale.getDefault(), "%.1f", newSum / newCount)
                }
            } else {
                Toast.makeText(this, "Por favor, selecciona una puntuación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Verifica si se han completado todos los pasos para mostrar la sección de valoración
     * y reproducir el sonido de celebración.
     */
    private fun checkAllStepsCompleted() {
        val cvRatingAction = findViewById<MaterialCardView>(R.id.cvRatingAction)
        if (completedStepsCount == totalStepsCount && totalStepsCount > 0) {
            cvRatingAction.visibility = View.VISIBLE
            audioManager.playCelebration()
        } else {
            cvRatingAction.visibility = View.GONE
        }
    }

    /**
     * Añade una vista de paso a la lista de instrucciones.
     * @param number Número del paso.
     * @param step Datos del paso.
     */
    private fun addStepView(number: Int, step: Step) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_step, stepsContainer, false)
        val checkbox = view.findViewById<CheckBox>(R.id.tvStepCheckbox)
        val internalContainer = view.findViewById<LinearLayout>(R.id.llStepInternalContainer)
        val internalTimerContainer = view.findViewById<LinearLayout>(R.id.timerContainer)

        val originalColor = ContextCompat.getColor(this, R.color.menuColor)
        val finishedColor = Color.parseColor("#BDBDBD")

        fun updateStepStyle(isFinished: Boolean) {
            val color = if (isFinished) finishedColor else originalColor
            internalContainer?.setBackgroundColor(color)
            internalTimerContainer?.backgroundTintList = null
            internalTimerContainer?.setBackgroundColor(color)
        }

        checkbox.isChecked = false
        updateStepStyle(false)
        view.findViewById<TextView>(R.id.tvStepTitle).text = "Paso $number"
        view.findViewById<TextView>(R.id.tvStepDescription).text = step.description

        val onCheckLogic: (Boolean) -> Unit = { isChecked: Boolean ->
            updateStepStyle(isChecked)
            if (isChecked) {
                completedStepsCount++
            } else {
                completedStepsCount--
            }
            checkAllStepsCompleted()
        }

        if (step.timeMinutes != null && step.timeMinutes > 0) {
            internalTimerContainer?.visibility = View.VISIBLE
            setupTimer(view, step.timeMinutes * 60L, onCheckLogic)
        } else {
            internalTimerContainer?.visibility = View.GONE
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                onCheckLogic(isChecked)
            }
        }

        view.setOnClickListener {
            checkbox.isChecked = !checkbox.isChecked
        }

        stepsContainer.addView(view)
    }

    /**
     * Configura el temporizador para un paso que tiene tiempo definido.
     * @param view Vista del paso.
     * @param totalSeconds Segundos totales del temporizador.
     * @param onCheckLogic Callback para manejar el estado de completado.
     */
    private fun setupTimer(view: View, totalSeconds: Long, onCheckLogic: (Boolean) -> Unit) {
        val tvTimer = view.findViewById<TextView>(R.id.tvTimer)
        val btnToggle = view.findViewById<ImageButton>(R.id.btnTimerToggle)
        val btnReset = view.findViewById<ImageButton>(R.id.btnTimerReset)
        val checkbox = view.findViewById<CheckBox>(R.id.tvStepCheckbox)

        var timeLeft = totalSeconds
        var isRunning = false
        var timer: CountDownTimer? = null

        fun stopTimer(pauseAudio: Boolean = true, hardStopAudio: Boolean = false) {
            timer?.cancel()
            isRunning = false
            btnToggle.setImageResource(android.R.drawable.ic_media_play)
            if (hardStopAudio) {
                audioManager.hardStop()
            } else if (pauseAudio) {
                audioManager.pause()
            }
        }

        fun updateText() {
            val m = timeLeft / 60
            val s = timeLeft % 60
            tvTimer.text = "%02d:%02d".format(m, s)
        }

        updateText()

        btnToggle.setOnClickListener {
            if (isRunning) {
                audioManager.stop()
                stopTimer()
            } else {
                audioManager.playRandomAudio()
                checkbox.isChecked = false
                isRunning = true
                btnToggle.setImageResource(android.R.drawable.ic_media_pause)

                timer = object : CountDownTimer(timeLeft * 1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        timeLeft = millisUntilFinished / 1000
                        updateText()
                    }

                    override fun onFinish() {
                        timeLeft = 0
                        updateText()
                        isRunning = false
                        checkbox.isChecked = true
                        btnToggle.setImageResource(android.R.drawable.ic_media_play)
                    }
                }.start()
            }
        }

        checkbox.setOnCheckedChangeListener { _, isChecked ->
            onCheckLogic(isChecked)
            if (isChecked && isRunning) {
                val shouldPauseAudio = completedStepsCount < totalStepsCount
                stopTimer(pauseAudio = shouldPauseAudio)
            }
        }

        btnReset.setOnClickListener {
            stopTimer(hardStopAudio = true)
            timeLeft = totalSeconds
            updateText()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.hardStop()
    }
}
