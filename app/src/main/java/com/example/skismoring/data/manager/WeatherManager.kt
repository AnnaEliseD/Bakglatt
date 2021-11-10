package com.example.skismoring.data.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.skismoring.data.model.base.WeatherForecast
import com.example.skismoring.data.remote.WeatherDao

class WeatherManager(){
    private val weatherDao = WeatherDao()

    private val _weatherForecast = MutableLiveData<WeatherForecast>()
    val weatherForecast : LiveData<WeatherForecast> get() = _weatherForecast

    /* Gets the forecast from the WeatherDao (LocationForecast) */
    suspend fun getWeatherForecast(lat: Double?, lng: Double?){
        _weatherForecast.postValue(weatherDao.getWeatherForecast(lat.toString(), lng.toString()))

    }

    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: WeatherManager? = null

        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: WeatherManager().also { instance = it }
                }
    }
}