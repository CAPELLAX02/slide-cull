package com.capellax.slidescull.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EntryScreen(
    onRemoveAdsClick: () -> Unit,
    onTryFreeClick: () -> Unit
) {
    // Arka plan için renk animasyonu
    val infiniteTransition = rememberInfiniteTransition()
    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF673AB7),
        targetValue = Color(0xFFFFC107),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val color2 by infiniteTransition.animateColor(
        initialValue = Color(0xFF2196F3),
        targetValue = Color(0xFFFF5722),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val gradientBrush = Brush.linearGradient(listOf(color1, color2))

    // İçerik animasyonu
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        contentVisible = true
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        // Arka plan
        Box(
            modifier = Modifier
                .background(gradientBrush)
        )

        // Ortadaki içerik
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Başlık
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn() + scaleIn(initialScale = 0.8f)
            ) {
                Text(
                    text = "Welcome to SlideCull",
                    fontSize = 34.sp,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Açıklama
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(800)) + scaleIn(initialScale = 0.8f)
            ) {
                Text(
                    text = "Organize & clean your gallery with fun swipes!",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // "Try Free" butonu (Outlined, keskin köşe)
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(1000)) + scaleIn(initialScale = 0.5f)
            ) {
                Button(
                    onClick = onTryFreeClick,
                    modifier = Modifier.fillMaxWidth().clip(RectangleShape),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
                ) {
                    Text("Try Free", color = color1)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Remove Ads" butonu (Dolu, keskin köşe)
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(1200)) + scaleIn(initialScale = 0.5f)
            ) {
                Button(
                    onClick = onRemoveAdsClick,
                    modifier = Modifier.fillMaxWidth().clip(RectangleShape),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Remove Ads – One Time Payment", color = color2)
                }
            }
        }
    }
}
