package com.example.recetasapp.ui

import android.app.Activity
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.recetasapp.R
import com.example.recetasapp.model.Recipe
import com.bumptech.glide.Glide
import com.google.android.material.chip.ChipGroup
import java.util.Locale

class RecipeAdapter(
    private var _recipes: List<Recipe>,
    private var favoriteRecipeIds: Set<String> = emptySet(),
    private val onFavoriteClick: (Recipe) -> Unit,
    private val onClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    val recipes: List<Recipe>
        get() = _recipes

    var longClickedPosition: Int = -1
        private set

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        val image: ImageView = view.findViewById(R.id.ivRecipeImage)
        val name: TextView = view.findViewById(R.id.tvRecipeName)
        val creator: TextView = view.findViewById(R.id.tvRecipeCreator)
        val description: TextView = view.findViewById(R.id.tvRecipeDescription)
        val prepTime: TextView = view.findViewById(R.id.tvPrepTime)
        val servings: TextView = view.findViewById(R.id.tvServings)
        val averageRating: TextView = view.findViewById(R.id.tvAverageRating)
        val allergensContainer: ChipGroup = view.findViewById(R.id.llAllergensContainer)
        val btnFavorite: ImageButton = view.findViewById(R.id.btnFavorite)

        init {
            view.setOnCreateContextMenuListener(this)
            view.setOnLongClickListener {
                longClickedPosition = adapterPosition
                false
            }
        }

        override fun onCreateContextMenu(
            menu: ContextMenu,
            v: View,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            (v.context as? Activity)?.menuInflater?.inflate(R.menu.menu_contextual, menu)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = _recipes[position]
        holder.name.text = recipe.name
        
        if (recipe.creatorId != null) {
            holder.creator.visibility = View.VISIBLE
            holder.creator.text = "Por: ${recipe.creatorId}"
        } else {
            holder.creator.visibility = View.GONE
        }

        holder.description.text = recipe.description
        holder.prepTime.text = "${recipe.prepTime} min"
        holder.servings.text = "${recipe.servings} porciones"
        
        if (recipe.ratingCount > 0) {
            holder.averageRating.text = String.format(Locale.getDefault(), "%.1f", recipe.averageRating)
        } else {
            holder.averageRating.text = "-"
        }

        val isFavorite = favoriteRecipeIds.contains(recipe.id)
        holder.btnFavorite.setImageResource(
            if (isFavorite) android.R.drawable.btn_star_big_on 
            else android.R.drawable.btn_star_big_off
        )

        holder.btnFavorite.setOnClickListener {
            onFavoriteClick(recipe)
        }

        holder.allergensContainer.removeAllViews()
        recipe.allergens?.forEach { allergen ->
            val imageView = ImageView(holder.itemView.context).apply {
                val iconSize = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.allergen_icon_size)
                layoutParams = ViewGroup.LayoutParams(iconSize, iconSize)
                setImageResource(allergen.iconResId)
                contentDescription = allergen.displayName
            }
            holder.allergensContainer.addView(imageView)
        }

        Glide.with(holder.itemView.context)
            .load(recipe.image)
            .centerCrop()
            .into(holder.image)

        holder.itemView.setOnClickListener {
            onClick(recipe)
        }
    }

    override fun getItemCount() = _recipes.size

    fun updateRecipes(newRecipes: List<Recipe>, newFavorites: Set<String> = favoriteRecipeIds) {
        val oldRecipes = _recipes
        val oldFavorites = favoriteRecipeIds
        
        _recipes = newRecipes
        favoriteRecipeIds = newFavorites

        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldRecipes.size
            override fun getNewListSize(): Int = newRecipes.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldRecipes[oldItemPosition].id == newRecipes[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldRecipe = oldRecipes[oldItemPosition]
                val newRecipe = newRecipes[newItemPosition]
                val wasFavorite = oldFavorites.contains(oldRecipe.id)
                val isFavorite = newFavorites.contains(newRecipe.id)
                return oldRecipe == newRecipe && wasFavorite == isFavorite
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)
    }
}
