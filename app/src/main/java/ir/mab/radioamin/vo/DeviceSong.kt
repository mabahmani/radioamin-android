package ir.mab.radioamin.vo

import android.net.Uri

data class DeviceSong(
    val id: Long?,
    val albumId: Long?,
    val name: String?,
    val artistName: String?,
    val duration: Long?,
    val contentUri: Uri?,
    val data: String?
)
