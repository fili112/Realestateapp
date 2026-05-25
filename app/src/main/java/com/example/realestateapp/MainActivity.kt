package com.example.realestateapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.realestateapp.model.Property
import com.example.realestateapp.repository.PropertyRepository
import com.example.realestateapp.ui.NotificationScreen
import com.example.realestateapp.ui.ProfileScreen
import com.example.realestateapp.ui.SplashScreen
import com.example.realestateapp.ui.admin.AdminScreen
import com.example.realestateapp.ui.auth.LoginScreen
import com.example.realestateapp.ui.theme.RealEstateAppTheme
import com.example.realestateapp.ui.user.PropertyDetailScreen
import com.example.realestateapp.ui.user.UserScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

enum class BottomTab { HOME, FAVORITES, PROFILE }

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : androidx.fragment.app.FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ── Back press handler ────────────────────────────────────────
        // Will be controlled by Compose state via backHandler below

        setContent {
            RealEstateAppTheme {
                var showSplash by remember { mutableStateOf(true) }
                var role by remember { mutableStateOf("") }
                var searchQuery by remember { mutableStateOf("") }
                var showLogoutDialog by remember { mutableStateOf(false) }
                var selectedProperty by remember { mutableStateOf<Property?>(null) }
                var showFilters by remember { mutableStateOf(false) }
                var selectedType by remember { mutableStateOf("All") }
                var selectedBudget by remember { mutableStateOf("All") }
                var selectedListing by remember { mutableStateOf("Buy") }
                var currentTab by remember { mutableStateOf(BottomTab.HOME) }
                var favorites by remember { mutableStateOf(setOf<String>()) }
                var recentlyViewed by remember { mutableStateOf(listOf<Property>()) }
                var showNotifications by remember { mutableStateOf(false) }
                var hasUnreadNotifications by remember { mutableStateOf(false) }
                var backPressedOnce by remember { mutableStateOf(false) }

                // ── Smart back navigation ──────────────────────────────────
                androidx.activity.compose.BackHandler {
                    when {
                        // Close notification screen
                        showNotifications -> showNotifications = false
                        // Close property detail
                        selectedProperty != null -> selectedProperty = null
                        // Close filters
                        showFilters -> showFilters = false
                        // Go back to HOME tab if on another tab
                        currentTab != BottomTab.HOME && role != "admin" -> currentTab = BottomTab.HOME
                        // On home screen or admin — double press to exit
                        else -> {
                            if (backPressedOnce) {
                                // Exit app
                                this@MainActivity.finish()
                            } else {
                                backPressedOnce = true
                                Toast.makeText(
                                    this@MainActivity,
                                    "Press back again to exit",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    backPressedOnce = false
                                }, 2000)
                            }
                        }
                    }
                }

                val user = Firebase.auth.currentUser
                val userName = user?.displayName?.split(" ")?.firstOrNull()
                    ?: user?.email?.substringBefore("@") ?: "User"

                // Load favorites
                LaunchedEffect(role) {
                    if (role.isNotEmpty() && user != null) {
                        try {
                            val doc = Firebase.firestore.collection("users")
                                .document(user.uid).get().await()
                            @Suppress("UNCHECKED_CAST")
                            val savedFavorites = doc.get("favorites") as? List<String> ?: emptyList()
                            favorites = savedFavorites.toSet()
                        } catch (_: Exception) { }
                    }
                }

                // Check unread notifications
                LaunchedEffect(Unit) {
                    try {
                        val snap = Firebase.firestore.collection("notifications")
                            .whereEqualTo("read", false)
                            .limit(1)
                            .get()
                            .await()
                        hasUnreadNotifications = !snap.isEmpty
                    } catch (_: Exception) { }
                }

                if (showSplash) {
                    SplashScreen(onFinished = { showSplash = false })
                    return@RealEstateAppTheme
                }

                if (showLogoutDialog) {
                    AlertDialog(
                        onDismissRequest = { showLogoutDialog = false },
                        title = { Text("Logout") },
                        text = { Text("Are you sure you want to logout?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    Firebase.auth.signOut()
                                    role = ""
                                    searchQuery = ""
                                    favorites = emptySet()
                                    showLogoutDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3C5E))
                            ) { Text("Logout") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showLogoutDialog = false }) {
                                Text("Cancel", color = Color(0xFFDAA520))
                            }
                        }
                    )
                }

                when {
                    role.isEmpty() -> LoginScreen(onLoginSuccess = { role = it })

                    showNotifications -> NotificationScreen(
                        onBack = { showNotifications = false }
                    )

                    selectedProperty != null -> PropertyDetailScreen(
                        property = selectedProperty!!,
                        onBack = { selectedProperty = null }
                    )

                    role == "admin" -> Scaffold(
                        topBar = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF0D1B2A), Color(0xFF1A3C5E))
                                        )
                                    )
                                    .statusBarsPadding()
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🏠", fontSize = 22.sp)
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text("Admin Panel", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                            Text("Manage listings", color = Color(0xFFDAA520), fontSize = 11.sp)
                                        }
                                    }
                                    Button(
                                        onClick = { showLogoutDialog = true },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFDAA520),
                                            contentColor = Color(0xFF0D1B2A)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ExitToApp,
                                            contentDescription = "Logout",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text("Logout", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            AdminScreen()
                        }
                    }

                    else -> Scaffold(
                        topBar = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF0D1B2A), Color(0xFF1A3C5E))
                                        )
                                    )
                                    .statusBarsPadding()
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = when (currentTab) {
                                                BottomTab.HOME -> "Hello, $userName 👋"
                                                BottomTab.FAVORITES -> "Saved Properties"
                                                BottomTab.PROFILE -> "My Profile"
                                            },
                                            color = Color.White,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = when (currentTab) {
                                                BottomTab.HOME -> "Find your dream home"
                                                BottomTab.FAVORITES -> "Properties you loved"
                                                BottomTab.PROFILE -> "Manage your account"
                                            },
                                            color = Color(0xFFDAA520),
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Notification bell
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(Color(0xFF1A3C5E), RoundedCornerShape(12.dp))
                                            .border(1.dp, Color(0xFFDAA520).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconButton(onClick = {
                                            showNotifications = true
                                            hasUnreadNotifications = false
                                        }) {
                                            Icon(
                                                Icons.Default.Notifications,
                                                contentDescription = "Notifications",
                                                tint = Color(0xFFDAA520),
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        if (hasUnreadNotifications) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(Color(0xFFE53935), CircleShape)
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = (-6).dp, y = 6.dp)
                                            )
                                        }
                                    }
                                }

                                if (currentTab == BottomTab.HOME) {
                                    Spacer(Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        placeholder = {
                                            Text("Search by city or title...", color = Color(0xFFDAA520), fontSize = 13.sp)
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFDAA520))
                                        },
                                        trailingIcon = {
                                            TextButton(onClick = { showFilters = !showFilters }) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = if (showFilters) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                        contentDescription = null,
                                                        tint = Color(0xFFDAA520),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(Modifier.width(2.dp))
                                                    Text("Filter", color = Color(0xFFDAA520), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFFDAA520),
                                            unfocusedBorderColor = Color(0xFF4A90D9),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            cursorColor = Color(0xFFDAA520)
                                        ),
                                        singleLine = true
                                    )

                                    if (showFilters) {
                                        Spacer(Modifier.height(10.dp))
                                        Text("Property Type", color = Color(0xFFDAA520), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Spacer(Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            listOf("All", "Apartment", "House", "Villa", "Commercial", "Land").forEach { type ->
                                                FilterChip(
                                                    selected = selectedType == type,
                                                    onClick = { selectedType = type },
                                                    label = { Text(type, fontSize = 12.sp) },
                                                    leadingIcon = if (selectedType == type) {
                                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                                    } else null,
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = Color(0xFFDAA520),
                                                        selectedLabelColor = Color(0xFF0D1B2A),
                                                        containerColor = Color(0x33FFFFFF),
                                                        labelColor = Color.White
                                                    )
                                                )
                                            }
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text("Budget Range", color = Color(0xFFDAA520), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Spacer(Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            listOf("All", "Under 5M", "5M-10M", "Above 10M").forEach { budget ->
                                                FilterChip(
                                                    selected = selectedBudget == budget,
                                                    onClick = { selectedBudget = budget },
                                                    label = { Text(budget, fontSize = 11.sp) },
                                                    leadingIcon = if (selectedBudget == budget) {
                                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                                    } else null,
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = Color(0xFFDAA520),
                                                        selectedLabelColor = Color(0xFF0D1B2A),
                                                        containerColor = Color(0x33FFFFFF),
                                                        labelColor = Color.White
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                }
                            }
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = Color(0xFF0D1B2A),
                                tonalElevation = 8.dp
                            ) {
                                listOf(
                                    Triple(BottomTab.HOME, Icons.Default.Home, "Home"),
                                    Triple(BottomTab.FAVORITES, Icons.Default.Favorite, "Saved"),
                                    Triple(BottomTab.PROFILE, Icons.Default.Person, "Profile")
                                ).forEach { (tab, icon, label) ->
                                    NavigationBarItem(
                                        selected = currentTab == tab,
                                        onClick = { currentTab = tab },
                                        icon = {
                                            if (currentTab == tab) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(42.dp)
                                                        .background(Color(0xFFDAA520), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(icon, contentDescription = label, tint = Color(0xFF0D1B2A), modifier = Modifier.size(22.dp))
                                                }
                                            } else {
                                                Box {
                                                    Icon(icon, contentDescription = label, tint = Color.Gray, modifier = Modifier.size(22.dp))
                                                    if (tab == BottomTab.FAVORITES && favorites.isNotEmpty()) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(16.dp)
                                                                .background(Color(0xFFDAA520), CircleShape)
                                                                .align(Alignment.TopEnd)
                                                                .offset(x = 4.dp, y = (-4).dp),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = if (favorites.size > 9) "9+" else favorites.size.toString(),
                                                                color = Color(0xFF0D1B2A),
                                                                fontSize = 8.sp,
                                                                fontWeight = FontWeight.ExtraBold
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        label = {
                                            Text(
                                                label,
                                                fontSize = 11.sp,
                                                color = if (currentTab == tab) Color(0xFFDAA520) else Color.Gray
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color(0xFFDAA520),
                                            selectedTextColor = Color(0xFFDAA520),
                                            unselectedIconColor = Color.Gray,
                                            unselectedTextColor = Color.Gray,
                                            indicatorColor = Color.Transparent
                                        )
                                    )
                                }
                            }
                        }
                    ) { paddingValues ->
                        when (currentTab) {
                            BottomTab.HOME -> UserScreen(
                                searchQuery = searchQuery,
                                selectedType = selectedType,
                                selectedBudget = selectedBudget,
                                selectedListing = selectedListing,
                                favorites = favorites,
                                paddingValues = paddingValues,
                                recentlyViewed = recentlyViewed,
                                onListingTypeChange = { selectedListing = it },
                                onFavoriteToggle = { id ->
                                    favorites = if (favorites.contains(id)) favorites - id else favorites + id
                                    user?.let {
                                        Firebase.firestore.collection("users")
                                            .document(it.uid)
                                            .update("favorites", favorites.toList())
                                    }
                                },
                                onPropertyClick = { property ->
                                    recentlyViewed = (listOf(property) + recentlyViewed)
                                        .distinctBy { it.id }
                                        .take(5)
                                    selectedProperty = property
                                    user?.let {
                                        Firebase.firestore.collection("users")
                                            .document(it.uid)
                                            .update("viewedCount", FieldValue.increment(1))
                                    }
                                }
                            )
                            BottomTab.FAVORITES -> FavoritesScreen(
                                paddingValues = paddingValues,
                                favorites = favorites
                            )
                            BottomTab.PROFILE -> ProfileScreen(
                                paddingValues = paddingValues,
                                onLogout = { showLogoutDialog = true }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(paddingValues: PaddingValues, favorites: Set<String>) {
    var allProperties by remember { mutableStateOf(emptyList<Property>()) }

    LaunchedEffect(Unit) {
        allProperties = PropertyRepository.getAllFromFirestore()
    }

    val savedProperties = allProperties.filter { favorites.contains(it.id) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
            .padding(paddingValues)
    ) {
        if (savedProperties.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("❤️", fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("No saved properties yet", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Tap the heart on any listing to save it", color = Color(0xFF4A90D9), fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("${savedProperties.size} Saved Properties", color = Color(0xFFDAA520), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                }
                items(savedProperties) { p ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0D1B2A))
                            ) {
                                if (p.imageUri.isNotEmpty()) {
                                    AsyncImage(
                                        model = p.imageUri,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(p.price, color = Color(0xFFDAA520), fontSize = 14.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF4A90D9), modifier = Modifier.size(12.dp))
                                    Text(p.location, color = Color(0xFF4A90D9), fontSize = 12.sp)
                                }
                                Text("🛏 ${p.bedrooms} Bed  •  ${p.type}", color = Color.LightGray, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
