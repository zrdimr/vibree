package com.example.wearablecollector.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onPostCreated: (String, Boolean) -> Unit,
    onCancel: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Post") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Text("X") // Simple close icon
                    }
                },
                actions = {
                    Button(
                        onClick = { 
                            if (text.isNotBlank()) {
                                onPostCreated(text, isPublic) 
                            }
                        },
                        enabled = text.isNotBlank()
                    ) {
                        Text("Post")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("What's on your mind?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = 10
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text("Make Public?")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isPublic,
                    onCheckedChange = { isPublic = it }
                )
            }
            Text(
                text = if (isPublic) "Visible to everyone" else "Private diary entry",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
