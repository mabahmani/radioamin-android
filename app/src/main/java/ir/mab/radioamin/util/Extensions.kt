package ir.mab.radioamin.util

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import ir.mab.radioamin.R
import ir.mab.radioamin.ui.deviceonly.DeviceFilesOnlyActivity


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.snackWithNavigateAction(
    message: String,
    destinationId: Int,
    bundle: Bundle
) {

    if (this is DeviceFilesOnlyActivity) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setActionTextColor(ContextCompat.getColor(this, R.color.color3))
            .setAction(this.resources.getString(R.string.view)) {
                binding.navHostFragment.findNavController().navigate(destinationId, bundle)
            }.show()
    }

}

fun Activity.showPermissionEducational(
    title: String,
    description: String,
    permissionEducationalActionListener: (allowed: Boolean) -> Unit
) {
    if (this is DeviceFilesOnlyActivity) {
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
    if (this is DeviceFilesOnlyActivity) {
        BottomSheetBehavior.from(this.binding.permissionBottomSheet).state =
            BottomSheetBehavior.STATE_HIDDEN
    }
}