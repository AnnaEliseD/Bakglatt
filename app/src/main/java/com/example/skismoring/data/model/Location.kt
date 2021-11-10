package com.example.skismoring.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_table")
data class Location (
        @PrimaryKey val name: String = "dummyValue",
        val lat: Double = 0.00,
        val lng: Double = 0.00,
        var favorite: Boolean = false
)