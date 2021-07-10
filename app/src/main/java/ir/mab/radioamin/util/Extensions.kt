package ir.mab.radioamin.util

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ir.mab.radioamin.ui.deviceonly.DeviceFilesOnlyActivity


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.showPermissionEducational(title: String, description: String, permissionEducationalActionListener: (allowed: Boolean) -> Unit) {
    if (this is DeviceFilesOnlyActivity){
        binding.permissionBottomSheet.setTitle(title)
        binding.permissionBottomSheet.setDescription(description)
        binding.permissionBottomSheet.setOnPermissionActionListener(permissionEducationalActionListener)
        BottomSheetBehavior.from(this.binding.permissionBottomSheet).state =
            BottomSheetBehavior.STATE_EXPANDED
    }
}

fun Activity.hidePermissionEducational() {
    if (this is DeviceFilesOnlyActivity){
        BottomSheetBehavior.from(this.binding.permissionBottomSheet).state =
            BottomSheetBehavior.STATE_HIDDEN
    }
}