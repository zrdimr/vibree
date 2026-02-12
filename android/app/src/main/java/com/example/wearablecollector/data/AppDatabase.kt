package com.example.wearablecollector.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HeartRateRecord::class, Post::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun heartRateDao(): HeartRateDao
    abstract fun postDao(): PostDao
}
