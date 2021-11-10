package com.example.skismoring.ui.map

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.skismoring.data.local.LocationRoomDatabase
import com.example.skismoring.data.manager.LocationRepository
import com.example.skismoring.data.model.Location
import kotlinx.coroutines.launch

class MapViewModel (application: Application): AndroidViewModel(application) {
    private val repository: LocationRepository

    init {
        val locationDao = LocationRoomDatabase.getDatabase(application).locationDao()
        repository = LocationRepository.getInstance(locationDao)
    }

    val mapPosition =  MutableLiveData(Position(59.97,10.78,9.0))

    fun changePos(pos: Position){
        mapPosition.value = pos
    }
    fun readData(context: Context){
        viewModelScope.launch {
            repository.readData(context)
        }
    }
}

data class Position(val lat:Double, val lng: Double, val zoom: Double)