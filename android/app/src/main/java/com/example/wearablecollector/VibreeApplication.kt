package com.example.wearablecollector

import android.app.Application
import androidx.room.Room
import com.example.wearablecollector.data.AppDatabase

class VibreeApplication : Application() {
    companion object {
        lateinit var database: AppDatabase
        lateinit var socialRepository: com.example.wearablecollector.data.SocialRepository
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "vibree-db"
        )
        .fallbackToDestructiveMigration() // Simple migration strategy for dev
        .build()
        
        // Initialize Repositories
        SensorDataRepository.initialize(database.heartRateDao())
        socialRepository = com.example.wearablecollector.data.SocialRepository(database.postDao())
    }
}
