package com.example.wearablecollector

import androidx.lifecycle.MutableLiveData

object SensorDataRepository {
    val heartRate = MutableLiveData<Int>(0)
    val hrv = MutableLiveData<Int>(0) // Placeholder, as standard HR service might not give explicit HRV
    val gsr = MutableLiveData<Float>(0f)
    val eda = MutableLiveData<Float>(0f)
    val status = MutableLiveData<String>("Disconnected")
    val scannedDevices = MutableLiveData<List<android.bluetooth.BluetoothDevice>>(emptyList())

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
        // Simple HRV representation using RR interval for now
        hrv.postValue(rrInterval)
    }

    fun updateGsr(value: Float) {
        gsr.postValue(value)
    }
    
    fun updateEda(value: Float) {
        eda.postValue(value)
    }

    fun updateStatus(newStatus: String) {
        status.postValue(newStatus)
    }
}
