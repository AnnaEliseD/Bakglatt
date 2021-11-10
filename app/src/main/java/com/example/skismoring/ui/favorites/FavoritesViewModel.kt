package com.example.skismoring.ui.favorites

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.skismoring.data.local.LocationRoomDatabase
import com.example.skismoring.data.model.Location
import com.example.skismoring.data.manager.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application): AndroidViewModel(application) {
    private val repository: LocationRepository
    val favorites = MutableLiveData<List<Location>>(listOf())

    init {
        val locationDao = LocationRoomDatabase.getDatabase(application).locationDao()
        repository = LocationRepository(locationDao)
    }

    //Fetches the favorites that are in the database and updates the list
    fun updateFavorites(){
        viewModelScope.launch(Dispatchers.IO) {
            favorites.postValue(repository.getFavorites())
        }
    }

    fun deleteFavorite(location: Location){
        viewModelScope.launch(Dispatchers.IO ) {
            repository.deleteLocation(location)
            Log.v("favviewmodel", "Removing $location")
            /*
            Since the database has changed the list with favorites has to be updated for the
            fragment to display the correct information
             */
            updateFavorites()
        }
    }

    fun addFavorite(location: Location){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addLocation(location)
            updateFavorites()
    }   }
}