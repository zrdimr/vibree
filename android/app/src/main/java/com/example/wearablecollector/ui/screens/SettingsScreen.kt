package com.example.wearablecollector.ui.screens

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wearablecollector.SensorDataRepository
import com.example.wearablecollector.ui.theme.VibreeNeonPink
import com.example.wearablecollector.ui.theme.VibreeNeonPurple
import com.example.wearablecollector.ui.theme.TextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onStartScan: () -> Unit,
    onConnect: (String) -> Unit
) {
    val scannedDevices by SensorDataRepository.scannedDevices.observeAsState(initial = emptyList())
    val status by SensorDataRepository.status.observeAsState("Idle")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Settings", style = MaterialTheme.typography.titleLarge, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Bluetooth Connection", style = MaterialTheme.typography.titleMedium, color = VibreeNeonPink)
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Status: $status", color = TextGray, style = MaterialTheme.typography.bodyMedium)
        
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onStartScan,
            colors = ButtonDefaults.buttonColors(containerColor = VibreeNeonPurple),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Scan for Devices", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Available Devices", style = MaterialTheme.typography.titleMedium, color = VibreeNeonPink)
        Spacer(modifier = Modifier.height(8.dp))

        if (scannedDevices.isEmpty()) {
            Text("No devices found yet.", color = TextGray, modifier = Modifier.padding(8.dp))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(scannedDevices) { device ->
                    DeviceItem(device = device, onClick = { onConnect(device.address) })
                }
            }
        }
    }
}

@Composable
fun DeviceItem(device: BluetoothDevice, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = device.name ?: "Unknown Device",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = device.address,
                color = TextGray,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text("Connect", color = VibreeNeonPink)
    }
}
