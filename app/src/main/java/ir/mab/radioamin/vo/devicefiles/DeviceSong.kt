package ir.mab.radioamin.vo.devicefiles

import android.net.Uri

data class DeviceSong(
    val id: Long?,
    val albumId: Long?,
    val name: String?,
    val artistName: String?,
    val albumName: String?,
    val duration: Long?,
    val contentUri: Uri,
    val data: String?
)
