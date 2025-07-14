package com.capellax.slidescull.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.capellax.slidescull.model.GalleryImage
import com.capellax.slidescull.ui.components.SwipeableCard
import com.capellax.slidescull.viewmodel.GalleryViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainSwipeScreen(
    viewModel: GalleryViewModel,
    onDone: (List<GalleryImage>) -> Unit
) {
    val images = viewModel.galleryImages
    val isLoading = viewModel.isLoading
    val deletedPhotos = viewModel.deletedImages

    // En az bir sola kaydırma yapıldığında Done butonunu göstermek için
    var hasLeftSwipeHappened by remember { mutableStateOf(false) }

    // Geri al butonuna basıldığında ters animasyon vermek için
    var justUndonePhoto by remember { mutableStateOf<GalleryImage?>(null) }

    // Tüm ekranı kaplayan beyaz arka plan:
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // <-- 1) TÜM EKRAN BEYAZ
    ) {
        // Eğer fotoğraflar yükleniyorsa
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading photos...")
            }
        } else {
            // AnimatedContent ile YALNIZCA son fotoğraf değişince animasyon
            AnimatedContent(
                targetState = images.lastOrNull(),
                transitionSpec = {
                    // Yeni foto aşağıdan kayarak + fadeIn (bounce’lı)
                    // Eski foto fadeOut ile çıkar
                    slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight / 2 },
                        animationSpec = tween(
                            durationMillis = 600,
                            easing = { t -> bounce(t) } // hafif zıplama
                        )
                    ) + fadeIn(
                        animationSpec = tween(400)
                    ) with fadeOut(tween(200))
                },
                label = "TopPhotoTransition"
            ) { topPhoto ->
                if (topPhoto != null) {
                    // Tek kart
                    SwipeableCard(
                        image = topPhoto,
                        onSwipedLeft = {
                            hasLeftSwipeHappened = true
                            viewModel.swipeLeft(topPhoto)
                        },
                        onSwipedRight = {
                            viewModel.skip(topPhoto)
                        },
                        // Geri alınan foto yeniden ekrana gelsin diye
                        undoAnimation = (justUndonePhoto == topPhoto),
                        showOverlayText = false
                    )
                } else {
                    // Liste boş
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No more photos!")
                    }
                }
            }
        }

        // Geri Al (Undo) Butonu
        if (deletedPhotos.isNotEmpty()) {
            IconButton(
                onClick = {
                    val undone = deletedPhotos.lastOrNull()
                    if (undone != null) {
                        viewModel.undoLastDelete()
                        justUndonePhoto = undone
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF44336)) // Kırmızı
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    tint = Color.White
                )
            }

            // Sol üstte "Çöp" simgesi, içinde badge ile silinen foto sayısı
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2196F3)), // Mavi
            ) {
                IconButton(
                    onClick = {
                        // Başka ekrana gidebilirsiniz (confirm_delete vs.)
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    BadgedBox(
                        badge = {
                            AnimatedContent(
                                targetState = deletedPhotos.size,
                                transitionSpec = {
                                    slideInVertically() + fadeIn() with
                                            slideOutVertically() + fadeOut()
                                }
                            ) { count ->
                                Badge {
                                    Text("$count")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Trash",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Done Butonu
        AnimatedVisibility(
            visible = hasLeftSwipeHappened && deletedPhotos.isNotEmpty(),
            enter = scaleIn(
                animationSpec = tween(700, easing = { BounceInterpolator(it) })
            ) + fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    onDone(deletedPhotos.toList())
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done.")
            }
        }
    }
}

// bounce fonksiyonları
fun BounceInterpolator(x: Float): Float {
    val t = x * 1.1226f
    return when {
        t < 0.3535f -> bounce(t)
        t < 0.7408f -> bounce(t - 0.54719f) + 0.7f
        t < 0.9644f -> bounce(t - 0.8526f) + 0.9f
        else -> bounce(t - 1.0435f) + 0.95f
    }
}

private fun bounce(t: Float): Float = t * t * 8.0f
