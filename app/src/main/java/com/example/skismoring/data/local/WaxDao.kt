package com.example.skismoring.data.local

import androidx.room.*
import com.example.skismoring.data.model.Wax

@Dao
interface WaxDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addWax(wax: Wax)

    @Delete
    fun delete(wax: Wax)

    @Query("SELECT * FROM wax_table ORDER BY name ASC")
    fun getWaxes(): List<Wax>

    @Query("DELETE FROM wax_table")
    fun nukeTable()
}