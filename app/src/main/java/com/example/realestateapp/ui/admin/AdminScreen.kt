package com.example.realestateapp.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.realestateapp.model.Property
import com.example.realestateapp.repository.PropertyRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var properties by remember { mutableStateOf(emptyList<Property>()) }
    var loading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var bedrooms by remember { mutableStateOf("") }
    var bathrooms by remember { mutableStateOf("") }
    var sqft by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Apartment") }
    var listingType by remember { mutableStateOf("Buy") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showForm by remember { mutableStateOf(false) }
    var editingProperty by remember { mutableStateOf<Property?>(null) }
    var pickedLat by remember { mutableStateOf(9.0192) }
    var pickedLng by remember { mutableStateOf(38.7525) }
    var locationPicked by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf("") }

    val imageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    LaunchedEffect(Unit) {
        properties = PropertyRepository.getAllFromFirestore()
        loading = false
    }

    suspend fun refresh() {
        properties = PropertyRepository.getAllFromFirestore()
    }

    suspend fun pullToRefresh() {
        isRefreshing = true
        properties = PropertyRepository.getAllFromFirestore()
        isRefreshing = false
    }

    fun clearForm() {
        title = ""; price = ""; location = ""; bedrooms = ""
        bathrooms = ""; sqft = ""; description = ""; imageUrl = ""
        selectedImageUri = null; uploadError = ""
        editingProperty = null; showForm = false
        selectedType = "Apartment"; listingType = "Buy"
        pickedLat = 9.0192; pickedLng = 38.7525
        locationPicked = false
    }

    fun loadForEdit(p: Property) {
        title = p.title; price = p.price; location = p.location
        bedrooms = p.bedrooms.toString()
        bathrooms = p.bathrooms.toString()
        sqft = p.sqft.toString()
        description = p.description
        selectedType = p.type
        listingType = p.listingType
        imageUrl = p.imageUri
        selectedImageUri = null
        pickedLat = p.latitude; pickedLng = p.longitude
        locationPicked = true
        editingProperty = p; showForm = true
    }

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0D1B2A)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFDAA520))
        }
        return
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { scope.launch { pullToRefresh() } },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0D1B2A)),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 110.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Manage Listings", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDAA520))
                        Text("${properties.size} properties", fontSize = 13.sp, color = Color(0xFF4A90D9))
                    }
                    FloatingActionButton(
                        onClick = { clearForm(); showForm = !showForm },
                        containerColor = Color(0xFFDAA520),
                        contentColor = Color(0xFF0D1B2A),
                        modifier = Modifier.size(48.dp)
                    ) { Icon(Icons.Default.Add, contentDescription = "Add") }
                }

                Spacer(Modifier.height(16.dp))

                val forSale = properties.count { it.listingType == "Buy" }
                val forRent = properties.count { it.listingType == "Rent" }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🏘", fontSize = 22.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(properties.size.toString(), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Total", color = Color(0xFF4A90D9), fontSize = 11.sp)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🏠", fontSize = 22.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(forSale.toString(), color = Color(0xFF4CAF50), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                            Text("For Sale", color = Color(0xFF4A90D9), fontSize = 11.sp)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔑", fontSize = 22.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(forRent.toString(), color = Color(0xFFDAA520), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                            Text("For Rent", color = Color(0xFF4A90D9), fontSize = 11.sp)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            if (showForm) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                if (editingProperty != null) "✏️ Edit Property" else "➕ Add New Property",
                                fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFDAA520)
                            )
                            Spacer(Modifier.height(12.dp))

                            // Image preview
                            val previewModel: Any? = selectedImageUri ?: imageUrl.ifEmpty { null }
                            if (previewModel != null) {
                                AsyncImage(
                                    model = previewModel,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(2.dp, Color(0xFFDAA520), RoundedCornerShape(12.dp))
                                )
                                Spacer(Modifier.height(8.dp))
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0D1B2A))
                                        .border(2.dp, Color(0xFF4A90D9), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("🖼️", fontSize = 28.sp)
                                        Text("No image selected", color = Color.Gray, fontSize = 12.sp)
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                            }

                            // Pick image from gallery
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { imageLauncher.launch("image/*") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF0D1B2A),
                                        contentColor = Color(0xFFDAA520)
                                    )
                                ) {
                                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(if (selectedImageUri != null) "✅ Selected" else "📷 Pick Image")
                                }

                                // Remove image button
                                if (selectedImageUri != null || imageUrl.isNotEmpty()) {
                                    OutlinedButton(
                                        onClick = {
                                            selectedImageUri = null
                                            imageUrl = ""
                                        },
                                        modifier = Modifier.weight(0.5f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color(0xFFFF5252)
                                        )
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // OR divider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF4A90D9))
                                Text(
                                    "  OR  ",
                                    color = Color.LightGray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF4A90D9))
                            }

                            Spacer(Modifier.height(12.dp))

                            // Image URL input field
                            OutlinedTextField(
                                value = imageUrl,
                                onValueChange = {
                                    imageUrl = it
                                    // Clear selected URI when URL is entered
                                    if (it.isNotEmpty()) {
                                        selectedImageUri = null
                                    }
                                },
                                label = { Text("Image URL", color = Color.LightGray) },
                                placeholder = { Text("https://example.com/image.jpg", color = Color.Gray, fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFFDAA520))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFDAA520),
                                    unfocusedBorderColor = Color(0xFF4A90D9),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color(0xFFDAA520),
                                    focusedLabelColor = Color(0xFFDAA520)
                                ),
                                singleLine = true
                            )

                            if (uploadError.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                Text(uploadError, color = Color.Red, fontSize = 12.sp)
                            }

                            Spacer(Modifier.height(12.dp))

                            // Listing Type
                            Text("Listing Type", fontSize = 13.sp, color = Color.LightGray, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                listOf("Buy", "Rent").forEach { type ->
                                    val selected = listingType == type
                                    Button(
                                        onClick = { listingType = type },
                                        modifier = Modifier.weight(1f).height(42.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selected) Color(0xFFDAA520) else Color(0xFF0D1B2A),
                                            contentColor = if (selected) Color(0xFF0D1B2A) else Color.White
                                        )
                                    ) {
                                        Text(
                                            text = if (type == "Buy") "🏠 For Sale" else "🔑 For Rent",
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))

                            // Property type chips
                            Text("Property Type", fontSize = 13.sp, color = Color.LightGray)
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Apartment", "House", "Villa", "Commercial", "Land").forEach { type ->
                                    FilterChip(
                                        selected = selectedType == type,
                                        onClick = { selectedType = type },
                                        label = { Text(type, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFDAA520),
                                            selectedLabelColor = Color(0xFF0D1B2A),
                                            containerColor = Color(0xFF0D1B2A),
                                            labelColor = Color.White
                                        )
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))

                            // Text fields
                            listOf(
                                Triple(title,       { v: String -> title = v },       "Title"),
                                Triple(price,       { v: String -> price = v },       "Price (e.g. ETB 4,500,000)"),
                                Triple(location,    { v: String -> location = v },    "Location"),
                                Triple(bedrooms,    { v: String -> bedrooms = v },    "Bedrooms"),
                                Triple(bathrooms,   { v: String -> bathrooms = v },   "Bathrooms"),
                                Triple(sqft,        { v: String -> sqft = v },        "Area (Sqft)"),
                                Triple(description, { v: String -> description = v }, "Description")
                            ).forEach { (value, onChange, label) ->
                                OutlinedTextField(
                                    value = value,
                                    onValueChange = onChange,
                                    label = { Text(label, color = Color.LightGray) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFDAA520),
                                        unfocusedBorderColor = Color(0xFF4A90D9),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        cursorColor = Color(0xFFDAA520),
                                        focusedLabelColor = Color(0xFFDAA520)
                                    )
                                )
                                Spacer(Modifier.height(8.dp))
                            }

                            // Map location picker
                            Text("📍 Pick Property Location", fontSize = 13.sp, color = Color.LightGray, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            Text("Tap on the map to set the exact location", fontSize = 11.sp, color = Color(0xFF4A90D9))
                            Spacer(Modifier.height(8.dp))

                            if (locationPicked) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A))
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDAA520), modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "Lat: ${"%.4f".format(pickedLat)}, Lng: ${"%.4f".format(pickedLng)}",
                                            color = Color(0xFFDAA520), fontSize = 12.sp, fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(2.dp, Color(0xFF4A90D9), RoundedCornerShape(12.dp))
                            ) {
                                AndroidView(
                                    factory = {
                                        Configuration.getInstance().userAgentValue = context.packageName
                                        val mapView = MapView(context)
                                        mapView.setTileSource(TileSourceFactory.MAPNIK)
                                        mapView.setMultiTouchControls(true)
                                        mapView.controller.setZoom(12.0)
                                        mapView.controller.setCenter(GeoPoint(pickedLat, pickedLng))
                                        val marker = Marker(mapView)
                                        marker.position = GeoPoint(pickedLat, pickedLng)
                                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        if (locationPicked) mapView.overlays.add(marker)
                                        val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                                            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                                pickedLat = p.latitude
                                                pickedLng = p.longitude
                                                locationPicked = true
                                                mapView.overlays.removeAll { it is Marker }
                                                val newMarker = Marker(mapView)
                                                newMarker.position = GeoPoint(pickedLat, pickedLng)
                                                newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                                newMarker.title = "Selected Location"
                                                mapView.overlays.add(newMarker)
                                                mapView.invalidate()
                                                return true
                                            }
                                            override fun longPressHelper(p: GeoPoint) = false
                                        })
                                        mapView.overlays.add(0, eventsOverlay)
                                        mapView
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (!locationPicked) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .background(Color(0xAA000000), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("👆 Tap to pin location", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { clearForm() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDAA520))
                                ) { Text("Cancel") }

                                Button(
                                    onClick = {
                                        scope.launch {
                                            saving = true
                                            uploadError = ""
                                            try {
                                                // Upload image if a new one was picked
                                                val finalImageUrl = if (selectedImageUri != null) {
                                                    val storageRef = Firebase.storage.reference
                                                        .child("properties/${System.currentTimeMillis()}.jpg")
                                                    storageRef.putFile(selectedImageUri!!).await()
                                                    storageRef.downloadUrl.await().toString()
                                                } else {
                                                    imageUrl // keep existing URL when editing
                                                }

                                                val priceVal = price.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L

                                                if (editingProperty != null) {
                                                    PropertyRepository.update(
                                                        editingProperty!!.copy(
                                                            title = title, price = price, priceValue = priceVal,
                                                            location = location,
                                                            bedrooms = bedrooms.toIntOrNull() ?: 0,
                                                            bathrooms = bathrooms.toIntOrNull() ?: 0,
                                                            sqft = sqft.toIntOrNull() ?: 0,
                                                            description = description,
                                                            imageUri = finalImageUrl, type = selectedType,
                                                            listingType = listingType,
                                                            latitude = pickedLat, longitude = pickedLng
                                                        )
                                                    )
                                                } else {
                                                    PropertyRepository.add(
                                                        Property(
                                                            title = title, price = price,
                                                            priceValue = priceVal, location = location,
                                                            bedrooms = bedrooms.toIntOrNull() ?: 0,
                                                            bathrooms = bathrooms.toIntOrNull() ?: 0,
                                                            sqft = sqft.toIntOrNull() ?: 0,
                                                            description = description,
                                                            imageUri = finalImageUrl, type = selectedType,
                                                            listingType = listingType,
                                                            latitude = pickedLat, longitude = pickedLng
                                                        )
                                                    )
                                                    // Notify users
                                                    Firebase.firestore.collection("notifications").add(
                                                        mapOf(
                                                            "title"     to "🏠 New Property Listed",
                                                            "message"   to "$title is now available in $location for $price",
                                                            "type"      to "new_property",
                                                            "timestamp" to System.currentTimeMillis(),
                                                            "read"      to false
                                                        )
                                                    )
                                                }
                                                clearForm()
                                                refresh()
                                            } catch (e: Exception) {
                                                uploadError = "Failed: ${e.message}"
                                            }
                                            saving = false
                                        }
                                    },
                                    enabled = !saving,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFDAA520), contentColor = Color(0xFF0D1B2A)
                                    )
                                ) {
                                    if (saving) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF0D1B2A), strokeWidth = 2.dp)
                                    } else {
                                        Text(if (editingProperty != null) "Update" else "Save", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }

            items(properties) { p ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(3.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF0D1B2A)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (p.imageUri.isNotEmpty()) {
                                AsyncImage(model = p.imageUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)))
                            } else {
                                Text("🏠", fontSize = 24.sp, textAlign = TextAlign.Center)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(p.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            Text(p.price, color = Color(0xFFDAA520), fontSize = 14.sp)
                            Text("📍 ${p.location}", color = Color(0xFF4A90D9), fontSize = 12.sp)
                            Text("🛏 ${p.bedrooms}  🚿 ${p.bathrooms}  📐 ${p.sqft} sqft", color = Color.LightGray, fontSize = 11.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF0D1B2A)) {
                                    Text(p.type, fontSize = 11.sp, color = Color(0xFF4A90D9), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (p.listingType == "Rent") Color(0xFF1A3C5E) else Color(0xFF0D3B1A)
                                ) {
                                    Text(
                                        if (p.listingType == "Rent") "🔑 For Rent" else "🏠 For Sale",
                                        fontSize = 11.sp,
                                        color = if (p.listingType == "Rent") Color(0xFF4A90D9) else Color(0xFF4CAF50),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        IconButton(onClick = { loadForEdit(p) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFFDAA520))
                        }
                        IconButton(onClick = {
                            scope.launch { PropertyRepository.delete(p.id); refresh() }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252))
                        }
                    }
                }
            }
        }
    }
}
