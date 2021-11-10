package com.example.skismoring.ui.favorites

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.skismoring.R
import com.example.skismoring.data.model.Location
import com.google.android.material.card.MaterialCardView

class FavoritesAdapter(
        private var dataSet: List<Location>,
        private val listener: OnRecyclerViewItemClickListener) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: MaterialCardView = view.findViewById(R.id.FavoriteCardView)
        val nameTW: TextView = view.findViewById(R.id.name)
        val starButton: ImageButton = view.findViewById(R.id.starButton)
    }

    interface OnRecyclerViewItemClickListener{
        fun onRecyclerViewItemClicked(position: Int, id: Int)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.favorite_element, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currentitem = dataSet[position]
        viewHolder.nameTW.text = currentitem.name

        // Set different listener to catch user click on the element or the star
        viewHolder.root.setOnClickListener{
            listener.onRecyclerViewItemClicked(position, -1)
        }
        viewHolder.starButton.setOnClickListener {
            listener.onRecyclerViewItemClicked(position, it.id)
        }
    }

    fun setData(locationList: List<Location>){
        dataSet = locationList
        this.notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}