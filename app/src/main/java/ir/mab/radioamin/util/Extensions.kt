package ir.mab.radioamin.util

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import ir.mab.radioamin.BuildConfig
import ir.mab.radioamin.R
import ir.mab.radioamin.ui.deviceonly.DeviceFilesActivity

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.networkErrorToast() {
    Toast.makeText(this, getString(R.string.no_connection_error), Toast.LENGTH_LONG).show()
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
    bundle: Bundle?
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