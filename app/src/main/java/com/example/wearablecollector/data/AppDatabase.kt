package com.example.wearablecollector.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HeartRateRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun heartRateDao(): HeartRateDao
}
