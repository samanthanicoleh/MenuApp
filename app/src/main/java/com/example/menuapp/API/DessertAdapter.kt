package com.example.menuapp.API

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.menuapp.R
import com.example.menuapp.extensions.onResult
import com.example.menuapp.models.Dessert
import com.example.menuapp.models.Rating
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.menu_item.view.*
import java.math.RoundingMode
import java.text.DecimalFormat

class DessertAdapter(
    private var data: List<Dessert>,
    private val tapAction: TapAction<Dessert>
) : RecyclerView.Adapter<DessertAdapter.DessertViewHolder>() {
    private var conversionRate: Double = 1.0
    private var ratings: List<Rating> = listOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DessertViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.menu_item, parent, false)
        return DessertViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: DessertViewHolder, position: Int) {
        val model = data[position]
        holder.bind(model, tapAction, conversionRate, ratings)
    }

    fun updateData(data: List<Dessert>) {
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

    class DessertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            model: Dessert,
            tapAction: TapAction<Dessert>,
            conversionRate: Double,
            ratings: List<Rating>
        ) {
            itemView.setOnClickListener { tapAction(model, layoutPosition) }
            itemView.tvMealTitle.text = model.dessert

            // price
            var sign = "€ "
            if (conversionRate != 1.0) {
                sign = "KN "
            }
            val priceRounded = model.price * conversionRate
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            itemView.tvPrice.text = sign + (df.format(priceRounded)).toString()

            // Preference
            val preference = "" + model.preference
            when (preference.toLowerCase()) {
                "vegan" -> itemView.ivFoodPreference.setImageResource(R.drawable.vegan)
                "gluten free" -> itemView.ivFoodPreference.setImageResource(R.drawable.glutenfree)
                else -> itemView.ivFoodPreference.setVisibility(View.GONE)
            }

            // image on recyclerview
            if (!model.url.isNullOrEmpty()) {
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

            // Thumbs up/ thumbs down buttons
            itemView.likeBtn.setOnClickListener {
                println("clicked like")
                rating.rating = rating.rating + 1
                itemView.tvRating.text = "" + rating.rating

                FoodClient.client.addRating(model.id, "drink", true).onResult(
                    onResponse = { _, apiData -> println(apiData) },
                    onFailure = { println("There has been an error. ${it?.message}") }
                )
            }

            itemView.dislikeBtn.setOnClickListener {
                println("clicked dislike")
                rating.rating = rating.rating - 1
                itemView.tvRating.text = "" + rating.rating

                FoodClient.client.addRating(model.id, "drink", false).onResult(
                    onResponse = { _, apiData -> println(apiData) },
                    onFailure = { println("There has been an error. ${it?.message}") }
                )
            }
            // Show star rating
            itemView.tvRating.text = "" + rating.rating
        }
    }
}