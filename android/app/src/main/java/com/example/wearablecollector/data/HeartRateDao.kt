package com.example.wearablecollector.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HeartRateDao {
    @Insert
    suspend fun insert(record: HeartRateRecord)

    @Query("SELECT * FROM heart_rate_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<HeartRateRecord>>

    @Query("SELECT * FROM heart_rate_records ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastRecord(): HeartRateRecord?
}
