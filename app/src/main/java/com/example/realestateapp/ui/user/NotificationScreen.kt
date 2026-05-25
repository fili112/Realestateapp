package com.example.realestateapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class AppNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "info",   // "new_property", "price_drop", "info"
    val timestamp: Long = 0L,
    val read: Boolean = false
)

@Composable
fun NotificationScreen(onBack: () -> Unit) {
    var notifications by remember { mutableStateOf(emptyList<AppNotification>()) }
    var loading by remember { mutableStateOf(true) }
    val uid = Firebase.auth.currentUser?.uid

    LaunchedEffect(Unit) {
        try {
            val snap = Firebase.firestore
                .collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .await()
            notifications = snap.documents.mapNotNull { doc ->
                AppNotification(
                    id        = doc.id,
                    title     = doc.getString("title") ?: "",
                    message   = doc.getString("message") ?: "",
                    type      = doc.getString("type") ?: "info",
                    timestamp = doc.getLong("timestamp") ?: 0L,
                    read      = doc.getBoolean("read") ?: false
                )
            }
            // Mark all as read
            if (uid != null) {
                snap.documents.forEach { doc ->
                    Firebase.firestore.collection("notifications")
                        .document(doc.id)
                        .update("read", true)
                }
            }
        } catch (_: Exception) { }
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF0D1B2A), Color(0xFF1A3C5E))
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFFDAA520)
                )
            }
            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    "Notifications",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${notifications.size} notifications",
                    color = Color(0xFF4A90D9),
                    fontSize = 11.sp
                )
            }
        }

        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFDAA520))
            }
            return@Column
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔔", fontSize = 56.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No notifications yet",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "You'll be notified about new listings",
                        color = Color(0xFF4A90D9),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications) { notif ->
                    NotificationCard(notif)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notif: AppNotification) {
    val icon: ImageVector = when (notif.type) {
        "new_property" -> Icons.Default.Home
        "price_drop"   -> Icons.Default.TrendingDown
        else           -> Icons.Default.Notifications
    }
    val iconColor: Color = when (notif.type) {
        "new_property" -> Color(0xFFDAA520)
        "price_drop"   -> Color(0xFF4CAF50)
        else           -> Color(0xFF4A90D9)
    }

    val timeStr = remember(notif.timestamp) {
        if (notif.timestamp == 0L) ""
        else SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
            .format(Date(notif.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notif.read) Color(0xFF1A3C5E) else Color(0xFF12263A)
        ),
        elevation = CardDefaults.cardElevation(if (!notif.read) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notif.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notif.read) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFFDAA520), CircleShape)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = notif.message,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
                if (timeStr.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = timeStr,
                        color = Color(0xFF4A90D9),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
