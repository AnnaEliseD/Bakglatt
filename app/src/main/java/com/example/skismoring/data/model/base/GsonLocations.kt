package com.example.skismoring.data.model

data class Name (val name: String)
data class GeometryGson (val coordinates: List<Double>)
data class Feature (val properties: Name, val geometry: GeometryGson)
data class GsonLocations(val type: String, val features: List<Feature>)
