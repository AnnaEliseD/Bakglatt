package com.example.skismoring.data.model.base

import java.util.*

data class WeatherForecast (
    val type: String,
    //val geometry: Geometry,
    val properties: Properties,
    )

data class Properties(
    //val meta: Meta,
    val timeseries: List<TimeObject>
)

data class TimeObject (
    val time: Date,
    val data: Data
    )

data class Data(
    val instant: Instant,
    val next_12_hours: Next12hours,
    val next_1_hours: Next1hours,
    val next_6_hours: Next6hours,
)

data class Instant(
    val details: Details
)
data class Details(
    val air_temperature: String
)
data class Next12hours(
    val summary: Summary
)
data class Next1hours(
    val summary: Summary
)
data class Next6hours(
    val summary: Summary
    )
data class Summary(
    val symbol_code: String
)