package com.example.skismoring.ui.location

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.skismoring.data.local.LocationRoomDatabase
import com.example.skismoring.data.manager.LocationRepository
import com.example.skismoring.data.manager.RecommendationRepository
import com.example.skismoring.data.manager.WeatherManager
import com.example.skismoring.data.model.Location
import com.example.skismoring.data.model.Recommendation
import com.example.skismoring.data.model.base.WeatherForecast
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationViewModel(application: Application): AndroidViewModel(application) {
    var favorites: List<Location>
    private val repository: LocationRepository
    private val recommendationRepository: RecommendationRepository
    private val weatherManager: WeatherManager

    init {
        val db = LocationRoomDatabase.getDatabase(application)
        val locationDao = db.locationDao()
        repository = LocationRepository.getInstance(locationDao)
        favorites = repository.favoritesList

        val waxDao = db.waxDao()
        recommendationRepository = RecommendationRepository.getInstance(waxDao)

        weatherManager = WeatherManager.getInstance()

        suspend {getWeatherForecast()}
    }

    val optimalRecommendation : LiveData<Recommendation> get() = recommendationRepository.optimalRecommendation
    val personalRecommendation : LiveData<Recommendation> get() = recommendationRepository.personalRecommendation
    val weather : LiveData<Pair<String, String>> get() = recommendationRepository.weather

    private val _currentLocation = MutableLiveData(Location("", 60.0,10.0, false))
    val currentLocation: LiveData<Location> get() = _currentLocation

    val wForecast : LiveData<WeatherForecast> get() = weatherManager.weatherForecast

    val loading: LiveData<Boolean> get() = recommendationRepository.loading

    //Gets recommended wax and the LiveData-objects will fetch the new data
    private fun getRecommendation(){
        viewModelScope.launch (Dispatchers.IO ){
            val lat = currentLocation.value?.lat
            val lng = currentLocation.value?.lng
            recommendationRepository.getRecommendation(lat!!.toFloat(), lng!!.toFloat())
        }

    }

    fun updateFavorites(){
        viewModelScope.launch(Dispatchers.IO) {
            favorites = repository.getFavorites()
            currentLocation.value?.let { checkIfLocationIsFavorite(it) }
        }
    }
    private fun checkIfLocationIsFavorite(location: Location){
        // Check if favorite
        var flag = false
        for (loc in favorites){
            if (loc.name == location.name) {
                _currentLocation.postValue(loc)
                flag = true
            }
        }

        if (!flag) {
            location.favorite = false
            _currentLocation.postValue(location)
        }
    }

    fun getWeatherForecast() {
        viewModelScope.launch(Dispatchers.IO) {
            weatherManager.getWeatherForecast(currentLocation.value?.lat, currentLocation.value?.lng)
        }
    }

    /* Sets current location to show in view
    * Also checks if it is a favorite, to use the samne Location should it have been saved before*/
    fun setCurrentLocation (location: Location){
        viewModelScope.launch {
            favorites = repository.favoritesList
            checkIfLocationIsFavorite(location)
            getRecommendation()
        }
    }

    /* Method to set current location when click on target in MapView
    * Required to get the correct data */
    fun setCurrentLocationFromFeature (feature: Feature){
        val point = feature.geometry() as Point
        val lat = point.latitude()
        val lng = point.longitude()
        val name = feature.properties()?.get("name")?.asString ?: "Location"
        setCurrentLocation(Location(name, lat, lng, false))
    }

    /* Update current location as favorite (save or delete)*/
    fun updateCurrentLocationFavorite(){
        val updatedLocation = currentLocation.value
        updatedLocation?.favorite = !currentLocation.value?.favorite!!
        _currentLocation.postValue(updatedLocation)
        viewModelScope.launch(Dispatchers.IO){
            // Toggle favorite and update
            Log.v("LocationViewModel", currentLocation.value.toString())
            repository.updateLocationFromView(currentLocation.value!!)
        }
    }
}


