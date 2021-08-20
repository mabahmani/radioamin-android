package ir.mab.radioamin.ui.deviceonly

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.ActivityDeviceFilesOnlyBinding
import ir.mab.radioamin.ui.BaseActivity

class DeviceFilesActivity : BaseActivity() {
    lateinit var binding: ActivityDeviceFilesOnlyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_files_only)
        BottomSheetBehavior.from(binding.permissionBottomSheet).state = BottomSheetBehavior.STATE_HIDDEN

        //binding.motionParent.setTransition(R.id.playerHidden, R.id.playerHidden)
    }
}