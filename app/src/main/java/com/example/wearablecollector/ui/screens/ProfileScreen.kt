package com.example.wearablecollector.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wearablecollector.ui.theme.VibreeNeonPink
import com.example.wearablecollector.ui.theme.VibreeNeonPurple
import com.example.wearablecollector.ui.theme.TextGray

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.DarkGray, CircleShape)
                .border(2.dp, VibreeNeonPink, CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("You", style = MaterialTheme.typography.titleLarge, color = Color.White)
        Text("Vibe: Resonant", color = VibreeNeonPink)

        Spacer(modifier = Modifier.height(30.dp))

        // Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ProfileStat("72", "Avg HR")
            ProfileStat("45", "HRV")
            ProfileStat("12", "Matches")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Menu
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MenuItem("History")
            MenuItem("Settings")
            MenuItem("Logout")
        }
    }
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VibreeNeonPurple)
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextGray)
    }
}

@Composable
fun MenuItem(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Medium)
    }
}
