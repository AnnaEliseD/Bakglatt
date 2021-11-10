package com.example.skismoring.data.manager

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.skismoring.R
import com.example.skismoring.data.historicData.LatestWeather
import com.example.skismoring.data.local.LocationDao
import com.example.skismoring.data.local.WaxDao
import com.example.skismoring.data.model.Recommendation
import com.example.skismoring.data.model.Wax
import com.example.skismoring.data.remote.WeatherDao
import com.example.skismoring.data.service.WaxChooser
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

class RecommendationRepository(waxDao: WaxDao) {
    private val latestWeather = LatestWeather()
    private val weatherDao = WeatherDao()
    private val waxRepository = WaxRepository.getInstance(waxDao)

    private val recommendations: List<Recommendation> = listOf(
            Recommendation(Wax("Personlig", "img"), "0"),
            Recommendation(Wax("Optimal", "img"), "0"),
    )

    /* Livedata objects that viewmodels can subscribe to */
    private val _optimalRecommendation = MutableLiveData<Recommendation>()
    val optimalRecommendation: LiveData<Recommendation> get() = _optimalRecommendation

    private val _personalRecommendation = MutableLiveData<Recommendation>()
    val personalRecommendation: LiveData<Recommendation> get() = _personalRecommendation

    private val _optimalGeneralRecommendation = MutableLiveData<Recommendation>()
    val optimalGeneralRecommendation: LiveData<Recommendation> get() = _optimalGeneralRecommendation

    private val _personalGeneralRecommendation = MutableLiveData<Recommendation>()
    val personalGeneralRecommendation: LiveData<Recommendation> get() = _personalGeneralRecommendation

    private val _weather = MutableLiveData<Pair<String,String>>()
    val weather: LiveData<Pair<String,String>> get() = _weather

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    init {
        _optimalRecommendation.value = recommendations.last()
        _personalRecommendation.value = recommendations.first()
        _weather.value = Pair("-", "icon")
        _loading.postValue(true)
    }

    /* Main method to get recommendation
    *  Gets the weather from LatestWeather (Frost), and temperature from WeatherDao (LocationForecast)
    *  Then uses this data to get a wax object from the WaxRepository */
    suspend fun getRecommendation(lat: Float, lng: Float) {
        _loading.postValue(true)
        val snowType = latestWeather.fromGeoToSnowType(lat, lng)
        val weather = weatherDao.getTemperature(lat.toString(), lng.toString())
        val temperature = weather.first
        val icon = weather.second
        val recommendationOptimal = waxRepository.getOptimalWax(temperature.toFloat(), snowType)
        val recommendationPersonal = waxRepository.getPersonalWax(temperature.toFloat(), snowType)

        _optimalRecommendation.postValue(Recommendation(recommendationOptimal, ""))
        _personalRecommendation.postValue(Recommendation(recommendationPersonal.first, recommendationPersonal.second))

        if (temperature.toDouble() % 1 == 0.0){
            _weather.postValue(Pair("${temperature.toDouble().roundToInt()}°", icon))
        }
        else{
            _weather.postValue(Pair("$temperature°", icon))
        }
        delay(100)
        _loading.postValue(false)
    }

    /* Gets the recommendation for the manual wax calculator
    *  Uses userinput in the parameters of the methods from WaxRepository */
    fun getGeneralRecommendation(temp: Float, snowType: String) {
        val recommendationOptimal = waxRepository.getWaxFromManualCalculator(temp, snowType, false)
        val recommendationPersonal = waxRepository.getWaxFromManualCalculator(temp, snowType, true)

        _optimalGeneralRecommendation.postValue(Recommendation(recommendationOptimal.first, recommendationOptimal.second))
        _personalGeneralRecommendation.postValue(Recommendation(recommendationPersonal.first, recommendationPersonal.second))
    }

    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: RecommendationRepository? = null

        fun getInstance(waxDao: WaxDao) =
                instance ?: synchronized(this) {
                    instance ?: RecommendationRepository(waxDao).also { instance = it }
                }
    }
}