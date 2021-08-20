package ir.mab.radioamin.ui.deviceonly

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.ActivityDeviceFilesOnlyBinding
import ir.mab.radioamin.ui.BaseActivity
import javax.inject.Inject

@AndroidEntryPoint
class DeviceFilesActivity : BaseActivity(), MotionLayout.TransitionListener{

    lateinit var binding: ActivityDeviceFilesOnlyBinding
    @Inject lateinit var player: SimpleExoPlayer

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_files_only)
        BottomSheetBehavior.from(binding.permissionBottomSheet).state = BottomSheetBehavior.STATE_HIDDEN
//        binding.motionParent.setTransition(R.id.playerHidden, R.id.playerHidden)
//
//        binding.motionParent.addTransitionListener(this)

        binding.motion.addTransitionListener(this)
        binding.motion.setTransition(R.id.transition1)

        BottomSheetBehavior.from(binding.bottomSheet).addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING){
                    binding.motion.setTransition(R.id.transition1)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.motion.progress = slideOffset
            }
        })

        binding.queueParent.setOnTouchListener { _, motionEvent ->
            BottomSheetBehavior.from(binding.bottomSheet).isDraggable =
                motionEvent.action == MotionEvent.ACTION_MOVE
            false
        }

        binding.chevronDown.setOnClickListener {
            BottomSheetBehavior.from(binding.bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
    }

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
    }

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
        BottomSheetBehavior.from(binding.bottomSheet).isDraggable = p1 != R.id.queueExpanded
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
    }


}