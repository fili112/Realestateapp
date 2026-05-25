package com.example.realestateapp.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.material3.Surface


@Composable
fun SplashScreen(onFinished: () -> Unit) {

    // Animations
    val logoScale    = remember { Animatable(0f) }
    val logoAlpha    = remember { Animatable(0f) }
    val titleAlpha   = remember { Animatable(0f) }
    val titleOffset  = remember { Animatable(40f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val badgeAlpha   = remember { Animatable(0f) }
    val footerAlpha  = remember { Animatable(0f) }
    val ringScale    = remember { Animatable(0.6f) }
    val ringAlpha    = remember { Animatable(0f) }

    // Pulse animation for ring
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        // Ring appears
        ringAlpha.animateTo(1f, animationSpec = tween(400))
        ringScale.animateTo(1f, animationSpec = tween(500, easing = EaseOutBack))

        // Logo pops in
        logoScale.animateTo(1f, animationSpec = tween(600, easing = EaseOutBack))
        logoAlpha.animateTo(1f, animationSpec = tween(400))

        // Title slides up
        delay(200)
        titleAlpha.animateTo(1f, animationSpec = tween(500))
        titleOffset.animateTo(0f, animationSpec = tween(500, easing = EaseOutCubic))

        // Subtitle fades in
        delay(150)
        subtitleAlpha.animateTo(1f, animationSpec = tween(400))

        // Badge and footer
        delay(100)
        badgeAlpha.animateTo(1f, animationSpec = tween(400))
        footerAlpha.animateTo(1f, animationSpec = tween(600))

        delay(1800)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A1628),
                        Color(0xFF0D1B2A),
                        Color(0xFF1A3C5E)
                    )
                )
            )
    ) {
        // Background decorative circles
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-80).dp)
                .background(
                    Color(0xFFDAA520).copy(alpha = 0.04f),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = 40.dp)
                .background(
                    Color(0xFF4A90D9).copy(alpha = 0.05f),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 60.dp)
                .background(
                    Color(0xFF4A90D9).copy(alpha = 0.04f),
                    CircleShape
                )
        )

        // Main content
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Logo with ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.scale(pulseScale)
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(ringScale.value)
                        .alpha(ringAlpha.value)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFFDAA520).copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )
                // Gold ring
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(ringScale.value)
                        .alpha(ringAlpha.value)
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
                // Inner dark circle
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(logoScale.value)
                        .alpha(logoAlpha.value)
                        .background(Color(0xFF0D1B2A), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏠", fontSize = 44.sp)
                }
            }

            Spacer(Modifier.height(32.dp))

            // App name
            Text(
                text = "Ethiopia Real Estate",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .offset(y = titleOffset.value.dp)
            )

            Spacer(Modifier.height(8.dp))

            // Divider line
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(3.dp)
                    .alpha(subtitleAlpha.value)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, Color(0xFFDAA520), Color.Transparent)
                        )
                    )
            )

            Spacer(Modifier.height(10.dp))

            // Tagline
            Text(
                text = "Find your dream home",
                fontSize = 15.sp,
                color = Color(0xFFDAA520),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(subtitleAlpha.value)
            )

            Spacer(Modifier.height(24.dp))

            // Feature badges
            Row(
                modifier = Modifier.alpha(badgeAlpha.value),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SplashBadge("🏠 Buy")
                SplashBadge("🔑 Rent")
                SplashBadge("📍 Locate")
            }
        }

        // Bottom section
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(footerAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Loading dots
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { index ->
                    val dotAlpha by rememberInfiniteTransition(label = "dot$index")
                        .animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600, delayMillis = index * 200),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "dot$index"
                        )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .alpha(dotAlpha)
                            .background(Color(0xFFDAA520), CircleShape)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Powered by Ethiopia Real Estate",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun SplashBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1A3C5E)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
