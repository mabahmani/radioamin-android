package ir.mab.radioamin.ui.deviceonly

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.ExoPlayer
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
    @Inject lateinit var sharePreferences: SharedPreferences
    private var stopUpdateSeekbar: Boolean = false

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

        binding.songName.isSelected = true

        binding.queueParent.setOnTouchListener { _, motionEvent ->
            playerBottomSheetBehavior.isDraggable =
                motionEvent.action == MotionEvent.ACTION_MOVE
            false
        }

        binding.chevronDown.setOnClickListener {
            binding.motion.setTransition(R.id.transition1)
            playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.playerParent.setOnClickListener {
            playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.play.setOnClickListener {
            togglePLayPause()
        }

        binding.miniPlay.setOnClickListener {
            togglePLayPause()
        }

        binding.next.setOnClickListener {
            player.seekToNext()
            player.play()
        }
        binding.miniNext.setOnClickListener {
            player.seekToNext()
            player.play()
        }
        binding.previous.setOnClickListener {
            player.seekToPrevious()
            player.play()
        }

        binding.shuffle.setOnClickListener {
            if (player.shuffleModeEnabled){
                binding.shuffle.setImageResource(R.drawable.ic_shuffle)
                binding.shuffle.setColorFilter(ContextCompat.getColor(this, R.color.color10))
                player.shuffleModeEnabled = false
            }
            else{
                binding.shuffle.setImageResource(R.drawable.ic_shuffle_bold)
                binding.shuffle.setColorFilter(ContextCompat.getColor(this, R.color.white))
                player.shuffleModeEnabled = true

            }
        }

        binding.repeat.setOnClickListener {
            when(player.repeatMode){
                ExoPlayer.REPEAT_MODE_OFF -> {
                    binding.repeat.setImageResource(R.drawable.ic_repeat_1_bold)
                    binding.repeat.setColorFilter(ContextCompat.getColor(this, R.color.white))
                    player.repeatMode = ExoPlayer.REPEAT_MODE_ONE
                }
                ExoPlayer.REPEAT_MODE_ONE -> {
                    binding.repeat.setImageResource(R.drawable.ic_repeat_bold)
                    binding.repeat.setColorFilter(ContextCompat.getColor(this, R.color.white))
                    player.repeatMode = ExoPlayer.REPEAT_MODE_ALL
                }
                ExoPlayer.REPEAT_MODE_ALL -> {
                    binding.repeat.setImageResource(R.drawable.ic_repeat)
                    binding.repeat.setColorFilter(ContextCompat.getColor(this, R.color.color10))
                    player.repeatMode = ExoPlayer.REPEAT_MODE_OFF
                }
            }

        }

        binding.seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(view: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    binding.elapsedTime = DateTimeFormatter.millisToHumanTime(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(view: SeekBar?) {
                stopUpdateSeekbar = true
            }

            override fun onStopTrackingTouch(view: SeekBar?) {
                stopUpdateSeekbar = false
                if (view != null){
                    player.seekTo(view.progress.toLong())
                }
            }
        })

    }

    private fun togglePLayPause() {
        if (player.isPlaying) {
            val fadeOut = ValueAnimator.ofFloat(1f,0f)
            fadeOut.duration = 750
            fadeOut.addUpdateListener {
                player.volume = it.animatedValue as Float
            }
            fadeOut.addListener(onEnd = {player.pause()})
            fadeOut.start()
        }
        else{
            val fadeIn = ValueAnimator.ofFloat(0f,1f)
            fadeIn.duration = 750
            fadeIn.addUpdateListener {
                player.volume = it.animatedValue as Float
            }
            fadeIn.addListener(onStart = {player.play()})
            fadeIn.start()
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
            player.volume = 1f
            binding.play.setImageResource(R.drawable.ic_pause)
            binding.miniPlay.setImageResource(R.drawable.ic_pause)

            binding.seekbarMaxProgress = player.duration.toInt()

            job = CoroutineScope(Dispatchers.Main).launch {
                tickerFlow(1000).collect {
                    if (!stopUpdateSeekbar){
                        binding.seekbarProgress = player.currentPosition.toInt()
                        binding.elapsedTime = DateTimeFormatter.millisToHumanTime(player.currentPosition)
                    }
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