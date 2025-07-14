package com.capellax.slidescull.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import coil.compose.rememberAsyncImagePainter
import com.capellax.slidescull.model.GalleryImage
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun SwipeableCard(
    image: GalleryImage,
    onSwipedLeft: () -> Unit,
    onSwipedRight: () -> Unit,
    showOverlayText: Boolean = false,
    undoAnimation: Boolean = false,
    enableGesture: Boolean = true,
    scaleFactor: Float = 1.0f,
    alphaFactor: Float = 1.0f
) {
    val offsetX = remember { Animatable(0f) }
    val swipeThreshold = 250f
    val scope = rememberCoroutineScope()

    // Undo animasyonu (soldan gelme)
    LaunchedEffect(image.id, undoAnimation) {
        if (undoAnimation) {
            offsetX.snapTo(-1000f)
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(300, easing = LinearEasing)
            )
        }
    }

    // Overlay text
    val labelText by derivedStateOf {
        if (!showOverlayText) ""
        else when {
            offsetX.value > 50f -> "SKIP"
            offsetX.value < -50f -> "DELETE"
            else -> ""
        }
    }
    val labelColor by derivedStateOf {
        if (!showOverlayText) Color.Transparent
        else if (offsetX.value < -50f) Color.Red else Color(0xFF4CAF50)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = offsetX.value
                rotationZ = offsetX.value / 30f
                scaleX = scaleFactor
                scaleY = scaleFactor
                alpha = alphaFactor
            }
            .pointerInput(enableGesture) {
                if (enableGesture) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consumeAllChanges()
                            scope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount.x)
                            }
                        },
                        onDragEnd = {
                            when {
                                offsetX.value > swipeThreshold -> {
                                    scope.launch {
                                        offsetX.animateTo(1000f, tween(300))
                                        onSwipedRight()
                                        offsetX.snapTo(0f)
                                    }
                                }
                                offsetX.value < -swipeThreshold -> {
                                    scope.launch {
                                        offsetX.animateTo(-1000f, tween(300))
                                        onSwipedLeft()
                                        offsetX.snapTo(0f)
                                    }
                                }
                                else -> {
                                    scope.launch {
                                        offsetX.animateTo(
                                            0f,
                                            spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
            .clip(RectangleShape), // kart kenarları keskin
        contentAlignment = Alignment.Center
    ) {
        // 2) HER KARTIN ARKA PLANI BEYAZ
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        )

        // Asıl görsel
        Image(
            painter = rememberAsyncImagePainter(image.uri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay
        AnimatedVisibility(
            visible = labelText.isNotEmpty(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Text(
                text = labelText,
                color = labelColor,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
