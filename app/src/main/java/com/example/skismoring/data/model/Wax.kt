package com.example.skismoring.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wax_table")
data class Wax(
        @PrimaryKey val name: String = "dummyValue",
        val img: String? = "",
        var innehar : Boolean = false
)
