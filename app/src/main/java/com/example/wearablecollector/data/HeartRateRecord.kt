package com.example.wearablecollector.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heart_rate_records")
data class HeartRateRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val bpm: Int,
    val hrv: Int,
    val stressLevel: Int
)
