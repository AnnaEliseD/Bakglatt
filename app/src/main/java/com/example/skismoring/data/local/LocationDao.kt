package com.example.skismoring.data.local

import androidx.room.*
import com.example.skismoring.data.model.Location

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addLocation (location: Location)

    @Delete
    fun delete(location: Location)

    @Query("SELECT * FROM location_table ORDER BY name ASC")
    fun getAllLocations(): List<Location>

    @Query("SELECT * FROM location_table WHERE favorite = 1 ORDER BY name ASC")
    fun getFavoriteLocations(): List<Location>

    @Update
    fun updateLocation(location: Location)

    @Query("DELETE FROM location_table")
    fun nukeTable()
}