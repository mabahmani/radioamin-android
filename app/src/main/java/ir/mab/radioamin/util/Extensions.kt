package ir.mab.radioamin.util

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import ir.mab.radioamin.BuildConfig
import ir.mab.radioamin.R
import ir.mab.radioamin.ui.deviceonly.DeviceFilesActivity
import timber.log.Timber

private const val ALBUM_ART_URI = "content://media/external/audio/albumart"
private const val READ_ONLY_MODE = "r"
private const val THUMBNAIL_WIDTH = 128
private const val THUMBNAIL_HEIGHT = 128

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.errorToast(message: String) {
    if (BuildConfig.DEBUG){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    else{
        Toast.makeText(this, getString(R.string.error_msg), Toast.LENGTH_LONG).show()
    }
}

fun Activity.snackWithNavigateAction(
    message: String,
    destinationId: Int,
    bundle: Bundle
) {

    if (this is DeviceFilesActivity) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setActionTextColor(ContextCompat.getColor(this, R.color.color3))
            .setAction(this.resources.getString(R.string.view)) {
                binding.navHostFragment.findNavController().navigate(destinationId, bundle)
            }.show()
    }

}

fun Activity.snack(
    message: String) {

    if (this is DeviceFilesActivity) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}

fun Activity.showPermissionEducational(
    title: String,
    description: String,
    permissionEducationalActionListener: (allowed: Boolean) -> Unit
) {
    if (this is DeviceFilesActivity) {
        binding.permissionBottomSheet.setTitle(title)
        binding.permissionBottomSheet.setDescription(description)
        binding.permissionBottomSheet.setOnPermissionActionListener(
            permissionEducationalActionListener
        )
        BottomSheetBehavior.from(this.binding.permissionBottomSheet).state =
            BottomSheetBehavior.STATE_EXPANDED
    }
}

fun Activity.hidePermissionEducational() {
    if (this is DeviceFilesActivity) {
        BottomSheetBehavior.from(this.binding.permissionBottomSheet).state =
            BottomSheetBehavior.STATE_HIDDEN
    }
}

fun Context.getDeviceThumbnailAlbumArt(albumId: Long): Bitmap?{
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return this.contentResolver.loadThumbnail(
                getDeviceAlbumUri(albumId),
                Size(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT),
                null
            )
        } else {
            val albumUri = ContentUris.withAppendedId(
                Uri.parse(ALBUM_ART_URI),
                albumId
            )

            this.contentResolver.openFileDescriptor(albumUri,
                READ_ONLY_MODE
            )
                .use { pfd ->

                    return BitmapFactory.decodeFileDescriptor(pfd!!.fileDescriptor)
                        .scale(
                            THUMBNAIL_WIDTH,
                            THUMBNAIL_HEIGHT, false)

                }
        }
    } catch (ex: Exception) {
        Timber.e(ex)
        return null
    }
}

fun Context.getOriginalAlbumArt(albumId: Long): Bitmap? {
    try {
        val albumUri = ContentUris.withAppendedId(
            Uri.parse(ALBUM_ART_URI),
            albumId
        )

        this.contentResolver.openFileDescriptor(albumUri,
            READ_ONLY_MODE
        ).use { pfd ->

            return BitmapFactory.decodeFileDescriptor(pfd!!.fileDescriptor)
        }
    } catch (ex: Exception) {
        Timber.e(ex)
        return null
    }
}

fun getDeviceAlbumUri(albumId: Long): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ContentUris.withAppendedId(
            MediaStore.Audio.Albums.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            ), albumId
        )

    } else {
        ContentUris.withAppendedId(
            MediaStore.Audio.Albums.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            ), albumId
        )
    }
}