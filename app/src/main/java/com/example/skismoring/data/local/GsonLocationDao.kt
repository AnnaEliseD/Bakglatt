package com.example.skismoring.data.local

import android.content.Context
import com.example.skismoring.data.model.GsonLocations
import com.example.skismoring.data.model.Location
import com.google.gson.Gson

class GsonLocation {
    /* Deserializes the locations from the local file*/
    fun getGsonLocations(context: Context): List<Location> {
        val tempList = mutableListOf<Location>()
        val data = Gson().fromJson(context.assets.open("locations.geojson").bufferedReader().use { it.readText() }, GsonLocations::class.java)
        for (feature in data.features){
            val name = feature.properties.name
            val lng = feature.geometry.coordinates.get(0)
            val lat = feature.geometry.coordinates.get(1)
            tempList.add(Location(name, lat, lng))
        }
        return tempList
    }
}