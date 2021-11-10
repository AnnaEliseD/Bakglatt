package com.example.skismoring.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skismoring.R
import com.example.skismoring.data.model.Location

class SearchAdapter (
    private var dataSet: List<Location>,
    private val isSearchType: Boolean,
    private val listener: OnItemClickListener) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{
        val textView: TextView = view.findViewById(R.id.searchResultTitle)
        val img: ImageView = view.findViewById(R.id.searchResultImage)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val position: Int = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(dataSet[position])
            }
        }
    }
    interface OnItemClickListener {
        fun onItemClick(item: Location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_result, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataSet[position].name
        if (isSearchType) {
            holder.img.setImageResource(R.drawable.ic_baseline_search_24)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    fun filterList(list : List<Location>) {
        dataSet = list
        this.notifyDataSetChanged()
    }
}