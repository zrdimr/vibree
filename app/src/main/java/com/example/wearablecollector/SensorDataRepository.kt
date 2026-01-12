package com.example.wearablecollector

import androidx.lifecycle.MutableLiveData
import com.example.wearablecollector.data.HeartRateDao
import com.example.wearablecollector.data.HeartRateRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SensorDataRepository {
    private var heartRateDao: HeartRateDao? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    val heartRate = MutableLiveData<Int>(0)
    val hrv = MutableLiveData<Int>(0) 
    val gsr = MutableLiveData<Float>(0f)
    val eda = MutableLiveData<Float>(0f)
    val stressLevel = MutableLiveData<Int>(0)
    val status = MutableLiveData<String>("Disconnected")
    val scannedDevices = MutableLiveData<List<android.bluetooth.BluetoothDevice>>(emptyList())

    fun initialize(dao: HeartRateDao) {
        heartRateDao = dao
    }

    fun addScannedDevice(device: android.bluetooth.BluetoothDevice) {
        val currentList = scannedDevices.value.orEmpty().toMutableList()
        if (currentList.none { it.address == device.address }) {
            currentList.add(device)
            scannedDevices.postValue(currentList)
        }
    }
    
    fun clearScannedDevices() {
        scannedDevices.postValue(emptyList())
    }

    fun updateHeartRate(hr: Int) {
        heartRate.postValue(hr)
    }

    fun updateHrv(rrInterval: Int) {
        // Calculate HRV and Stress using the new logic
        val result = com.example.wearablecollector.logic.StressCalculator.processRR(rrInterval)
        val currentHr = heartRate.value ?: 0
        
        hrv.postValue(result.hrv)
        stressLevel.postValue(result.stressLevel)

        // Save to DB
        if (result.hrv > 0) {
            scope.launch {
                heartRateDao?.insert(
                    HeartRateRecord(
                        timestamp = System.currentTimeMillis(),
                        bpm = currentHr,
                        hrv = result.hrv,
                        stressLevel = result.stressLevel
                    )
                )
            }
        }
    }

    fun updateGsr(value: Float) {
        gsr.postValue(value)
    }
    
    fun updateEda(value: Float) {
        eda.postValue(value)
    }

    fun updateStatus(newStatus: String) {
        status.postValue(newStatus)
        if (newStatus == "Disconnected") {
            com.example.wearablecollector.logic.StressCalculator.reset()
            heartRate.postValue(0)
            hrv.postValue(0)
            stressLevel.postValue(0)
        }
    }

    fun getAllRecords(): androidx.lifecycle.LiveData<List<HeartRateRecord>> {
        return androidx.lifecycle.liveData { 
            heartRateDao?.getAllRecords()?.collect { emit(it) } 
        }
    }
}
