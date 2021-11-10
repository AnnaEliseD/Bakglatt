package com.example.skismoring.ui.search

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.skismoring.data.local.GsonLocation
import com.example.skismoring.data.local.LocationRoomDatabase
import com.example.skismoring.data.manager.LocationRepository
import com.example.skismoring.data.model.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class SearchViewModel(application: Application): AndroidViewModel(application) {
    val searchList: LiveData<List<Location>>
    private val repository: LocationRepository
    init {
        val locationDao = LocationRoomDatabase.getDatabase(application).locationDao()
        repository = LocationRepository.getInstance(locationDao)
        searchList = repository.allLocations
    }

    val searchListFiltered = MutableLiveData<List<Location>>(listOf())

    //Gives the user locations from what they have written in the search field
    fun filter(query: String){
        // Search filtering
        resetFilter()
        val tempSearchList = mutableListOf<Location>()
        if (query != "") {
            for (item in searchList.value!!) {
                if (item.name.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
                    tempSearchList.add(item)
                }
            }
        }
        searchListFiltered.postValue(tempSearchList)
    }
    private fun resetFilter(){
        searchListFiltered.value = listOf()
    }
    fun readData(context: Context){
        viewModelScope.launch {
            repository.readData(context)
        }
    }
}