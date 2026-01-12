package com.example.wearablecollector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.bluetooth.BluetoothDevice
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {

    private lateinit var bleManager: BleManager
    private lateinit var tvStatus: TextView
    private lateinit var tvHeartRate: TextView
    private lateinit var tvHrv: TextView
    private lateinit var tvGsr: TextView
    private lateinit var tvEda: TextView
    private lateinit var btnScan: Button
    private lateinit var btnQrScan: Button
    private lateinit var btnStop: Button
    private lateinit var lvDevices: ListView
    private lateinit var deviceAdapter: ArrayAdapter<String>
    private val scannedDevicesList = mutableListOf<BluetoothDevice>()

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val scannedContent = result.contents
            Toast.makeText(this, "Scanned: $scannedContent", Toast.LENGTH_LONG).show()
            // Basic simplistic MAC address check (format XX:XX:XX:XX:XX:XX)
            // In reality, might need more complex parsing depending on what the QR code actually holds
            if (scannedContent.matches(Regex("([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}"))) {
                 bleManager.connect(scannedContent)
            } else {
                 Toast.makeText(this, "Valid MAC Address not found in QR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.entries.all { it.value }
            if (allGranted) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions denied, BLE cannot function", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        tvHeartRate = findViewById(R.id.tvHeartRate)
        tvHrv = findViewById(R.id.tvHrv)
        tvGsr = findViewById(R.id.tvGsr)
        tvEda = findViewById(R.id.tvEda)
        btnScan = findViewById(R.id.btnScan)
        btnQrScan = findViewById(R.id.btnQrScan)
        btnStop = findViewById(R.id.btnStop)
        lvDevices = findViewById(R.id.lvDevices)

        deviceAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        lvDevices.adapter = deviceAdapter

        lvDevices.setOnItemClickListener { _, _, position, _ ->
            if (position < scannedDevicesList.size) {
                val device = scannedDevicesList[position]
                bleManager.connect(device)
            }
        }

        bleManager = BleManager(this)

        checkPermissions()

        btnScan.setOnClickListener {
            if (hasPermissions()) {
                bleManager.startScan()
            } else {
                checkPermissions()
            }
        }

        btnQrScan.setOnClickListener {
             val options = ScanOptions()
             options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
             options.setPrompt("Scan a watch QR code")
             options.setCameraId(0) 
             options.setBeepEnabled(false)
             barcodeLauncher.launch(options)
        }

        btnStop.setOnClickListener {
            bleManager.stopScan()
        }

        observeData()
    }

    private fun observeData() {
        SensorDataRepository.status.observe(this) { status ->
            tvStatus.text = "Status: $status"
        }

        SensorDataRepository.heartRate.observe(this) { hr ->
            tvHeartRate.text = "Heart Rate: $hr bpm"
        }

        SensorDataRepository.hrv.observe(this) { hrv ->
            tvHrv.text = "HRV (RR): $hrv ms"
        }

        SensorDataRepository.gsr.observe(this) { gsr ->
            tvGsr.text = "GSR: $gsr"
        }

        SensorDataRepository.eda.observe(this) { eda ->
            tvEda.text = "EDA: $eda"
        }

        SensorDataRepository.scannedDevices.observe(this) { devices ->
            scannedDevicesList.clear()
            scannedDevicesList.addAll(devices)
            
            val deviceNames = devices.map { device ->
                if (device.name != null) "${device.name} (${device.address})" else "Unknown (${device.address})"
            }
            
            deviceAdapter.clear()
            deviceAdapter.addAll(deviceNames)
            deviceAdapter.notifyDataSetChanged()
        }
    }

    private fun hasPermissions(): Boolean {
        val permissions = getRequiredPermissions()
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun checkPermissions() {
        if (!hasPermissions()) {
            requestPermissionLauncher.launch(getRequiredPermissions())
        }
    }

    private fun getRequiredPermissions(): Array<String> {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        return permissions.toTypedArray()
    }
}
