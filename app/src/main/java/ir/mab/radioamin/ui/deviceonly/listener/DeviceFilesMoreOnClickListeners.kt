package ir.mab.radioamin.ui.deviceonly.listener

import android.graphics.Bitmap
import ir.mab.radioamin.vo.devicefiles.DeviceFileType

interface DeviceFilesMoreOnClickListeners {
    fun onShowOptions(id: Long,
               title: String,
               subtitle: String,
               thumbnail: Bitmap?,
               type: DeviceFileType
    )
}