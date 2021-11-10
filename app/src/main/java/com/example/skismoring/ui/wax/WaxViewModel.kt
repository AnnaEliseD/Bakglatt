package com.example.skismoring.ui.wax

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.skismoring.data.local.LocationRoomDatabase
import com.example.skismoring.data.manager.LocationRepository
import com.example.skismoring.data.manager.WaxRepository
import com.example.skismoring.data.model.Wax
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WaxViewModel (application: Application): AndroidViewModel(application) {

    private val waxRepository: WaxRepository
    var allWax: List<Wax>

    private val _waxes = MutableLiveData<List<Wax>>(listOf())
    val waxes: LiveData<List<Wax>> get () = _waxes


    init {
        val waxDao = LocationRoomDatabase.getDatabase(application).waxDao()
        waxRepository = WaxRepository.getInstance(waxDao)
        allWax = waxRepository.waxes
    }

    fun updateWaxes(){
        viewModelScope.launch(Dispatchers.IO) {
            waxRepository.fillPersonalizedList()
            _waxes.postValue(waxRepository.personalizedWaxes)
        }
    }

    fun addWax(wax: Wax) {
        viewModelScope.launch(Dispatchers.IO) {
            waxRepository.addWax(wax)
            updateWaxes()
        }
    }

    fun removeWax(wax: Wax) {
        viewModelScope.launch(Dispatchers.IO) {
            waxRepository.removeWax(wax)
            updateWaxes()
        }
    }
}