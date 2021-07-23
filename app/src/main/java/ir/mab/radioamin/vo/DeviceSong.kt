package ir.mab.radioamin.vo

import android.graphics.Bitmap
import android.net.Uri

data class DeviceSong(
    val id: Long?,
    val name: String?,
    val artistName: String?,
    val duration: Long?,
    val contentUri: Uri?,
    val thumbnail: Bitmap?
)
