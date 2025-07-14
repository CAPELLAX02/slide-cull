package com.capellax.slidescull.viewmodel

import android.app.Application
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.capellax.slidescull.model.GalleryImage
import kotlinx.coroutines.launch

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val _galleryImages = mutableStateListOf<GalleryImage>()
    val galleryImages: List<GalleryImage> get() = _galleryImages

    private val _deletedImages = mutableStateListOf<GalleryImage>()
    val deletedImages: List<GalleryImage> get() = _deletedImages

    // Geri al (undo) için stack
    private val _undoStack = mutableListOf<GalleryImage>()

    // Yüklenme durumu
    private val _isLoading = mutableStateOf(true)
    val isLoading: Boolean get() = _isLoading.value

    init {
        loadImages()
    }

    private fun loadImages() {
        _isLoading.value = true
        viewModelScope.launch {
            val images = mutableListOf<GalleryImage>()
            val projection = arrayOf(MediaStore.Images.Media._ID)
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            val resolver = getApplication<Application>().contentResolver
            val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val contentUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id.toString()
                    )
                    images.add(GalleryImage(id.toString(), contentUri.toString()))
                }
            }

            _galleryImages.clear()
            // Listeyi önce distinctBy ile ayıklıyoruz (id bazlı) sonra rastgele sıraya sokuyoruz
            val distinctImages = images.distinctBy { it.id }
            _galleryImages.addAll(distinctImages.shuffled())

            _isLoading.value = false
        }
    }


    fun swipeLeft(image: GalleryImage) {
        if (!_deletedImages.contains(image)) {
            _deletedImages.add(image)
            _galleryImages.remove(image)
            _undoStack.add(image)
        }
    }

    fun skip(image: GalleryImage) {
        _galleryImages.remove(image)
    }

    fun undoLastDelete() {
        val last = _undoStack.removeLastOrNull() ?: return
        _deletedImages.remove(last)
        _galleryImages.add(last)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun createDeleteRequest(selectedImages: List<GalleryImage>): IntentSender? {
        val resolver = getApplication<Application>().contentResolver
        val uris = selectedImages.map { Uri.parse(it.uri) }
        return try {
            MediaStore.createDeleteRequest(resolver, uris).intentSender
        } catch (e: SecurityException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    fun onDeleteConfirmed() {
        _deletedImages.clear()
        _undoStack.clear()
    }
}
