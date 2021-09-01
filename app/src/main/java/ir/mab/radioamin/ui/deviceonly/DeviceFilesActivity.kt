package ir.mab.radioamin.ui.deviceonly

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.ActivityDeviceFilesOnlyBinding
import ir.mab.radioamin.ui.BaseActivity
import ir.mab.radioamin.ui.deviceonly.listener.PlayerQueueItemListeners
import ir.mab.radioamin.ui.deviceonly.player.PlayerQueueSongsAdapter
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.util.DateTimeFormatter
import ir.mab.radioamin.util.DeviceFilesImageLoader.getOriginalAlbumArt
import ir.mab.radioamin.util.DeviceFilesImageLoader.getOriginalAlbumArtSync
import ir.mab.radioamin.vo.DeviceSong
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class DeviceFilesActivity : BaseActivity(), MotionLayout.TransitionListener, Player.Listener,
    PlayerQueueItemListeners,            PlayerNotificationManager.MediaDescriptionAdapter
{

    lateinit var binding: ActivityDeviceFilesOnlyBinding
    lateinit var playerBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    @Inject
    lateinit var player: SimpleExoPlayer
    @Inject
    lateinit var sharePreferences: SharedPreferences
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private var stopUpdateSeekbar: Boolean = false
    var queuePlaylistSongs = mutableListOf<DeviceSong>()
    lateinit var playerQueueSongsAdapter: PlayerQueueSongsAdapter
    private lateinit var playerQueueItemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_files_only)

        BottomSheetBehavior.from(binding.permissionBottomSheet).state =
            BottomSheetBehavior.STATE_HIDDEN

        setupPlayerBottomSheet()
        setupQueueList()
        setupPlayer()
        setupPlayerNotificationManager()
    }

    private fun setupPlayerNotificationManager() {

        playerNotificationManager = PlayerNotificationManager.Builder(this,
            AppConstants.Notifications.PLAYER_NOTIFICATION_ID,
            AppConstants.Notifications.PLAYER_NOTIFICATION_CHANNEL_ID)
            .setChannelNameResourceId(R.string.app_name)
            .setChannelDescriptionResourceId(R.string.app_name)
            .setMediaDescriptionAdapter(this)
            //.setSmallIconResourceId(R.drawable.ic_small_logo)
            .build()

        playerNotificationManager.setSmallIcon(R.drawable.ic_small_logo)
        playerNotificationManager.setPriority(PRIORITY_MAX)
        playerNotificationManager.setUseFastForwardAction(false)
        playerNotificationManager.setUseRewindAction(false)
        playerNotificationManager.setPlayer(player)

    }

    private fun setupQueueList() {
        playerQueueSongsAdapter = PlayerQueueSongsAdapter(queuePlaylistSongs, this)

        binding.queueList.layoutManager = LinearLayoutManager(this)
        binding.queueList.adapter = playerQueueSongsAdapter

        binding.queueList.isNestedScrollingEnabled = false

        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                source: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                Collections.swap(
                    playerQueueSongsAdapter.list,
                    source.bindingAdapterPosition,
                    target.bindingAdapterPosition
                )
                playerQueueSongsAdapter.notifyItemMoved(
                    source.bindingAdapterPosition,
                    target.bindingAdapterPosition
                )

                player.moveMediaItem(source.bindingAdapterPosition, target.bindingAdapterPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }
        }

        playerQueueItemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        playerQueueItemTouchHelper.attachToRecyclerView(binding.queueList)
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
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    binding.motion.setTransition(R.id.transition1)
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.navHostFragment.setPadding(
                        0,
                        0,
                        0,
                        playerBottomSheetBehavior.peekHeight
                    )
                }
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.navHostFragment.setPadding(0, 0, 0, 0)
                    player.stop()
                    player.clearMediaItems()
                    queuePlaylistSongs.clear()
                    playerNotificationManager.setPlayer(null)
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

        binding.close.setOnClickListener {
            playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
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
            if (player.shuffleModeEnabled) {
                binding.shuffle.setImageResource(R.drawable.ic_shuffle)
                binding.shuffle.setColorFilter(ContextCompat.getColor(this, R.color.color10))
                player.shuffleModeEnabled = false
            } else {
                binding.shuffle.setImageResource(R.drawable.ic_shuffle_bold)
                binding.shuffle.setColorFilter(ContextCompat.getColor(this, R.color.white))
                player.shuffleModeEnabled = true

            }
        }

        binding.repeat.setOnClickListener {
            when (player.repeatMode) {
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

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(view: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.elapsedTime = DateTimeFormatter.millisToHumanTime(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(view: SeekBar?) {
                stopUpdateSeekbar = true
            }

            override fun onStopTrackingTouch(view: SeekBar?) {
                stopUpdateSeekbar = false
                if (view != null) {
                    player.seekTo(view.progress.toLong())
                }
            }
        })

    }

    private fun togglePLayPause() {
        if (player.isPlaying) {
            val fadeOut = ValueAnimator.ofFloat(1f, 0f)
            fadeOut.duration = 750
            fadeOut.addUpdateListener {
                player.volume = it.animatedValue as Float
            }
            fadeOut.addListener(onEnd = { player.pause() })
            fadeOut.start()
        } else {
            val fadeIn = ValueAnimator.ofFloat(0f, 1f)
            fadeIn.duration = 750
            fadeIn.addUpdateListener {
                player.volume = it.animatedValue as Float
            }
            fadeIn.addListener(onStart = { player.play() })
            fadeIn.start()
        }
    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
    }

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
    }

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
        playerBottomSheetBehavior.isDraggable = p1 != R.id.queueExpanded

        binding.queueList.post {
            binding.queueList.scrollToPosition(player.currentWindowIndex)
        }
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)

        if (mediaItem?.playbackProperties?.tag != null) {
            val song = mediaItem.playbackProperties!!.tag as DeviceSong

            binding.song = song

            binding.duration = DateTimeFormatter.millisToHumanTime(song.duration ?: 0)

            binding.elapsedTime = DateTimeFormatter.millisToHumanTime(0)

            GlobalScope.launch(Dispatchers.IO) {
                binding.thumbnail = getOriginalAlbumArt(song.albumId ?: -1)
            }

            playerQueueSongsAdapter.currentMediaPosition = player.currentWindowIndex
            playerQueueSongsAdapter.notifyDataSetChanged()
        }
    }

    var job: Job? = null

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)

        if (isPlaying) {

            if (BottomSheetBehavior.from(binding.playerBottomSheet).state == BottomSheetBehavior.STATE_HIDDEN)
                BottomSheetBehavior.from(binding.playerBottomSheet).state =
                    BottomSheetBehavior.STATE_COLLAPSED

            player.volume = 1f
            binding.play.setImageResource(R.drawable.ic_pause)
            binding.miniPlay.setImageResource(R.drawable.ic_pause)

            binding.seekbarMaxProgress = player.duration.toInt()

            job = CoroutineScope(Dispatchers.Main).launch {
                tickerFlow(1000).collect {
                    if (!stopUpdateSeekbar) {
                        binding.seekbarProgress = player.currentPosition.toInt()
                        binding.elapsedTime =
                            DateTimeFormatter.millisToHumanTime(player.currentPosition)
                    }
                }
            }

        } else {
            binding.play.setImageResource(R.drawable.ic_play)
            binding.miniPlay.setImageResource(R.drawable.ic_play)
            job?.cancel()
        }
        playerQueueSongsAdapter.isPlaying = isPlaying
        playerQueueSongsAdapter.notifyItemChanged(player.currentWindowIndex)

    }

    private fun tickerFlow(period: Long, initialDelay: Long = 0) = flow {
        delay(initialDelay)
        while (true) {
            emit(Unit)
            delay(period)
        }
    }.cancellable()

    override fun onBackPressed() {
        if (playerBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(position: Int) {
        player.seekToDefaultPosition(position)
        player.play()
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
    }

    override fun getCurrentContentTitle(player: Player): CharSequence {
        return (player.currentMediaItem?.playbackProperties?.tag as DeviceSong).name.toString()
    }

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        //return PendingIntent.getActivity(this,0, Intent(this,DeviceFilesActivity::class.java),FLAG_UPDATE_CURRENT)
        return null
    }

    override fun getCurrentContentText(player: Player): CharSequence? {
        return (player.currentMediaItem?.playbackProperties?.tag as DeviceSong).artistName.toString()
    }

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        return getOriginalAlbumArtSync((player.currentMediaItem?.playbackProperties?.tag as DeviceSong).albumId?: -1)
    }

    override fun onDestroy() {
        super.onDestroy()
        playerNotificationManager.setPlayer(null)
        player.release()
    }
}