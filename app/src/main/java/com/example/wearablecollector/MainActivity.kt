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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.example.wearablecollector.R
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private lateinit var bleManager: BleManager
    private val scannedDevicesList = mutableListOf<BluetoothDevice>()

    // Simple navigation state
    private enum class Screen {
        LOADING, LOGIN, DASHBOARD, MATCH, SEARCH, PROFILE, EDIT_PROFILE, VITALS, ACTIVITY, SETTINGS, HISTORY
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
    
    // Google Sign In Launcher
    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        bleManager = BleManager(this)
        checkPermissions()
        
        // Initialize Auth
        val auth = FirebaseAuth.getInstance()


        setContent {
            VibreeTheme {
                // Navigation State - Start with Loading
                var currentScreen by remember { mutableStateOf(Screen.LOADING) }

                // Observe LiveData
                val status by SensorDataRepository.status.observeAsState("Disconnected")
                val heartRate by SensorDataRepository.heartRate.observeAsState(0)
                val hrv by SensorDataRepository.hrv.observeAsState(0)
                val stress by SensorDataRepository.stressLevel.observeAsState(0)

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        // Hide TopBar on Loading and Login screens
                        if (currentScreen != Screen.LOADING && currentScreen != Screen.LOGIN) {
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
                                            else -> ""
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
                        }
                    },
                    bottomBar = {
                        if (currentScreen != Screen.LOADING && currentScreen != Screen.LOGIN && currentScreen != Screen.MATCH) {
                            NavigationBar(
                                containerColor = com.example.wearablecollector.ui.theme.VibreeBlack.copy(alpha = 0.95f),
                                contentColor = com.example.wearablecollector.ui.theme.VibreeNeonPink
                            ) {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                                    label = { Text("Home") },
                                    selected = currentScreen == Screen.DASHBOARD,
                                    onClick = { currentScreen = Screen.DASHBOARD },
                                    colors = NavigationBarItemDefaults.colors(selectedIconColor = com.example.wearablecollector.ui.theme.VibreeNeonPink, indicatorColor = com.example.wearablecollector.ui.theme.VibreeDarkPurple)
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                                    label = { Text("Search") },
                                    selected = currentScreen == Screen.SEARCH,
                                    onClick = { currentScreen = Screen.SEARCH },
                                    colors = NavigationBarItemDefaults.colors(selectedIconColor = com.example.wearablecollector.ui.theme.VibreeNeonPink, indicatorColor = com.example.wearablecollector.ui.theme.VibreeDarkPurple)

                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.Favorite, contentDescription = "Vitals") },
                                    label = { Text("Vitals") },
                                    selected = currentScreen == Screen.VITALS,
                                    onClick = { currentScreen = Screen.VITALS },
                                    colors = NavigationBarItemDefaults.colors(selectedIconColor = com.example.wearablecollector.ui.theme.VibreeNeonPink, indicatorColor = com.example.wearablecollector.ui.theme.VibreeDarkPurple)
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.List, contentDescription = "Activity") },
                                    label = { Text("Activity") },
                                    selected = currentScreen == Screen.ACTIVITY,
                                    onClick = { currentScreen = Screen.ACTIVITY },
                                    colors = NavigationBarItemDefaults.colors(selectedIconColor = com.example.wearablecollector.ui.theme.VibreeNeonPink, indicatorColor = com.example.wearablecollector.ui.theme.VibreeDarkPurple)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            Screen.LOADING -> LoadingScreen(
                                onTimeout = {
                                    val currentUser = auth.currentUser
                                    if (currentUser != null) {
                                        currentScreen = Screen.DASHBOARD
                                    } else {
                                        currentScreen = Screen.LOGIN
                                    }
                                }
                            )
                            Screen.LOGIN -> LoginScreen(
                                onLoginClick = { startGoogleSignIn() }
                            )
                            Screen.DASHBOARD -> DashboardScreen(
                                stressLevel = stress.toString(),
                                hrv = hrv.toString(),
                                heartRate = heartRate.toString(),
                                onNavigateToMatch = { currentScreen = Screen.MATCH },
                                onNavigateToVitals = { currentScreen = Screen.VITALS }
                            )
                            Screen.SEARCH -> SearchScreen()
                            Screen.PROFILE -> ProfileScreen(
                                avgHr = heartRate.toString(),
                                hrv = hrv.toString(),
                                onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                                onEditProfile = { currentScreen = Screen.EDIT_PROFILE },
                                onHistory = { currentScreen = Screen.HISTORY },
                                onLogout = {
                                    auth.signOut()
                                    currentScreen = Screen.LOGIN
                                }
                            )
                            Screen.EDIT_PROFILE -> EditProfileScreen(
                                onBack = { currentScreen = Screen.PROFILE }
                            )
                            Screen.MATCH -> MatchScreen(
                                onBack = { currentScreen = Screen.DASHBOARD }
                            )
                            Screen.VITALS -> VitalsScreen(
                                stressLevel = stress,
                                hrv = hrv,
                                heartRate = heartRate,
                                onBack = { currentScreen = Screen.DASHBOARD }
                            )
                            Screen.HISTORY -> HistoryScreen(
                                onBack = { currentScreen = Screen.PROFILE }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Auth Helper Functions
    private fun startGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) 
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        signInLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Welcome ${FirebaseAuth.getInstance().currentUser?.displayName}", Toast.LENGTH_SHORT).show()
                    // Navigation to Dashboard will happen via state observation or could force recompose, 
                    // but for now let's just create a mechanism to trigger update. 
                    // Actual mechanism: The Loading screen logic already ran. 
                    // We need to switch screen state. 
                    // Since 'currentScreen' is inside setContent, we can't easily change it from here without a ViewModel or global state.
                    // REFACTOR: ideally use ViewModel. 
                    // HACK for MVP: Restart Activity or simple observer? 
                    // Actually, let's just recreate the activity for simplicity to re-trigger Loading -> Dashboard check.
                    recreate()
                } else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
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
