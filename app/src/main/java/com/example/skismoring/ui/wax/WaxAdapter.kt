package com.example.skismoring.ui.wax

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skismoring.R
import com.example.skismoring.data.model.Wax
import com.mapbox.mapboxsdk.http.HttpIdentifier.getIdentifier


class WaxAdapter(private val liste: List<Wax>, private val listener: OnItemClickListener): RecyclerView.Adapter<WaxAdapter.ViewHolder>(){
    private var personalWaxes = listOf<Wax>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_wax_cardview, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return liste.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wax: Wax = liste[position]
        holder.name.text = wax.name
        val context: Context = holder.img.context
        Glide.with(context).load(context.resources.getIdentifier(wax.img, "drawable", context.packageName)).into(holder.img)

        holder.holds.isChecked = wax in personalWaxes

        holder.holds.setOnClickListener{
            if (holder.holds.isChecked){
                if(position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(wax, true)
                }
            }
            else {
                if(position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(wax, false)
                }
            }
        }
    }
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.txt_smoring)
        val img: ImageView = itemView.findViewById(R.id.img_smoring)
        val holds: CheckBox = itemView.findViewById(R.id.cb_smoring)
    }
    //Removing and adding of waxes is handled in the WaxGarageFragment
    interface OnItemClickListener {
        fun onItemClick(wax: Wax, checked: Boolean)
    }
    fun updatePersonalWaxes(list: List<Wax>){
        this.personalWaxes = list
        this.notifyDataSetChanged()
    }
}
