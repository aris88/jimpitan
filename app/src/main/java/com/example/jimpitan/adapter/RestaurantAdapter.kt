package com.example.jimpitan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jimpitan.R
import com.example.jimpitan.model.Restaurant
import com.example.jimpitan.util.RestaurantUtil
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import me.zhanghai.android.materialratingbar.MaterialRatingBar


/**
 * RecyclerView adapter for a list of Restaurants.
 */
open class RestaurantAdapter(query: Query?, val mListener: OnRestaurantSelectedListener) :
    FirestoreAdapter<RestaurantAdapter.ViewHolder?>(query) {

    interface OnRestaurantSelectedListener {
        fun onRestaurantSelected(restaurant: DocumentSnapshot?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_restaurant, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), mListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: ImageView
        var nameView: TextView
        var ratingBar: MaterialRatingBar
        var numRatingsView: TextView
        var priceView: TextView
        var categoryView: TextView
        var cityView: TextView

        init {
            imageView = itemView.findViewById(R.id.restaurant_item_image)
            nameView = itemView.findViewById(R.id.restaurant_item_name)
            ratingBar = itemView.findViewById(R.id.restaurant_item_rating)
            numRatingsView = itemView.findViewById(R.id.restaurant_item_num_ratings)
            priceView = itemView.findViewById(R.id.restaurant_item_price)
            categoryView = itemView.findViewById(R.id.restaurant_item_category)
            cityView = itemView.findViewById(R.id.restaurant_item_city)
        }


        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnRestaurantSelectedListener?
        ) {
            val restaurant: Restaurant = snapshot.toObject(Restaurant::class.java)!!
            val resources = itemView.resources

            // Load image
            Glide.with(imageView.context)
                .load(restaurant.photo)
                .into(imageView)
            nameView.setText(restaurant.name)
            ratingBar.rating = restaurant.avgRating.toFloat()
            cityView.setText(restaurant.city)
            categoryView.setText(restaurant.category)
            numRatingsView.text = resources.getString(R.string.fmt_num_ratings,restaurant.numRatings)
            priceView.setText(RestaurantUtil().getPriceString(restaurant))

            // Click listener
            itemView.setOnClickListener { listener?.onRestaurantSelected(snapshot) }
        }


    }
}
