package ir.mab.radioamin.ui.deviceonly

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.ActivityDeviceFilesOnlyBinding
import ir.mab.radioamin.ui.BaseActivity
import ir.mab.radioamin.util.DateTimeFormatter
import ir.mab.radioamin.util.DeviceFilesImageLoader.getOriginalAlbumArt
import ir.mab.radioamin.vo.DeviceSong
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@AndroidEntryPoint
class DeviceFilesActivity : BaseActivity(), MotionLayout.TransitionListener, Player.Listener{

    lateinit var binding: ActivityDeviceFilesOnlyBinding
    lateinit var playerBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    @Inject lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_files_only)
        BottomSheetBehavior.from(binding.permissionBottomSheet).state = BottomSheetBehavior.STATE_HIDDEN

        setupPlayerBottomSheet()
        setupPlayer();
    }

    private fun setupPlayer() {
        player.addListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPlayerBottomSheet() {
        playerBottomSheetBehavior = BottomSheetBehavior.from(binding.playerBottomSheet)
        playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.motion.addTransitionListener(this)
        binding.motion.setTransition(R.id.transition1)

        playerBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING){
                    binding.motion.setTransition(R.id.transition1)
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    binding.navHostFragment.setPadding(0,0,0, playerBottomSheetBehavior.peekHeight)
                }
                if (newState == BottomSheetBehavior.STATE_HIDDEN){
                    binding.navHostFragment.setPadding(0,0,0, 0)
                    player.stop()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.motion.progress = slideOffset
            }
        })

        binding.queueParent.setOnTouchListener { _, motionEvent ->
            playerBottomSheetBehavior.isDraggable =
                motionEvent.action == MotionEvent.ACTION_MOVE
            false
        }

        binding.chevronDown.setOnClickListener {
            playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.playerParent.setOnClickListener {
            playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.play.setOnClickListener {
            if (player.isPlaying)
                player.pause()
            else
                player.play()
        }

        binding.miniPlay.setOnClickListener {
            if (player.isPlaying)
                player.pause()
            else
                player.play()
        }

        binding.next.setOnClickListener {
            player.seekToNextWindow()
        }
        binding.miniNext.setOnClickListener {
            player.seekToNextWindow()
        }
        binding.previous.setOnClickListener {
            player.seekToPreviousWindow()
        }
    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
    }

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
    }

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
        playerBottomSheetBehavior.isDraggable = p1 != R.id.queueExpanded
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)

        if (mediaItem?.playbackProperties?.tag != null){
            val song = mediaItem.playbackProperties!!.tag as DeviceSong

            binding.song = song

            binding.duration = DateTimeFormatter.millisToHumanTime(song.duration?:0)

            binding.elapsedTime = DateTimeFormatter.millisToHumanTime(0)

            GlobalScope.launch(Dispatchers.IO){
                binding.thumbnail = getOriginalAlbumArt(song.albumId ?: -1)
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

    }
    var job: Job? = null

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)

        if (isPlaying){

            binding.play.setImageResource(R.drawable.ic_pause)
            binding.miniPlay.setImageResource(R.drawable.ic_pause)

            binding.seekbarMaxProgress = player.duration.toInt()

            job = CoroutineScope(Dispatchers.Main).launch {
                tickerFlow(1000).collect {
                    binding.elapsedTime = DateTimeFormatter.millisToHumanTime(player.currentPosition)
                    binding.seekbarProgress = player.currentPosition.toInt()
                }
            }
        }

        else{
            binding.play.setImageResource(R.drawable.ic_play)
            binding.miniPlay.setImageResource(R.drawable.ic_play)
            job?.cancel()
        }
    }

    private fun tickerFlow(period: Long, initialDelay: Long = 0) = flow {
        delay(initialDelay)
        while (true) {
            emit(Unit)
            delay(period)
        }
    }.cancellable()

    override fun onBackPressed() {
        if (playerBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
            playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        else{
            super.onBackPressed()
        }
    }

}