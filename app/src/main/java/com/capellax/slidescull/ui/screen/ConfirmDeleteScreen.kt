package com.capellax.slidescull.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.capellax.slidescull.model.GalleryImage

@Composable
fun ConfirmDeleteScreen(
    initialImages: List<GalleryImage>,
    onConfirmDelete: (List<GalleryImage>) -> Unit,
    onCancel: () -> Unit
) {
    val selectedImages = remember { mutableStateListOf<GalleryImage>() }

    LaunchedEffect(Unit) {
        selectedImages.clear()
        selectedImages.addAll(initialImages)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Review photos to delete",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(16.dp)
        )

        // Opsiyonel: Tümünü Seç / Temizle
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            OutlinedButton(onClick = {
                selectedImages.clear()
                selectedImages.addAll(initialImages)
            }) {
                Text("Select All")
            }
            OutlinedButton(onClick = {
                selectedImages.clear()
            }) {
                Text("Clear All")
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(initialImages) { image ->
                val isSelected = selectedImages.contains(image)
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colors.primary else Color.Transparent
                )

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .aspectRatio(1f)
                        .border(
                            width = 2.dp,
                            color = borderColor
                        )
                        .clickable {
                            if (isSelected) {
                                selectedImages.remove(image)
                            } else {
                                selectedImages.add(image)
                            }
                        }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(image.uri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        )
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedButton(onClick = onCancel) {
                Text("Cancel")
            }
            Button(onClick = { onConfirmDelete(selectedImages.toList()) }) {
                Text("Delete ${selectedImages.size} Photo${if (selectedImages.size != 1) "s" else ""}")
            }
        }
    }
}
