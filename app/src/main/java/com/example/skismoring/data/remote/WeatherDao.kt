package com.example.skismoring.data.remote

import com.example.skismoring.data.model.base.WeatherForecast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson

class WeatherDao {

    /* Gets the current temperature for a certain lat and lng */
    suspend fun getTemperature(lat: String, lon:String): Pair<String, String> {
        val path = "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/2.0/compact?lat=${lat}&lon=${lon}"
        val response: WeatherForecast = Gson().fromJson(Fuel.get(path).awaitString(), WeatherForecast::class.java)

        val currentTemp = response.properties.timeseries[0].data.instant.details.air_temperature
        val iconCode = response.properties.timeseries[0].data.next_1_hours.summary.symbol_code
        return Pair(currentTemp, iconCode)
    }

    /* Gets a weatherForecast based on a certain lat and lng*/
    suspend fun getWeatherForecast(lat: String, lng: String): WeatherForecast?{
        val path = "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/2.0/compact?lat=${lat}&lon=${lng}"
        return Gson().fromJson(Fuel.get(path).awaitString(), WeatherForecast::class.java)
    }
}
