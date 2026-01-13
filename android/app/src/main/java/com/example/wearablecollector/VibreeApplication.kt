package com.example.wearablecollector

import android.app.Application
import androidx.room.Room
import com.example.wearablecollector.data.AppDatabase

class VibreeApplication : Application() {
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "vibree-db"
        ).build()
        
        // Initialize Repository with DB
        SensorDataRepository.initialize(database.heartRateDao())
    }
}
