package ir.mab.radioamin.ui.listener

import android.graphics.Bitmap
import ir.mab.radioamin.vo.DeviceFileType

interface DeviceFilesMoreOnClickListeners {
    fun onShowOptions(id: Long,
               title: String,
               subtitle: String,
               thumbnail: Bitmap?,
               type: DeviceFileType)
}