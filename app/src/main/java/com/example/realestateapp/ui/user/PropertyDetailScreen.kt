package com.example.realestateapp.ui.user

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.realestateapp.model.Property
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun PropertyDetailScreen(property: Property, onBack: () -> Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableStateOf(0) }

    // Build full image list: imageUri first, then extra images
    val allImages = buildList {
        if (property.imageUri.isNotEmpty()) add(property.imageUri)
        addAll(property.images.filter { it != property.imageUri })
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Interested in ${property.title}?") },
            text = {
                Text(
                    "Our agent will contact you shortly about " +
                            "this property at ${property.location} for ${property.price}."
                )
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3C5E))
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = Color(0xFFDAA520))
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0D1B2A))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── Image Gallery ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                // Main selected image
                if (allImages.isNotEmpty()) {
                    AsyncImage(
                        model = allImages[selectedImageIndex],
                        contentDescription = property.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (property.imageRes != 0) {
                    Image(
                        painter = painterResource(id = property.imageRes),
                        contentDescription = property.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF1A3C5E), Color(0xFF0D1B2A))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) { Text("🏠", fontSize = 80.sp) }
                }

                // Gradient overlay bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xFF0D1B2A)),
                                startY = 400f
                            )
                        )
                )

                // Top bar: back + share
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0x88000000), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    // Share button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0x88000000), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = {
                            val shareText = """
🏠 ${property.title} - ${property.price}
📍 ${property.location}
🛏 ${property.bedrooms} Beds | 🚿 ${property.bathrooms} Baths | 📐 ${property.sqft} Sqft
🏷 Type: ${property.type}

${if (property.description.isNotEmpty()) property.description else ""}

Check out this property on Ethiopia Real Estate!
                            """.trimIndent()

                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Property via"))
                        }) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Image counter badge
                if (allImages.size > 1) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 56.dp, end = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xAA000000)
                    ) {
                        Text(
                            "${selectedImageIndex + 1}/${allImages.size}",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Property type badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFDAA520)
                ) {
                    Text(
                        property.type,
                        color = Color(0xFF0D1B2A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            // ── Thumbnail strip ───────────────────────────────────────────
            if (allImages.size > 1) {
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(allImages) { index, url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = if (index == selectedImageIndex) 2.dp else 0.dp,
                                    color = if (index == selectedImageIndex) Color(0xFFDAA520) else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedImageIndex = index }
                        )
                    }
                }
            }

            // ── Content ───────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                Spacer(Modifier.height(16.dp))

                // Title + price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        property.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        property.price,
                        color = Color(0xFFDAA520),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Location
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF4A90D9),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(property.location, color = Color(0xFF4A90D9), fontSize = 14.sp)
                }

                Spacer(Modifier.height(20.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailStatCard("🛏", "${property.bedrooms}", "Beds", Modifier.weight(1f))
                    DetailStatCard("🚿", "${property.bathrooms}", "Baths", Modifier.weight(1f))
                    DetailStatCard("📐", "${property.sqft}", "Sqft", Modifier.weight(1f))
                }

                Spacer(Modifier.height(24.dp))

                // About section
                SectionTitle("About this property")
                Spacer(Modifier.height(10.dp))
                Text(
                    if (property.description.isNotEmpty()) property.description
                    else "A beautiful property in ${property.location}. Contact our agent for more details.",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    lineHeight = 24.sp
                )

                Spacer(Modifier.height(24.dp))

                // ── Map View ──────────────────────────────────────────────
                SectionTitle("Location on Map")
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    PropertyMapView(
                        latitude = property.latitude,
                        longitude = property.longitude,
                        title = property.title
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Open in Google Maps button
                OutlinedButton(
                    onClick = {
                        val uri = Uri.parse("geo:${property.latitude},${property.longitude}?q=${property.latitude},${property.longitude}(${property.title})")
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4A90D9))
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Open in Google Maps", fontSize = 13.sp)
                }

                Spacer(Modifier.height(24.dp))

                // Extra bottom padding so content clears the fixed buttons
                Spacer(Modifier.height(100.dp))
            }
        }

        // ── Bottom action buttons (fixed) ─────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xFF0D1B2A))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Call Agent button
                Button(
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:+251911000000"))
                        )
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDAA520),
                        contentColor = Color(0xFF0D1B2A)
                    )
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Call Agent", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                // Book via Telegram button
                Button(
                    onClick = {
                        val telegramUrl = "https://t.me/F1li_6" // 👈Telegram username
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl)))
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF229ED9),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Book Now", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

    } // end outer Box
}

@Composable
fun PropertyMapView(latitude: Double, longitude: Double, title: String) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            Configuration.getInstance().userAgentValue = context.packageName
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                controller.setCenter(GeoPoint(latitude, longitude))
                val marker = Marker(this)
                marker.position = GeoPoint(latitude, longitude)
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = title
                overlays.add(marker)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun SectionTitle(text: String) {
    Text(text, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color(0xFFDAA520))
    )
}

@Composable
fun DetailStatCard(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
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
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color(0xFF4A90D9), fontSize = 11.sp)
        }
    }
}
