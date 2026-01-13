package com.example.wearablecollector.utils

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var gender: String
        get() = prefs.getString("gender", "") ?: ""
        set(value) = prefs.edit().putString("gender", value).apply()

    var birthday: Long
        get() = prefs.getLong("birthday", 0L)
        set(value) = prefs.edit().putLong("birthday", value).apply()
        
    // In case we want to cache name/photo locally too
}
