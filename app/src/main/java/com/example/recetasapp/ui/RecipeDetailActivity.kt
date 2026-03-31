package com.example.recetasapp.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.recetasapp.R
import com.example.recetasapp.model.Recipe
import com.example.recetasapp.model.Step
import com.example.recetasapp.utils.AudioManager
import com.bumptech.glide.Glide

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var audioManager: AudioManager
    private lateinit var stepsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        audioManager = AudioManager(this)
        audioManager.playWelcome()

        val recipe = intent.getParcelableExtra<Recipe>("RECIPE")

        if (recipe != null) {
            setupUI(recipe)
        } else {
            finish()
        }
    }

    private fun setupUI(recipe: Recipe) {
        supportActionBar?.title = recipe.name

        findViewById<TextView>(R.id.tvDetailDescription).text = recipe.description
        findViewById<TextView>(R.id.tvDetailTime).text = "${recipe.prepTime} min"
        findViewById<TextView>(R.id.tvDetailServings).text = "${recipe.servings}"

        // Allergens
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

        // Ingredients
        val ingredientsText = recipe.ingredients.joinToString("\n") { ingredient ->
            if (ingredient.quantity.isNullOrBlank()) {
                "• ${ingredient.name}"
            } else {
                "• ${ingredient.name}: ${ingredient.quantity}"
            }
        }
        findViewById<TextView>(R.id.tvIngredientsList).text = ingredientsText

        // Image
        Glide.with(this).load(recipe.image).into(findViewById(R.id.ivDetailImage))

        // Steps
        stepsContainer = findViewById(R.id.stepsContainer)
        recipe.steps.forEachIndexed { index, step ->
            addStepView(index + 1, step)
        }
    }

    private fun addStepView(number: Int, step: Step) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_step, stepsContainer, false)
        val checkbox = view.findViewById<CheckBox>(R.id.tvStepCheckbox)

        checkbox.isChecked = false
        view.findViewById<TextView>(R.id.tvStepTitle).text = "Paso $number"
        view.findViewById<TextView>(R.id.tvStepDescription).text = step.description

        // Hacer que al pulsar en el CardView cambie el estado del checkbox
        view.setOnClickListener {
            checkbox.isChecked = !checkbox.isChecked
        }

        val timerContainer = view.findViewById<LinearLayout>(R.id.timerContainer)
        if (step.timeMinutes != null && step.timeMinutes > 0) {
            timerContainer.visibility = View.VISIBLE
            setupTimer(view, step.timeMinutes * 60L)
        } else {
            timerContainer.visibility = View.GONE
        }

        stepsContainer.addView(view)
    }

    private fun setupTimer(view: View, totalSeconds: Long) {
        val tvTimer = view.findViewById<TextView>(R.id.tvTimer)
        val btnToggle = view.findViewById<ImageButton>(R.id.btnTimerToggle)
        val btnReset = view.findViewById<ImageButton>(R.id.btnTimerReset)
        val checkbox = view.findViewById<CheckBox>(R.id.tvStepCheckbox)

        var timeLeft = totalSeconds
        var isRunning = false
        var timer: CountDownTimer? = null

        fun stopTimer() {
            timer?.cancel()
            isRunning = false
            btnToggle.setImageResource(android.R.drawable.ic_media_play)
            audioManager.stop()
        }

        fun updateText() {
            val m = timeLeft / 60
            val s = timeLeft % 60
            tvTimer.text = "%02d:%02d".format(m, s)
        }

        updateText()

        btnToggle.setOnClickListener {
            if (isRunning) {
                stopTimer()
            } else {
                // Si se vuelve a retomar el tiempo dandole al play que se desmarque el checkbox
                checkbox.isChecked = false
                
                isRunning = true
                btnToggle.setImageResource(android.R.drawable.ic_media_pause)

                timer = object : CountDownTimer(timeLeft * 1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        timeLeft = millisUntilFinished / 1000
                        updateText()
                        if (timeLeft > 0 && timeLeft % 8 == 0L) {
                            audioManager.playRandomJoke()
                        }
                    }

                    override fun onFinish() {
                        timeLeft = 0
                        checkbox.isChecked = true
                        updateText()
                        isRunning = false
                        btnToggle.setImageResource(android.R.drawable.ic_media_play)
                        audioManager.playCelebration()
                    }
                }.start()
            }
        }

        // Si se marca el checkbox manualmente mientras el tiempo corre, se para.
        checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && isRunning) {
                stopTimer()
            }
        }

        btnReset.setOnClickListener {
            stopTimer()
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
        audioManager.stop()
    }
}
