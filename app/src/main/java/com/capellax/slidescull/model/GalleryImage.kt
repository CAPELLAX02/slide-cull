package com.capellax.slidescull.model

data class GalleryImage(
    val id: String,
    val uri: String,
) {
    override fun equals(other: Any?): Boolean {
        return other is GalleryImage && other.id == this.id
    }

    override fun hashCode(): Int = id.hashCode()
}
