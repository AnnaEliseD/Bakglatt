package com.example.skismoring.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skismoring.data.local.LocationRoomDatabase
import com.example.skismoring.data.manager.LocationRepository
import com.example.skismoring.data.manager.WaxRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsViewModel (application: Application): AndroidViewModel(application) {

    private val locationRepository: LocationRepository
    private val waxRepository: WaxRepository
    init {
        val db = LocationRoomDatabase.getDatabase(application)
        val waxDao = db.waxDao()
        waxRepository = WaxRepository.getInstance(waxDao)
        val locationDao = db.locationDao()
        locationRepository = LocationRepository.getInstance(locationDao)
    }

    fun clearAllData(){
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.clearData()
            waxRepository.clearData()
        }
    }
}