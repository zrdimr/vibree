package com.example.wearablecollector.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip

@Composable
fun ProfileScreen(
    avgHr: String,
    hrv: String,
    onNavigateToSettings: () -> Unit,
    onEditProfile: () -> Unit,
    onHistory: () -> Unit,
    onLogout: () -> Unit
) {
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val user = auth.currentUser

    val context = androidx.compose.ui.platform.LocalContext.current
    val userPrefs = androidx.compose.runtime.remember { com.example.wearablecollector.utils.UserPreferences(context) }
    
    // Force refresh of state when revisiting (basic approach)
    // In a real app we'd observe a data store or shared ViewModel
    val gender = userPrefs.gender
    val birthday = userPrefs.birthday

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
                .border(2.dp, VibreeNeonPink, CircleShape),
            contentAlignment = Alignment.Center
        ) {
             if (user?.photoUrl != null) {
                coil.compose.AsyncImage(
                    model = user.photoUrl,
                    contentDescription = "Profile Photo",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
             } else {
                 Text(
                     text = user?.displayName?.take(1)?.uppercase() ?: "U",
                     style = MaterialTheme.typography.headlineLarge,
                     color = Color.White
                 )
             }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(user?.displayName ?: "User", style = MaterialTheme.typography.titleLarge, color = Color.White)
        
        // extra details
        if (gender.isNotEmpty()) {
            Text(text = gender, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        if (birthday > 0) {
            // Quick format for display
             Text(text = "Born: $birthday", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Vibe: Resonant", color = VibreeNeonPink)

        Spacer(modifier = Modifier.height(20.dp))

        // Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ProfileStat(avgHr, "Last HR")
            ProfileStat(hrv, "HRV")
            ProfileStat("12", "Matches")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Menu
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MenuItem("Edit Profile", onClick = onEditProfile)
            MenuItem("History", onClick = onHistory)
            MenuItem("Settings", onClick = onNavigateToSettings)
            MenuItem("Logout", onClick = onLogout)
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
fun MenuItem(text: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Medium)
    }
}
