package com.example.wearablecollector

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.wearablecollector.ui.screens.DashboardScreen
import com.example.wearablecollector.ui.screens.MatchScreen
import com.example.wearablecollector.ui.theme.VibreeTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : ComponentActivity() {

    private lateinit var bleManager: BleManager
    private val scannedDevicesList = mutableListOf<BluetoothDevice>()

    // Simple navigation state
    private enum class Screen {
        DASHBOARD, MATCH
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val scannedContent = result.contents
            Toast.makeText(this, "Scanned: $scannedContent", Toast.LENGTH_LONG).show()
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
        
        bleManager = BleManager(this)
        checkPermissions()

        setContent {
            VibreeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }
                    
                    // Observe LiveData from Repository
                    val status by SensorDataRepository.status.observeAsState("Disconnected")
                    val heartRate by SensorDataRepository.heartRate.observeAsState("0")
                    val hrv by SensorDataRepository.hrv.observeAsState("0")
                    
                    when (currentScreen) {
                        Screen.DASHBOARD -> {
                            // We can pass data to Dashboard logic here
                            // For now using the static "Resonant" but triggering animations with HR could be next
                            DashboardScreen(
                                onNavigateToMatch = { currentScreen = Screen.MATCH }
                            )
                        }
                        Screen.MATCH -> {
                            MatchScreen(
                                onBack = { currentScreen = Screen.DASHBOARD }
                            )
                        }
                    }
                }
            }
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
    
    // TODO: Expose scanning functions to UI if needed
    fun startScan() {
        if (hasPermissions()) bleManager.startScan()
    }
}
