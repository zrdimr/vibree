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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.wearablecollector.ui.screens.*
import com.example.wearablecollector.ui.theme.VibreeTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private lateinit var bleManager: BleManager
    private val scannedDevicesList = mutableListOf<BluetoothDevice>()

    // Simple navigation state
    private enum class Screen {
        DASHBOARD, MATCH, SEARCH, PROFILE, VITALS, ACTIVITY, SETTINGS
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
                // Navigation State
                var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }

                // Observe LiveData from Repository (keeping existing logic)
                val status by SensorDataRepository.status.observeAsState("Disconnected")
                val heartRate by SensorDataRepository.heartRate.observeAsState("0")
                val hrv by SensorDataRepository.hrv.observeAsState("0")

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        TopAppBar(
                            title = { 
                                Text(
                                    text = when(currentScreen) {
                                        Screen.DASHBOARD -> "Dashboard"
                                        Screen.SEARCH -> "Search"
                                        Screen.PROFILE -> "Profile"
                                        Screen.MATCH -> "Connections"
                                        Screen.VITALS -> "Vitals"
                                        Screen.ACTIVITY -> "Activity"
                                        Screen.SETTINGS -> "Settings"
                                    },
                                    color = Color.White
                                ) 
                            },
                            actions = {
                                IconButton(onClick = { currentScreen = Screen.PROFILE }) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(Color.Gray, CircleShape)
                                            .border(1.dp, com.example.wearablecollector.ui.theme.VibreeNeonPink, CircleShape)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.smallTopAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = Color.White
                            )
                        )
                    },
                    bottomBar = {
                        // Hide bottom bar on Match screen if desired, or keep it. 
                        // HTML design shows bottom bar on all screens except maybe match? 
                        // HTML Match screen has back button in header, but let's keep bottom nav for consistency unless requested.
                        if (currentScreen != Screen.MATCH) {
                            NavigationBar(
                                containerColor = com.example.wearablecollector.ui.theme.VibreeBlack.copy(alpha = 0.95f),
                                contentColor = com.example.wearablecollector.ui.theme.VibreeNeonPink
                            ) {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                                    label = { Text("Home") },
                                    selected = currentScreen == Screen.DASHBOARD,
                                    onClick = { currentScreen = Screen.DASHBOARD },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = com.example.wearablecollector.ui.theme.VibreeNeonPink,
                                        selectedTextColor = com.example.wearablecollector.ui.theme.VibreeNeonPink,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray,
                                        indicatorColor = com.example.wearablecollector.ui.theme.VibreeDarkPurple
                                    )
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                                    label = { Text("Search") },
                                    selected = currentScreen == Screen.SEARCH,
                                    onClick = { currentScreen = Screen.SEARCH },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = com.example.wearablecollector.ui.theme.VibreeNeonPink,
                                        selectedTextColor = com.example.wearablecollector.ui.theme.VibreeNeonPink,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray,
                                        indicatorColor = com.example.wearablecollector.ui.theme.VibreeDarkPurple
                                    )
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.Favorite, contentDescription = "Vitals") },
                                    label = { Text("Vitals") },
                                    selected = currentScreen == Screen.VITALS,
                                    onClick = { currentScreen = Screen.VITALS },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = com.example.wearablecollector.ui.theme.VibreeNeonPink,
                                        selectedTextColor = com.example.wearablecollector.ui.theme.VibreeNeonPink,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray,
                                        indicatorColor = com.example.wearablecollector.ui.theme.VibreeDarkPurple
                                    )
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.List, contentDescription = "Activity") },
                                    label = { Text("Activity") },
                                    selected = currentScreen == Screen.ACTIVITY,
                                    onClick = { currentScreen = Screen.ACTIVITY },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = com.example.wearablecollector.ui.theme.VibreeNeonPink,
                                        selectedTextColor = com.example.wearablecollector.ui.theme.VibreeNeonPink,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray,
                                        indicatorColor = com.example.wearablecollector.ui.theme.VibreeDarkPurple
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            Screen.DASHBOARD -> DashboardScreen(
                                onNavigateToMatch = { currentScreen = Screen.MATCH }
                            )
                            Screen.SEARCH -> SearchScreen()
                            Screen.PROFILE -> ProfileScreen(
                                avgHr = heartRate.toString(),
                                hrv = hrv.toString(),
                                onNavigateToSettings = { currentScreen = Screen.SETTINGS }
                            )
                            Screen.MATCH -> MatchScreen(
                                onBack = { currentScreen = Screen.DASHBOARD }
                            )
                            Screen.VITALS -> PlaceholderScreen("Vitals")
                            Screen.ACTIVITY -> PlaceholderScreen("Activity")
                            Screen.SETTINGS -> SettingsScreen(
                                onBack = { currentScreen = Screen.PROFILE },
                                onStartScan = { startScan() },
                                onConnect = { address -> bleManager.connect(address) }
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
