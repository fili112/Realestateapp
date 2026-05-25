package com.example.realestateapp.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.realestateapp.repository.PropertyRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.google.firebase.storage.storage
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.LockOpen




@Composable
fun SubScreenHeader(title: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(listOf(Color(0xFF0D1B2A), Color(0xFF1A3C5E)))
            )
            .statusBarsPadding()
            .height(56.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFFDAA520)
            )
        }
        Text(
            title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ProfileScreen(paddingValues: PaddingValues, onLogout: () -> Unit) {
    val user = Firebase.auth.currentUser
    var currentScreen by remember { mutableStateOf("") }
    var refreshKey by remember { mutableStateOf(0) }

    val currentUser = Firebase.auth.currentUser
    val userInitial = currentUser?.email?.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
    var userName by remember(refreshKey) {
        mutableStateOf(
            currentUser?.displayName?.split(" ")?.firstOrNull()
                ?: currentUser?.email?.substringBefore("@") ?: "User"
        )
    }

    var savedCount by remember { mutableStateOf(0) }
    var totalProperties by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        try {
            val doc = Firebase.firestore.collection("users")
                .document(user?.uid ?: "").get().await()
            @Suppress("UNCHECKED_CAST")
            val favs = doc.get("favorites") as? List<String> ?: emptyList()
            savedCount = favs.size
            totalProperties = PropertyRepository.getAllFromFirestore().size
        } catch (_: Exception) { }
    }

    when (currentScreen) {
        "edit" -> EditProfileScreen(
            onBack = { currentScreen = "" },
            user = user,
            onSaved = {
                refreshKey++
                currentScreen = ""
            }
        )
        "notifications" -> NotificationsScreen(onBack = { currentScreen = "" })
        "security"      -> SecurityScreen(onBack = { currentScreen = "" })
        "password"      -> ChangePasswordScreen(onBack = { currentScreen = "" })
        "about"         -> AboutScreen(onBack = { currentScreen = "" })
        "contact"       -> ContactScreen(onBack = { currentScreen = "" })
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0D1B2A))
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {

                // ── Hero Header ───────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF1A3C5E), Color(0xFF0D1B2A))
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .offset(x = (-60).dp, y = (-60).dp)
                            .background(Color(0xFFDAA520).copy(alpha = 0.06f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 50.dp, y = (-30).dp)
                            .background(Color(0xFF4A90D9).copy(alpha = 0.06f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.BottomStart)
                            .offset(x = 30.dp, y = 30.dp)
                            .background(Color(0xFFDAA520).copy(alpha = 0.04f), CircleShape)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(92.dp)
                                    .background(
                                        Brush.sweepGradient(
                                            listOf(
                                                Color(0xFFDAA520),
                                                Color(0xFFFFD700),
                                                Color(0xFFB8860B),
                                                Color(0xFFDAA520)
                                            )
                                        ),
                                        CircleShape
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .size(82.dp)
                                    .background(Color(0xFF0D1B2A), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    userInitial,
                                    color = Color(0xFFDAA520),
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                        }

                        Spacer(Modifier.width(20.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userName,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = user?.email ?: "",
                                color = Color(0xFF4A90D9),
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                            Spacer(Modifier.height(8.dp))
                            val joinDate = remember {
                                val timestamp = user?.metadata?.creationTimestamp ?: 0L
                                if (timestamp == 0L) "Member"
                                else {
                                    val sdf = java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault())
                                    "Member since ${sdf.format(java.util.Date(timestamp))}"
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFF4A90D9).copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = Color(0xFF4A90D9),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        joinDate,
                                        color = Color(0xFF4A90D9),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                        }

                        IconButton(
                            onClick = { currentScreen = "edit" },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color(0xFFDAA520).copy(alpha = 0.15f),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color(0xFFDAA520),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0xFFDAA520).copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                Spacer(Modifier.height(16.dp))
                var viewedCount by remember { mutableStateOf(0) }

                LaunchedEffect(Unit) {
                    try {
                        val doc = Firebase.firestore.collection("users")
                            .document(user?.uid ?: "").get().await()
                        viewedCount = (doc.getLong("viewedCount") ?: 0L).toInt()
                    } catch (_: Exception) { }
                }


                // ── Stats Row ─────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        value = savedCount.toString(),
                        label = "Saved",
                        icon = Icons.Default.Favorite,
                        color = Color(0xFFE53935),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = totalProperties.toString(),
                        label = "Listings",
                        icon = Icons.Default.Home,
                        color = Color(0xFFDAA520),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = viewedCount.toString(),
                        label = "Viewed",
                        icon = Icons.Default.Visibility,
                        color = Color(0xFF4A90D9),
                        modifier = Modifier.weight(1f)
                    )

                }

                Spacer(Modifier.height(24.dp))

                // ── Account Settings ──────────────────────────────────────
                Text(
                    "Account Settings",
                    color = Color(0xFFDAA520),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(10.dp))

                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    label = "Edit Profile",
                    subtitle = "Update your name and phone",
                    iconBg = Color(0xFF1A3C5E),
                    iconTint = Color(0xFFDAA520),
                    onClick = { currentScreen = "edit" }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    label = "Notifications",
                    subtitle = "Manage your alerts",
                    iconBg = Color(0xFF1A2E1A),
                    iconTint = Color(0xFF4CAF50),
                    onClick = { currentScreen = "notifications" }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Security,
                    label = "Security",
                    subtitle = "Two-factor & login alerts",
                    iconBg = Color(0xFF1A1A2E),
                    iconTint = Color(0xFF4A90D9),
                    onClick = { currentScreen = "security" }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Lock,
                    label = "Change Password",
                    subtitle = "Reset your password via email",
                    iconBg = Color(0xFF2E1A1A),
                    iconTint = Color(0xFFE53935),
                    onClick = { currentScreen = "password" }
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "More",
                    color = Color(0xFFDAA520),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(10.dp))

                ProfileMenuItem(
                    icon = Icons.Default.Info,
                    label = "About Us",
                    subtitle = "Version 1.0.0",
                    iconBg = Color(0xFF1A2E2E),
                    iconTint = Color(0xFF4A90D9),
                    onClick = { currentScreen = "about" }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Phone,
                    label = "Contact Support",
                    subtitle = "We're here to help",
                    iconBg = Color(0xFF1A2E1A),
                    iconTint = Color(0xFF4CAF50),
                    onClick = { currentScreen = "contact" }
                )

                Spacer(Modifier.height(24.dp))

                // ── Logout ────────────────────────────────────────────────
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onLogout() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1A1A)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Logout",
                            color = Color(0xFFE53935),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    "Ethiopia Real Estate v1.0.0",
                    color = Color(0xFF4A90D9).copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, color = Color(0xFF4A90D9), fontSize = 11.sp)
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    subtitle: String,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color.Gray, fontSize = 11.sp)
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF4A90D9),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    user: FirebaseUser?,
    onSaved: () -> Unit = {}
) {
    var displayName by remember { mutableStateOf(user?.displayName ?: "") }
    var phone by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var uploading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }
    var photoUrl by remember { mutableStateOf(user?.photoUrl?.toString() ?: "") }
    val scope = rememberCoroutineScope()
    val isVerified = user?.isEmailVerified == true

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            uploading = true
            val storageRef = com.google.firebase.Firebase.storage
                .reference
                .child("profile_photos/${user?.uid}.jpg")

            storageRef.putFile(it)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        photoUrl = downloadUri.toString()
                        // Save photo URL to Firebase Auth
                        val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                            setPhotoUri(downloadUri)
                        }
                        user?.updateProfile(profileUpdates)
                        // Save to Firestore
                        com.google.firebase.Firebase.firestore
                            .collection("users")
                            .document(user?.uid ?: "")
                            .update("photoUrl", downloadUri.toString())
                        uploading = false
                        message = "✅ Photo updated"
                        isSuccess = true
                    }
                }
                .addOnFailureListener { e ->
                    uploading = false
                    message = "❌ ${e.message}"
                    isSuccess = false
                }
        }
    }

    LaunchedEffect(Unit) {
        try {
            val doc = com.google.firebase.Firebase.firestore
                .collection("users")
                .document(user?.uid ?: "").get().await()
            phone = doc.getString("phone") ?: ""
            val savedPhoto = doc.getString("photoUrl") ?: ""
            if (savedPhoto.isNotEmpty()) photoUrl = savedPhoto
        } catch (_: Exception) { }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
    ) {
        SubScreenHeader("Edit Profile", onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── Avatar Hero Section ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1A3C5E), Color(0xFF0D1B2A))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .offset(x = (-60).dp, y = (-40).dp)
                        .background(Color(0xFFDAA520).copy(alpha = 0.05f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 30.dp, y = (-20).dp)
                        .background(Color(0xFF4A90D9).copy(alpha = 0.05f), CircleShape)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") }
                    ) {
                        // Gold ring
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .background(
                                    Brush.sweepGradient(
                                        listOf(
                                            Color(0xFFDAA520),
                                            Color(0xFFFFD700),
                                            Color(0xFFB8860B),
                                            Color(0xFFDAA520)
                                        )
                                    ),
                                    CircleShape
                                )
                        )
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(82.dp)
                                .background(Color(0xFF0D1B2A), CircleShape)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (photoUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            } else {
                                Text(
                                    text = displayName.firstOrNull()?.uppercaseChar()?.toString()
                                        ?: user?.email?.firstOrNull()?.uppercaseChar()?.toString()
                                        ?: "U",
                                    color = Color(0xFFDAA520),
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        // Camera badge
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(Color(0xFFDAA520), CircleShape)
                                .align(Alignment.BottomEnd)
                                .offset(x = (-2).dp, y = (-2).dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uploading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = Color(0xFF0D1B2A),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF0D1B2A),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (uploading) "Uploading..." else "Tap to change photo",
                        color = Color(0xFF4A90D9),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                Text(
                    "Personal Information",
                    color = Color(0xFFDAA520),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Full name
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Text(
                            "Full Name",
                            color = Color(0xFFDAA520),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFDAA520), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            TextField(
                                value = displayName,
                                onValueChange = { displayName = it },
                                placeholder = { Text("Enter your full name", color = Color.Gray, fontSize = 14.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color(0xFFDAA520),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Email (read only)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF12263A)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Text(
                            "Email Address",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(text = user?.email ?: "", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isVerified) Color(0xFF1A2E1A) else Color(0xFF2E2A1A)
                            ) {
                                Text(
                                    if (isVerified) "✅ Verified" else "⚠ Not Verified",
                                    color = if (isVerified) Color(0xFF4CAF50) else Color(0xFFDAA520),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Phone
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Text(
                            "Phone Number",
                            color = Color(0xFFDAA520),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFFDAA520), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            TextField(
                                value = phone,
                                onValueChange = { phone = it },
                                placeholder = { Text("+251 9XX XXX XXX", color = Color.Gray, fontSize = 14.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color(0xFFDAA520),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Status message
                if (message.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSuccess) Color(0xFF1A2E1A) else Color(0xFF2E1A1A)
                        )
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(if (isSuccess) "✅" else "❌", fontSize = 18.sp)
                            Spacer(Modifier.width(10.dp))
                            Text(message, color = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFE53935), fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Save button
                Button(
                    onClick = {
                        scope.launch {
                            saving = true
                            message = ""
                            try {
                                val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                                    setDisplayName(displayName)
                                }
                                user?.updateProfile(profileUpdates)?.await()
                                com.google.firebase.Firebase.firestore
                                    .collection("users")
                                    .document(user?.uid ?: "")
                                    .set(
                                        mapOf(
                                            "displayName" to displayName,
                                            "phone" to phone,
                                            "email" to (user?.email ?: "")
                                        ),
                                        com.google.firebase.firestore.SetOptions.merge()
                                    ).await()
                                isSuccess = true
                                message = "Profile updated successfully"
                                delay(800)
                                onSaved()
                            } catch (e: Exception) {
                                isSuccess = false
                                message = e.message ?: "Failed to update profile"
                            }
                            saving = false
                        }
                    },
                    enabled = !saving && !uploading && displayName.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDAA520),
                        contentColor = Color(0xFF0D1B2A),
                        disabledContainerColor = Color(0xFFDAA520).copy(alpha = 0.4f)
                    )
                ) {
                    if (saving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF0D1B2A), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Saving...", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDAA520))
                ) {
                    Text("Cancel", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}


@Composable
fun SecurityScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var savedCount by remember { mutableStateOf(0) }
    var totalProperties by remember { mutableStateOf(0) }

    var loginAlerts by remember { mutableStateOf(true) }
    var biometric by remember { mutableStateOf(false) }
    var biometricStatus by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val user = Firebase.auth.currentUser

    LaunchedEffect(Unit) {
        try {
            // Reload to get latest email verification status
            Firebase.auth.currentUser?.reload()?.await()

            val doc = Firebase.firestore.collection("users")
                .document(user?.uid ?: "").get().await()
            val prefs = doc.get("securityPrefs") as? Map<*, *>
            if (prefs != null) {
                loginAlerts = prefs["loginAlerts"] as? Boolean ?: true
                biometric   = prefs["biometric"]   as? Boolean ?: false
            }
        } catch (_: Exception) { }
        loading = false
    }


    fun savePrefs() {
        scope.launch {
            try {
                Firebase.firestore.collection("users")
                    .document(user?.uid ?: "")
                    .set(
                        mapOf(
                            "securityPrefs" to mapOf(
                                "loginAlerts" to loginAlerts,
                                "biometric"   to biometric
                            )
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    ).await()
            } catch (_: Exception) { }
        }
    }

    fun launchBiometric() {
        val biometricManager = androidx.biometric.BiometricManager.from(context)
        val canAuthenticate = biometricManager.canAuthenticate(
            androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        when (canAuthenticate) {
            androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = androidx.core.content.ContextCompat.getMainExecutor(context)
                val fragmentActivity = context as? androidx.fragment.app.FragmentActivity
                if (fragmentActivity == null) {
                    biometricStatus = "❌ Cannot launch biometric"
                    return
                }
                val prompt = androidx.biometric.BiometricPrompt(
                    fragmentActivity,
                    executor,
                    object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(
                            result: androidx.biometric.BiometricPrompt.AuthenticationResult
                        ) {
                            biometric = true
                            biometricStatus = "✅ Fingerprint enabled successfully"
                            savePrefs()
                        }
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            biometric = false
                            biometricStatus = "❌ $errString"
                        }
                        override fun onAuthenticationFailed() {
                            biometricStatus = "❌ Authentication failed"
                        }
                    }
                )
                val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Fingerprint Authentication")
                    .setSubtitle("Confirm your fingerprint to enable")
                    .setAllowedAuthenticators(
                        androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                    .build()
                prompt.authenticate(promptInfo)
            }
            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                biometricStatus = "❌ No fingerprint hardware found"
            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                biometricStatus = "❌ Fingerprint temporarily unavailable. Unlock phone first and try again"

            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                biometricStatus = "❌ No fingerprints enrolled. Go to Settings > Security"
            else ->
                biometricStatus = "❌ Code: $canAuthenticate"

        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
    ) {
        SubScreenHeader("Security", onBack)

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFDAA520))
            }
            return@Column
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                "Security Settings",
                color = Color(0xFFDAA520),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Email verification
            var verificationSent by remember { mutableStateOf(false) }
            val isVerified = Firebase.auth.currentUser?.isEmailVerified == true

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    Color(0xFFDAA520).copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📧", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Email Verification",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                if (isVerified) "Your email is verified"
                                else "Verify your email address",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                        // Status badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isVerified) Color(0xFF1A2E1A) else Color(0xFF2E2A1A)
                        ) {
                            Text(
                                if (isVerified) "✅ Done" else "⚠ Pending",
                                color = if (isVerified) Color(0xFF4CAF50) else Color(0xFFDAA520),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Send verification button — only show if not verified
                    if (!isVerified) {
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                Firebase.auth.currentUser?.sendEmailVerification()
                                    ?.addOnSuccessListener { verificationSent = true }
                            },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDAA520),
                                contentColor = Color(0xFF0D1B2A)
                            )
                        ) {
                            Text(
                                if (verificationSent) "✅ Email Sent!" else "Send Verification Email",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        if (verificationSent) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Check your inbox and click the link to verify",
                                color = Color(0xFF4A90D9),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            NotificationToggleItem(
                icon = "🔔",
                title = "Login Alerts",
                subtitle = "Get notified when a new device logs in",
                checked = loginAlerts,
                onToggle = { loginAlerts = !loginAlerts; savePrefs() }
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Biometric",
                color = Color(0xFFDAA520),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    Color(0xFFDAA520).copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👆", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Fingerprint Login",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                "Use fingerprint to unlock the app",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = biometric,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    launchBiometric()
                                } else {
                                    biometric = false
                                    biometricStatus = "Fingerprint disabled"
                                    savePrefs()
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF0D1B2A),
                                checkedTrackColor = Color(0xFFDAA520),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color(0xFF0D1B2A)
                            )
                        )
                    }

                    if (biometricStatus.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (biometricStatus.startsWith("✅"))
                                    Color(0xFF1A2E1A) else Color(0xFF2E1A1A)
                            )
                        ) {
                            Text(
                                biometricStatus,
                                color = if (biometricStatus.startsWith("✅"))
                                    Color(0xFF4CAF50) else Color(0xFFE53935),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    val user = Firebase.auth.currentUser
    var newListings by remember { mutableStateOf(true) }
    var priceDrops by remember { mutableStateOf(false) }
    var agentMessages by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val doc = Firebase.firestore.collection("users")
                .document(user?.uid ?: "").get().await()
            val prefs = doc.get("notificationPrefs") as? Map<*, *>
            if (prefs != null) {
                newListings   = prefs["newListings"]   as? Boolean ?: true
                priceDrops    = prefs["priceDrops"]    as? Boolean ?: false
                agentMessages = prefs["agentMessages"] as? Boolean ?: true
            }
        } catch (_: Exception) { }
        loading = false
    }

    fun savePrefs() {
        scope.launch {
            saving = true
            try {
                Firebase.firestore.collection("users")
                    .document(user?.uid ?: "")
                    .set(
                        mapOf(
                            "notificationPrefs" to mapOf(
                                "newListings"   to newListings,
                                "priceDrops"    to priceDrops,
                                "agentMessages" to agentMessages
                            )
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    ).await()
            } catch (_: Exception) { }
            saving = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
    ) {
        SubScreenHeader("Notifications", onBack)

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFDAA520))
            }
            return@Column
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            if (saving) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(12.dp), color = Color(0xFFDAA520), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Saving...", color = Color(0xFFDAA520), fontSize = 12.sp)
                }
            }
            Text("Push Notifications", color = Color(0xFFDAA520), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
            NotificationToggleItem(icon = "🏠", title = "New Listings", subtitle = "Get notified when new properties are added", checked = newListings, onToggle = { newListings = !newListings; savePrefs() })
            NotificationToggleItem(icon = "📉", title = "Price Drops", subtitle = "Alert when property prices are reduced", checked = priceDrops, onToggle = { priceDrops = !priceDrops; savePrefs() })
            NotificationToggleItem(icon = "💬", title = "Agent Messages", subtitle = "Receive messages from our agents", checked = agentMessages, onToggle = { agentMessages = !agentMessages; savePrefs() })
        }
    }
}

@Composable
fun NotificationToggleItem(icon: String, title: String, subtitle: String, checked: Boolean, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(42.dp).background(Color(0xFFDAA520).copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Text(icon, fontSize = 20.sp) }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, color = Color.Gray, fontSize = 11.sp, lineHeight = 16.sp)
            }
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = checked, onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF0D1B2A),
                    checkedTrackColor = Color(0xFFDAA520),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color(0xFF0D1B2A)
                )
            )
        }
    }
}

@Composable
fun NotificationItem(title: String, subtitle: String, checked: Boolean, onToggle: () -> Unit) {
    NotificationToggleItem(icon = "🔔", title = title, subtitle = subtitle, checked = checked, onToggle = onToggle)
}

@Composable
fun ChangePasswordScreen(onBack: () -> Unit) {
    val user = Firebase.auth.currentUser
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
    ) {
        SubScreenHeader("Change Password", onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Spacer(Modifier.height(8.dp))

            // Info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFDAA520).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔒", fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Change Password", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Enter your current password to set a new one", color = Color.Gray, fontSize = 11.sp)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Current Password",
                color = Color(0xFFDAA520),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Old password
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFDAA520), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    TextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        placeholder = { Text("Enter current password", color = Color.Gray, fontSize = 14.sp) },
                        visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFDAA520),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                        Icon(
                            if (oldPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "New Password",
                color = Color(0xFFDAA520),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // New password
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color(0xFFDAA520), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    TextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = { Text("Enter new password", color = Color.Gray, fontSize = 14.sp) },
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFDAA520),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(
                            if (newPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Confirm new password
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color(0xFFDAA520), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    TextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = { Text("Confirm new password", color = Color.Gray, fontSize = 14.sp) },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFDAA520),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Password strength indicator
            if (newPassword.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                val strength = when {
                    newPassword.length < 6 -> Pair("Weak", Color(0xFFE53935))
                    newPassword.length < 10 -> Pair("Medium", Color(0xFFDAA520))
                    else -> Pair("Strong", Color(0xFF4CAF50))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    when {
                                        strength.first == "Weak" && index == 0 -> Color(0xFFE53935)
                                        strength.first == "Medium" && index <= 1 -> Color(0xFFDAA520)
                                        strength.first == "Strong" -> Color(0xFF4CAF50)
                                        else -> Color(0xFF1A3C5E)
                                    }
                                )
                        )
                    }
                    Text(strength.first, color = strength.second, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Message
            if (message.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSuccess) Color(0xFF1A2E1A) else Color(0xFF2E1A1A)
                    )
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isSuccess) "✅" else "❌", fontSize = 18.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(message, color = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFE53935), fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Change button
            Button(
                onClick = {
                    when {
                        oldPassword.isEmpty() -> { message = "Enter your current password"; isSuccess = false }
                        newPassword.isEmpty() -> { message = "Enter a new password"; isSuccess = false }
                        newPassword.length < 6 -> { message = "Password must be at least 6 characters"; isSuccess = false }
                        newPassword != confirmPassword -> { message = "Passwords do not match"; isSuccess = false }
                        oldPassword == newPassword -> { message = "New password must be different"; isSuccess = false }
                        else -> {
                            scope.launch {
                                saving = true
                                message = ""
                                try {
                                    // Re-authenticate with old password
                                    val credential = com.google.firebase.auth.EmailAuthProvider
                                        .getCredential(user?.email ?: "", oldPassword)
                                    user?.reauthenticate(credential)?.await()
                                    // Update to new password
                                    user?.updatePassword(newPassword)?.await()
                                    isSuccess = true
                                    message = "Password changed successfully"
                                    oldPassword = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                } catch (e: Exception) {
                                    isSuccess = false
                                    message = when {
                                        e.message?.contains("wrong-password") == true ||
                                                e.message?.contains("invalid-credential") == true ->
                                            "Current password is incorrect"
                                        else -> e.message ?: "Failed to change password"
                                    }
                                }
                                saving = false
                            }
                        }
                    }
                },
                enabled = !saving,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDAA520),
                    contentColor = Color(0xFF0D1B2A),
                    disabledContainerColor = Color(0xFFDAA520).copy(alpha = 0.4f)
                )
            ) {
                if (saving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF0D1B2A), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Changing...", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                } else {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Change Password", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDAA520))
            ) {
                Text("Cancel", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}


@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0D1B2A))) {
        SubScreenHeader("About Us", onBack)
        Column(
            modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier.size(90.dp).background(
                    Brush.radialGradient(listOf(Color(0xFFDAA520), Color(0xFFB8860B))),
                    RoundedCornerShape(24.dp)
                ),
                contentAlignment = Alignment.Center
            ) { Text("🏠", fontSize = 44.sp) }
            Spacer(Modifier.height(16.dp))
            Text("Ethiopia Real Estate", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Your trusted property partner", color = Color(0xFF4A90D9), fontSize = 13.sp)
            Spacer(Modifier.height(20.dp))
            Text(
                "We are Ethiopia's leading real estate platform, connecting buyers, sellers and renters across the country.",
                color = Color.LightGray, lineHeight = 22.sp
            )
            Spacer(Modifier.height(24.dp))
            listOf("Version" to "1.0.0", "Location" to "Addis Ababa, Ethiopia", "Email" to "info@realestate.com").forEach { (label, value) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E))
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text(label, color = Color(0xFFDAA520), modifier = Modifier.weight(1f))
                        Text(value, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ContactScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0D1B2A))) {
        SubScreenHeader("Contact Support", onBack)
        Column(modifier = Modifier.padding(24.dp)) {
            Spacer(Modifier.height(8.dp))
            ContactItem("📞", "Call Us", "+251 911 000 000") {
                context.startActivity(Intent(Intent.ACTION_DIAL, "tel:+251911000000".toUri()))
            }
            ContactItem("📧", "Email Us", "support@realestate.com") {
                context.startActivity(Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:support@realestate.com".toUri()
                })
            }
            ContactItem("🌐", "Visit Website", "www.realestate.com") {
                context.startActivity(Intent(Intent.ACTION_VIEW, "https://www.example.com".toUri()))
            }
        }
    }
}

@Composable
fun ContactItem(emoji: String, label: String, value: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label, color = Color(0xFFDAA520), fontWeight = FontWeight.SemiBold)
                Text(value, color = Color.LightGray, fontSize = 13.sp)
            }
        }
    }
}


