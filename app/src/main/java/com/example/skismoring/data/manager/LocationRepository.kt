package com.example.skismoring.data.manager

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.skismoring.data.local.GsonLocation
import com.example.skismoring.data.local.LocationDao
import com.example.skismoring.data.model.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LocationRepository(private val locationDao: LocationDao) {
    var favoritesList: List<Location> = listOf()
    private val _allLocations = MutableLiveData<List<Location>>()
    val allLocations: LiveData<List<Location>> get() = _allLocations

    private val gsonLocationDao = GsonLocation()

    /* Calls the method to get the locations, and also updates favorites to catch previously added favorites */
    suspend fun readData(context: Context){
        val gsonData = gsonLocationDao.getGsonLocations(context)
        if (allLocations.value == null){
            _allLocations.postValue(gsonData)
        }
        updateFavorites()
    }

    suspend fun addLocation(location: Location){
        locationDao.addLocation(location)
        Log.v("Adding to db", location.toString())
        updateFavorites()
    }
    suspend fun deleteLocation(location: Location){
        locationDao.delete(location)
        Log.v("Removing from db", location.toString())
        updateFavorites()
    }
    private suspend fun updateLocation(location: Location){
        locationDao.updateLocation(location)
        Log.v("Updating in db", location.toString())
        updateFavorites()
    }

    /*  Gets a location that the user wants to add as a favorite.
        Based on the state of the location calls the correct method */
    suspend fun updateLocationFromView(location: Location){
        if(location.favorite){
            for (loc in favoritesList){
                if (loc.name == location.name ) {
                    updateLocation(loc)
                    return
                }
            }
            addLocation(location)
        }
        else {
            deleteLocation(location)
        }
    }

    suspend fun updateFavorites(){
        val task = CoroutineScope(Dispatchers.IO).async {
            favoritesList = locationDao.getFavoriteLocations()
        }
        task.await()
        Log.v("LocationRepository", "Updating favorites --> $favoritesList")
    }

    suspend fun getFavorites(): List<Location>{
        updateFavorites()
        return favoritesList
    }

    suspend fun clearData(){
        locationDao.nukeTable()
        updateFavorites()
    }

    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: LocationRepository? = null

        fun getInstance(locationDao: LocationDao) =
                instance ?: synchronized(this) {
                    instance ?: LocationRepository(locationDao).also { instance = it }
                }
    }
}