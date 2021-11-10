package com.example.skismoring.ui.location

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skismoring.R
import com.example.skismoring.data.model.base.TimeObject


class WeatherAdapter(private val forecast: List<TimeObject>): RecyclerView.Adapter<WeatherAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_forecast_cardview, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return forecast.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeObject : TimeObject = forecast[position]
        val time : String = timeObject.time.toString().substring(11,13)

        var tmp : Int = time.toInt() + 3
        if(tmp == 24) tmp = 0
        if(tmp == 25) tmp = 1
        if(tmp == 26) tmp = 2
        val tmpTime = "$tmp:00"
        holder.time.text = tmpTime

        val context : Context = holder.img.context

        Glide.with(context).load(context.resources.getIdentifier(timeObject.data.next_1_hours.summary.symbol_code, "drawable", context.packageName)).into(holder.img)

        val airTemp = timeObject.data.instant.details.air_temperature + "℃"
        holder.airTemp.text = airTemp
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val img: ImageView = itemView.findViewById(R.id.img_weather)
        var airTemp: TextView = itemView.findViewById(R.id.txt_temperature)
        val time: TextView = itemView.findViewById(R.id.txt_date)
    }
}
