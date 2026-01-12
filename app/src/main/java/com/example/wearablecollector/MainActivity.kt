package com.example.wearablecollector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var bleManager: BleManager
    private lateinit var tvStatus: TextView
    private lateinit var tvHeartRate: TextView
    private lateinit var tvHrv: TextView
    private lateinit var tvGsr: TextView
    private lateinit var tvEda: TextView
    private lateinit var btnScan: Button
    private lateinit var btnStop: Button

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
        btnStop = findViewById(R.id.btnStop)

        bleManager = BleManager(this)

        checkPermissions()

        btnScan.setOnClickListener {
            if (hasPermissions()) {
                bleManager.startScan()
            } else {
                checkPermissions()
            }
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
