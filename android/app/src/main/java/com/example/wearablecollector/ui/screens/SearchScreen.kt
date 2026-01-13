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
import com.example.wearablecollector.ui.theme.VibreeDarkPurple
import com.example.wearablecollector.ui.theme.VibreeNeonPink
import com.example.wearablecollector.ui.theme.VibreeNeonPurple
import com.example.wearablecollector.ui.theme.TextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search by Vibe or Name...", color = TextGray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Trending Vibes
        Text("Trending Vibes", color = VibreeNeonPink, style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VibeTag("Calm")
            VibeTag("Energetic")
            VibeTag("Focused")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent People
        Text("Recent People", color = VibreeNeonPink, style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        
        PersonItem(name = "Alex", vibe = "Resonant")
        PersonItem(name = "Jordan", vibe = "Syncing...")
    }
}

@Composable
fun VibeTag(text: String) {
    Box(
        modifier = Modifier
            .background(VibreeNeonPurple.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .border(1.dp, VibreeNeonPurple, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, color = Color.White, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun PersonItem(name: String, vibe: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.Gray, CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(name, color = Color.White, fontWeight = FontWeight.Medium)
            Text(vibe, color = TextGray, style = MaterialTheme.typography.bodySmall)
        }
    }
}
