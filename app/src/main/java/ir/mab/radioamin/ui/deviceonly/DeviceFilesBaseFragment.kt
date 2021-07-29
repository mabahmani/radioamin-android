package ir.mab.radioamin.ui.deviceonly

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ir.mab.radioamin.R
import ir.mab.radioamin.util.hidePermissionEducational
import ir.mab.radioamin.util.showPermissionEducational

open class DeviceFilesBaseFragment : Fragment() {
    private val permissionGranted = MutableLiveData<Boolean>()
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    fun checkPermissions(): LiveData<Boolean> {

        if (requestPermissionLauncher == null){
            requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                    permissionGranted.value = it
                }
        }


        when {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                    == PackageManager.PERMISSION_GRANTED -> {
                permissionGranted.value = true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                requireActivity().showPermissionEducational(
                    getString(R.string.read_write_file_permission_title),
                    getString(R.string.read_write_file_permission_description)
                ) {
                    if (it) {
                        requestPermissionLauncher!!.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                    requireActivity().hidePermissionEducational()
                }
            }

            else -> {
                requestPermissionLauncher!!.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        return permissionGranted
    }

}