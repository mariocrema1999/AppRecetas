package com.example.recetasapp.ui

import android.app.Activity
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.recetasapp.R
import com.example.recetasapp.model.Recipe
import com.bumptech.glide.Glide

class RecipeAdapter(
    private var recipes: List<Recipe>,
    private val onClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    var longClickedPosition: Int = -1
        private set

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        val image: ImageView = view.findViewById(R.id.ivRecipeImage)
        val name: TextView = view.findViewById(R.id.tvRecipeName)
        val description: TextView = view.findViewById(R.id.tvRecipeDescription)
        val prepTime: TextView = view.findViewById(R.id.tvPrepTime)
        val servings: TextView = view.findViewById(R.id.tvServings)

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
        val recipe = recipes[position]
        holder.name.text = recipe.name
        holder.description.text = recipe.description
        holder.prepTime.text = "${recipe.prepTime} min"
        holder.servings.text = "${recipe.servings} porciones"

        Glide.with(holder.itemView.context)
            .load(recipe.image)
            .centerCrop()
            .into(holder.image)

        holder.itemView.setOnClickListener {
            onClick(recipe)
        }
    }

    override fun getItemCount() = recipes.size

    fun updateRecipes(newRecipes: List<Recipe>) {
        val diffCallback = RecipeDiffCallback(recipes, newRecipes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        
        recipes = newRecipes
        diffResult.dispatchUpdatesTo(this)
    }

    class RecipeDiffCallback(
        private val oldList: List<Recipe>,
        private val newList: List<Recipe>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
