package com.example.wearablecollector

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.UUID

@SuppressLint("MissingPermission") // Permissions are handled in UI
class BleManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val scanner = bluetoothAdapter?.bluetoothLeScanner
    private var bluetoothGatt: BluetoothGatt? = null
    private var isScanning = false
    private val handler = Handler(Looper.getMainLooper())

    // Standard Heart Rate UUIDs
    private val HR_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
    private val HR_CHARACTERISTIC_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
    private val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    // Placeholder for Custom GSR/EDA UUIDs (Replace with actual Device UUIDs)
    // Example: Shimmer or Empatica UUIDs
    private val CUSTOM_SERVICE_UUID = UUID.fromString("0000181a-0000-1000-8000-00805f9b34fb") // Example Env Sensing

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            Log.d("BleManager", "Found device: ${device.name} ${device.address}")
            
            if (device.name != null) {
                 SensorDataRepository.addScannedDevice(device)
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                SensorDataRepository.updateStatus("Connected to ${gatt.device.name}")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                SensorDataRepository.updateStatus("Disconnected")
                bluetoothGatt?.close()
                bluetoothGatt = null
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val services = gatt.services
                for (service in services) {
                    if (service.uuid == HR_SERVICE_UUID) {
                        enableNotification(gatt, service, HR_CHARACTERISTIC_UUID)
                    }
                    // Add checks for other services here
                }
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            if (characteristic.uuid == HR_CHARACTERISTIC_UUID) {
                parseHeartRate(characteristic)
            }
            // Parse other traits
        }
    }

    fun startScan() {
        if (isScanning || scanner == null) return
        SensorDataRepository.updateStatus("Scanning...")
        SensorDataRepository.clearScannedDevices()
        isScanning = true
        scanner.startScan(scanCallback)
        
        // Stop scanning after 10 seconds
        handler.postDelayed({
             stopScan()
        }, 10000)
    }

    fun stopScan() {
        if (!isScanning || scanner == null) return
        scanner.stopScan(scanCallback)
        isScanning = false
        if (bluetoothGatt == null) {
            SensorDataRepository.updateStatus("Scan finished (No device connected)")
        }
    }

    fun connect(device: BluetoothDevice) {
        stopScan()
        SensorDataRepository.updateStatus("Connecting to ${device.name}...")
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    fun connect(address: String) {
        stopScan()
        try {
            val device = bluetoothAdapter?.getRemoteDevice(address)
            if (device != null) {
                connect(device)
            } else {
                SensorDataRepository.updateStatus("Error: Device not found")
            }
        } catch (e: IllegalArgumentException) {
            SensorDataRepository.updateStatus("Error: Invalid Address")
        }
    }

    private fun enableNotification(gatt: BluetoothGatt, service: BluetoothGattService, charUuid: UUID) {
        val characteristic = service.getCharacteristic(charUuid)
        if (characteristic != null) {
            gatt.setCharacteristicNotification(characteristic, true)
            val descriptor = characteristic.getDescriptor(CCCD_UUID)
            if (descriptor != null) {
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }
        }
    }

    private fun parseHeartRate(characteristic: BluetoothGattCharacteristic) {
        val flag = characteristic.properties
        var format = -1
        // Heart rate format bit: 0 = uint8, 1 = uint16
        // This logic in Android's standard documentation is bit checking slightly differently
        // byte[0] is flags.
        val flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)
        
        // Bit 0 is format
        format = if ((flags and 0x01) != 0) {
            BluetoothGattCharacteristic.FORMAT_UINT16
        } else {
            BluetoothGattCharacteristic.FORMAT_UINT8
        }
        
        val heartRate = characteristic.getIntValue(format, 1)
        Log.d("BleManager", "HR: $heartRate")
        SensorDataRepository.updateHeartRate(heartRate)
        
        // Bit 4 indicates if RR intervals are present
        if ((flags and 0x10) != 0) {
             // RR intervals can be multiple uint16 values
             // We just take the first one for simplicity or we might need to parse scan loop
             // Offset depends on if Energy Expended is present (Bit 3)
             var offset = if ((flags and 0x01) != 0) 3 else 2 // 1 byte flag + 1 or 2 byte HR
             if ((flags and 0x08) != 0) {
                 offset += 2 // Energy expended is 2 bytes
             }
             
             // Check if we have enough data
             val valueBytes = characteristic.value
             if (valueBytes.size >= offset + 2) {
                 val rr = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset)
                 SensorDataRepository.updateHrv(rr)
             }
        }
    }
}
