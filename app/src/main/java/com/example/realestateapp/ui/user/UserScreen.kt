package com.example.realestateapp.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.realestateapp.model.Property
import com.example.realestateapp.repository.PropertyRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    searchQuery: String = "",
    selectedType: String = "All",
    selectedBudget: String = "All",
    selectedListing: String = "Buy",
    favorites: Set<String> = emptySet(),
    paddingValues: PaddingValues = PaddingValues(),
    recentlyViewed: List<Property> = emptyList(),
    onListingTypeChange: (String) -> Unit = {},
    onFavoriteToggle: (String) -> Unit = {},
    onPropertyClick: (Property) -> Unit = {}
) {
    var allProperties by remember { mutableStateOf(emptyList<Property>()) }
    var loading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var showAllProperties by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        allProperties = PropertyRepository.getAllFromFirestore()
        loading = false
    }

    val properties = allProperties.filter { p ->
        val matchSearch = searchQuery.isEmpty() ||
                p.location.contains(searchQuery, ignoreCase = true) ||
                p.price.contains(searchQuery, ignoreCase = true) ||
                p.title.contains(searchQuery, ignoreCase = true)
        val matchType = selectedType == "All" || p.type == selectedType
        val matchBudget = when (selectedBudget) {
            "Under 5M"  -> p.priceValue < 5_000_000L
            "5M-10M"    -> p.priceValue in 5_000_000L..10_000_000L
            "Above 10M" -> p.priceValue > 10_000_000L
            else        -> true
        }
        val matchListing = p.listingType == selectedListing
        matchSearch && matchType && matchBudget && matchListing
    }

    // Show all properties screen
    if (showAllProperties) {
        AllPropertiesScreen(
            properties = properties,
            favorites = favorites,
            selectedListing = selectedListing,
            onFavoriteToggle = onFavoriteToggle,
            onPropertyClick = onPropertyClick,
            onBack = { showAllProperties = false }
        )
        return
    }

    if (loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1B2A))
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFFDAA520))
                Spacer(Modifier.height(12.dp))
                Text("Loading properties...", color = Color(0xFFDAA520), fontSize = 14.sp)
            }
        }
        return
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                allProperties = PropertyRepository.getAllFromFirestore()
                isRefreshing = false
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1B2A)),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            // ── Greeting ──────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Dreams Find",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Home",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Find your perfect property in Ethiopia",
                        color = Color(0xFF4A90D9),
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── Buy / Rent Toggle ─────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf("Buy", "Rent").forEach { type ->
                        val selected = selectedListing == type
                        Button(
                            onClick = { onListingTypeChange(type) },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) Color(0xFFDAA520) else Color(0xFF1A3C5E),
                                contentColor = if (selected) Color(0xFF0D1B2A) else Color.White
                            )
                        ) {
                            Text(
                                text = if (type == "Buy") "🏠 Buy" else "🔑 Rent",
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // ── Recently Viewed ───────────────────────────────────────────
            if (recentlyViewed.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recently Viewed",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${recentlyViewed.size} properties",
                            color = Color(0xFF4A90D9),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(recentlyViewed) { p ->
                            Card(
                                onClick = { onPropertyClick(p) },
                                modifier = Modifier.width(140.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E))
                            ) {
                                Column {
                                    Box(modifier = Modifier.fillMaxWidth().height(90.dp)) {
                                        if (p.imageUri.isNotEmpty()) {
                                            AsyncImage(
                                                model = p.imageUri,
                                                contentDescription = p.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color(0xFF0D1B2A))
                                                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                                                contentAlignment = Alignment.Center
                                            ) { Text("🏠", fontSize = 28.sp) }
                                        }
                                    }
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = p.title,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = p.price,
                                            color = Color(0xFFDAA520),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            // ── Hero card ─────────────────────────────────────────────────
            if (properties.isNotEmpty()) {
                item {
                    val hero = properties.first()
                    val isFav = favorites.contains(hero.id)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(230.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { onPropertyClick(hero) }
                    ) {
                        when {
                            hero.imageUri.isNotEmpty() -> AsyncImage(
                                model = hero.imageUri,
                                contentDescription = hero.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            hero.imageRes != 0 -> Image(
                                painter = painterResource(id = hero.imageRes),
                                contentDescription = hero.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            else -> Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF1A3C5E))
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color(0xDD0D1B2A))
                                    )
                                )
                        )

                        IconButton(
                            onClick = { onFavoriteToggle(hero.id) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                                .size(34.dp)
                                .background(Color(0x88000000), CircleShape)
                        ) {
                            Icon(
                                if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isFav) Color.Red else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                HeroChip("🛏 ${hero.bedrooms} Bed")
                                HeroChip("🚿 ${hero.bathrooms} Bath")
                                HeroChip("📐 ${hero.sqft} Sqft")
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = hero.title,
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFFDAA520),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = hero.location,
                                    color = Color(0xFFDAA520),
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = hero.price,
                                    color = Color(0xFFDAA520),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }

            // ── Featured header ───────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Featured Properties",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${properties.size} ${if (selectedListing == "Rent") "rentals" else "properties"} available",
                            color = Color(0xFF4A90D9),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    TextButton(onClick = { showAllProperties = true }) {
                        Text("See More", color = Color(0xFFDAA520), fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // ── Empty state ───────────────────────────────────────────────
            if (properties.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                if (selectedListing == "Rent") "🔑" else "🏠",
                                fontSize = 48.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "No ${if (selectedListing == "Rent") "rentals" else "properties"} found",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDAA520)
                            )
                            Text(
                                text = "Try changing your filters",
                                color = Color.LightGray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // ── Featured property cards ───────────────────────────────────
            if (properties.isNotEmpty()) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(properties) { p ->
                            val isFav = favorites.contains(p.id)
                            Card(
                                onClick = { onPropertyClick(p) },
                                modifier = Modifier.width(160.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E))
                            ) {
                                Column {
                                    Box(modifier = Modifier.fillMaxWidth().height(110.dp)) {
                                        when {
                                            p.imageUri.isNotEmpty() -> AsyncImage(
                                                model = p.imageUri,
                                                contentDescription = p.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                            )
                                            p.imageRes != 0 -> Image(
                                                painter = painterResource(id = p.imageRes),
                                                contentDescription = p.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                            )
                                            else -> Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color(0xFF0D1B2A))
                                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                            )
                                        }

                                        IconButton(
                                            onClick = { onFavoriteToggle(p.id) },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .size(26.dp)
                                                .background(Color(0x88000000), CircleShape)
                                        ) {
                                            Icon(
                                                if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                contentDescription = null,
                                                tint = if (isFav) Color.Red else Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = p.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = Color(0xFF4A90D9),
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Text(
                                                text = p.location,
                                                fontSize = 10.sp,
                                                color = Color(0xFF4A90D9),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = p.price,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFDAA520)
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("🛏 ${p.bedrooms}", fontSize = 10.sp, color = Color.LightGray)
                                            Text(p.type, fontSize = 10.sp, color = Color(0xFF4A90D9))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

// ── All Properties Screen ─────────────────────────────────────────────────────
@Composable
fun AllPropertiesScreen(
    properties: List<Property>,
    favorites: Set<String>,
    selectedListing: String,
    onFavoriteToggle: (String) -> Unit,
    onPropertyClick: (Property) -> Unit,
    onBack: () -> Unit
) {
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
                    text = if (selectedListing == "Rent") "All Rentals" else "All Properties",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${properties.size} available",
                    color = Color(0xFF4A90D9),
                    fontSize = 11.sp
                )
            }
        }

        if (properties.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (selectedListing == "Rent") "🔑" else "🏠", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "No properties found",
                        color = Color(0xFFDAA520),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(properties) { p ->
                    val isFav = favorites.contains(p.id)
                    Card(
                        onClick = { onPropertyClick(p) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3C5E))
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            // Image
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0D1B2A))
                            ) {
                                when {
                                    p.imageUri.isNotEmpty() -> AsyncImage(
                                        model = p.imageUri,
                                        contentDescription = p.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    p.imageRes != 0 -> Image(
                                        painter = painterResource(id = p.imageRes),
                                        contentDescription = p.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    else -> Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) { Text("🏠", fontSize = 28.sp) }
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = p.title,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = p.price,
                                    color = Color(0xFFDAA520),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color(0xFF4A90D9),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = p.location,
                                        color = Color(0xFF4A90D9),
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("🛏 ${p.bedrooms}", fontSize = 11.sp, color = Color.LightGray)
                                    Text("🚿 ${p.bathrooms}", fontSize = 11.sp, color = Color.LightGray)
                                    Text("📐 ${p.sqft}", fontSize = 11.sp, color = Color.LightGray)
                                }
                                Spacer(Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF0D1B2A)
                                    ) {
                                        Text(
                                            p.type,
                                            fontSize = 10.sp,
                                            color = Color(0xFF4A90D9),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (p.listingType == "Rent") Color(0xFF1A3C5E) else Color(0xFF0D3B1A)
                                    ) {
                                        Text(
                                            if (p.listingType == "Rent") "🔑 Rent" else "🏠 Sale",
                                            fontSize = 10.sp,
                                            color = if (p.listingType == "Rent") Color(0xFF4A90D9) else Color(0xFF4CAF50),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            // Favorite button
                            IconButton(
                                onClick = { onFavoriteToggle(p.id) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0x22FFFFFF), CircleShape)
                            ) {
                                Icon(
                                    if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (isFav) Color.Red else Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroChip(text: String) {
    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xAA0D1B2A)) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun InfoChip(text: String) {
    Surface(shape = RoundedCornerShape(20.dp), color = Color(0x88000000)) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
