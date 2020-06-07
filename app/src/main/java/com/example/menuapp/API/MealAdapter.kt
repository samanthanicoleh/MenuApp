package com.example.menuapp.API

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.Transliterator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.menuapp.R
import com.example.menuapp.extensions.onResult
import com.example.menuapp.models.Meal
import com.example.menuapp.models.Rating
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.menu_item.*
import kotlinx.android.synthetic.main.menu_item.view.*
import java.math.RoundingMode
import java.math.RoundingMode.valueOf
import java.text.DecimalFormat

typealias TapAction<T> = (T, Int) -> Unit

class MealAdapter(
    private var data: List<Meal>,
    private val tapAction: TapAction<Meal>
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    private var conversionRate: Double = 1.0
    private var ratings: List<Rating> = listOf()

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.menu_item, parent, false)


        return MealViewHolder(view)

    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val model = data[position]
        holder.bind(model, tapAction, conversionRate, ratings)
    }

    fun updateMealData(data: List<Meal>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun updateConversionRate(conversionRate: Double) {
        this.conversionRate = conversionRate
        notifyDataSetChanged()
    }

    fun updateRating(ratings: List<Rating>) {
        this.ratings = ratings
        notifyDataSetChanged()
    }

    class MealViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(
            model: Meal,
            tapAction: TapAction<Meal>,
            conversionRate: Double,
            ratings: List<Rating>
        ) {
            itemView.setOnClickListener {
                tapAction(model, layoutPosition)

            }
            itemView.tvMealTitle.text = model.meal
            itemView.tvMealType.text = model.type // italian, chinese, local, etc.
            // price
            var sign = "€ "
            if (conversionRate != 1.0) {
                sign = "KN "
            }
            val priceRounded = model.price * conversionRate
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            itemView.tvPrice.text = sign + (df.format(priceRounded)).toString()

            val preference = "" + model.preference // vegan, vegetarian, gluten free
            when (preference.toLowerCase()) {
                "vegan" -> itemView.ivFoodPreference.setImageResource(R.drawable.vegan)
                "vegetarian" -> itemView.ivFoodPreference.setImageResource(R.drawable.vegetarian)
                "gluten free" -> itemView.ivFoodPreference.setImageResource(R.drawable.glutenfree)
                else -> itemView.ivFoodPreference.setVisibility(View.GONE)
            }

            // meal image
            if (!model.url.isNullOrEmpty()) {
                // image on recyclerview
                Picasso.get()
                    .load(model.url)
                    .into(itemView.ivFood)
            }
            // Rating
            var rating: Rating = Rating(0, 0, "meal", 0)
            for (tempRating in ratings) {
                if (tempRating.itemId == model.id) {
                    rating = tempRating
                    break
                }
            }
            // Like/dislike buttons
            itemView.likeBtn.setOnClickListener {
                println("clicked like")
                rating.rating = rating.rating + 1
                itemView.tvRating.text = "" + rating.rating

                FoodClient.client.addRating(model.id, "meal", true).onResult(
                    onResponse = { _, apiData -> println(apiData) },
                    onFailure = { println("There has been an error. ${it?.message}") }
                )
            }

            itemView.dislikeBtn.setOnClickListener {
                println("clicked dislike")
                rating.rating = rating.rating - 1
                itemView.tvRating.text = "" + rating.rating

                FoodClient.client.addRating(model.id, "meal", false).onResult(
                    onResponse = { _, apiData -> println(apiData) },
                    onFailure = { println("There has been an error. ${it?.message}") }
                )
            }
            // Show star rating
            itemView.tvRating.text = "" + rating.rating

        }
    }
}