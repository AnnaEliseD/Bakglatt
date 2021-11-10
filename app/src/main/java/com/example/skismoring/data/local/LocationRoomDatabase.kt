package com.example.skismoring.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.skismoring.data.model.Location
import com.example.skismoring.data.model.Wax

@Database(entities = [Location::class, Wax::class], version = 1, exportSchema = false)
abstract class LocationRoomDatabase: RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun waxDao(): WaxDao

    companion object{
            @Volatile
            private var INSTANCE: LocationRoomDatabase? = null

            fun getDatabase(context: Context): LocationRoomDatabase {
                val tempIns = INSTANCE
                if(tempIns != null){
                    return tempIns
                }
                synchronized(this){
                    val instance = Room.databaseBuilder(
                            context.applicationContext,
                            LocationRoomDatabase::class.java,
                            "room_database"
                    ).build()
                    INSTANCE = instance
                    return instance
                }
            }
        }
}