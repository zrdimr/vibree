package com.example.wearablecollector.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, // For future multi-user support
    val content: String,
    val timestamp: Long,
    val isPublic: Boolean = false,
    
    // AI Analysis Results (Nullable as they are processed asynchronously)
    val stressScore: Float? = null,
    val sentiment: String? = null
)
