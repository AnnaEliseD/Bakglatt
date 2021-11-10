package com.example.skismoring.data.historicData

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.skismoring.data.model.base.ObservationsBase
import com.example.skismoring.data.model.base.SourceBase
import com.example.skismoring.data.model.base.TimeSeriesBase
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.interceptors.cUrlLoggingRequestInterceptor
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.*
import org.json.JSONObject.NULL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.sqrt

/* Methods commented out would have been used if the sensors contained proper data.
* Our solution was to always use one of two sensor we knew had the data.
* Should there been a need for scalability this is high-priority */

class LatestWeather {
    private val baseUrl= "https://IN2000-frostproxy.ifi.uio.no/"
    private val clientId = "b7c8430c-1128-41ed-9d43-137c29929725"
    private val gson = Gson()
/*
    /* Method to find the nearest source sensor based on lat lng */
    suspend fun findNearestSource(lat: Float, lng: Float): String {
        var sourceId = ""
        val geometryUrl = "${baseUrl}sources/v0.jsonld?geometry=nearest(POINT(${lng} ${lat}))"
        Log.v("url", geometryUrl)
        val task = CoroutineScope(Dispatchers.IO).async {
            try {
                val response = gson.fromJson(Fuel
                        .get(geometryUrl)
                        .authentication().basic(client_id, "")
                        .awaitString(), SourceBase::class.java)
                Log.d("API fetching (1)", response.toString())
                sourceId = response.data?.get(0)?.id.toString()
            } catch(exception: Exception) {
                println("(1) A network request exception was thrown: ${exception.message}")
            }
        }
        task.await()
        return sourceId
    }*/

    /* Method data returns one of two source based on lat and lng. */
    private fun findNearestFunctioningSource(lat: Float, lng: Float): String {
        val tryvannLat = 10.6693
        val tryvannLng = 59.9847
        val bjornholtLat = 10.6878
        val bjornholtLng = 90.0513

        val distanceTryvann = sqrt((tryvannLat-lat).pow(2) + (tryvannLng-lng).pow(2))
        val distanceBjornholt = sqrt((bjornholtLat-lat).pow(2) + (bjornholtLng-lng).pow(2))

        return if (distanceTryvann < distanceBjornholt) "SN18950"
        else "SN18500"
    }

    /*
    /* Method to find the available data from a certain source */
    suspend fun availableTimeSeries(sourceId: String) : MutableList<String?> {
        val currentTimeSeries = mutableListOf<String?>()
        val timeSeriesUrl = "${baseUrl}observations/availableTimeSeries/v0.jsonld?sources=${sourceId}"

        val task = CoroutineScope(Dispatchers.IO).async {
            try {
                val response = gson.fromJson(Fuel
                        .get(timeSeriesUrl)
                        .authentication().basic(client_id, "")
                        .awaitString(), TimeSeriesBase::class.java)
                Log.d("API fetching (2)", response.toString())
                for (dataClass in response.data!!) {
                    if (dataClass.validTo != NULL) {
                        currentTimeSeries += dataClass.elementId
                    }
                }
            } catch(exception: Exception) {
                println("(2) A network request exception was thrown: ${exception.message}")
            }
        }
        task.await()

        return currentTimeSeries
    }
    */

    /* Gets the latest observation for a certain time period (4 days) for a certain source
    * and elements such as "air_temperature"*/
    @SuppressLint("SimpleDateFormat")
    suspend fun getLatestDays(sourceId: String, element:String) : MutableList<String?> {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val observations = mutableListOf<String?>()
        val calendar = Calendar.getInstance()
        val currentDate = formatter.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -4)
        val fourDaysAgo = formatter.format(calendar.time)

        Log.v("Dates", "c: $currentDate --- 4: $fourDaysAgo")
        val observationUrl = "${baseUrl}observations/v0.jsonld?sources=${sourceId}&referencetime=${fourDaysAgo}/${currentDate}&elements=${element}&timeresolutions=PT1H"


        val task = CoroutineScope(Dispatchers.IO).async {
            try {
                val response = gson.fromJson(Fuel
                        .get(observationUrl)
                        .authentication().basic(clientId, "")
                        .awaitString(), ObservationsBase::class.java)
                Log.d("API fetching (3)", response.toString())
                for (dataList in response.data!!) {
                    for (observation in dataList.observations!!) {
                        observations += observation.value
                    }
                }
            } catch(exception: Exception) {
                println("(3) A network request exception was thrown: ${exception.message}")
            }
        }
        task.await()

        return observations
    }

    /* Method to find the snow type based on the recent preciptation amount and temperature */
    private fun findSnowType(precipitationObs: MutableList<String?>, temperatureObs: MutableList<String?>) : String {
        var snowType = "old"
        val underTen : ArrayList<String?> = arrayListOf()
        val betweenTenAndOne : ArrayList<String?> = arrayListOf()
        val overOne : ArrayList<String?> = arrayListOf()
        var indexOfLastPrecip = 0

        for (observation in precipitationObs) {
            if (observation != null) {
                indexOfLastPrecip = precipitationObs.indexOf(observation)
            }
        }

        //Temperatures since last snowfall
        for (i in indexOfLastPrecip until temperatureObs.size) {
            if (temperatureObs[i]!! <= (-10).toString()) underTen.add(temperatureObs[i])
            if ((-10).toString() < temperatureObs[i]!! && temperatureObs[i]!! < (-0.5).toString()) betweenTenAndOne.add(temperatureObs[i])
            if ((-0.5).toString() <= temperatureObs[i]!!) overOne.add(temperatureObs[i])
        }

        /* Set snow type based on time since snowfall
        * and the rate of transformation in given temperatures */
        if (indexOfLastPrecip != 0) snowType = "new"
        if (overOne.size > 0) {
            snowType = "old"
            return snowType
        }
        if (indexOfLastPrecip < 24*4 && betweenTenAndOne.size > 35) {
            snowType = "old"
            return snowType
        }
        if (indexOfLastPrecip < 24*1) {
            snowType = "old"
            return snowType
        }
        return snowType
    }

    /* Finds the snowtype based on a lat and lng */
    suspend fun fromGeoToSnowType(lat: Float, long: Float) : String {
        val tempObs: MutableList<String?>
        val precipObs: MutableList<String?>

        /*
        /* This would have wound the temperatures and precipation using the alternative methods
        *  that wouldve been used in case of better data
        */

        val sourceID = findNearestSource(lat, long)

        val avTimeSeries = sourceID?.let { availableTimeSeries(it) }
        if (avTimeSeries != null) {
            for (i in 0..avTimeSeries.size) {
                if (avTimeSeries[i] == "air_temperature")   tempObs = getLatestDays(sourceID, "air_temperature")
                if (avTimeSeries[i] == "precipitation_amount")   precipObs = getLatestDays(sourceID, "sum(precipitation_amount PT1H)")
            }
        }
        */
        val sourceID = findNearestFunctioningSource(lat, long)
        tempObs = getLatestDays(sourceID, "air_temperature")
        precipObs = getLatestDays(sourceID, "sum(precipitation_amount PT1H)")

        return findSnowType(precipObs, tempObs)
    }
}