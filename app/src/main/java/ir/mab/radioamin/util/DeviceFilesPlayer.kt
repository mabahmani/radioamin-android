package ir.mab.radioamin.util

import android.app.Activity
import android.content.Intent
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ir.mab.radioamin.service.MediaPlayerService
import ir.mab.radioamin.ui.deviceonly.DeviceFilesActivity
import ir.mab.radioamin.vo.DeviceSong

object DeviceFilesPlayer {
    fun Activity.setDeviceFilesPlayerPlaylist(deviceSongs: List<DeviceSong>, startPosition: Int) {
        if (this is DeviceFilesActivity) {
            player.clearMediaItems()
            queuePlaylistSongs.clear()
            player.stop()

            for (song in deviceSongs) {
                player.addMediaItem(
                    MediaItem.Builder().setUri(song.contentUri).setMediaId(song.id.toString())
                        .setTag(song).build()
                )
                queuePlaylistSongs.add(song)
            }

            playerQueueSongsAdapter.notifyDataSetChanged()

            player.volume = 1f
            player.seekToDefaultPosition(startPosition)

            player.prepare()
            player.play()

            playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            Intent(this, MediaPlayerService::class.java).run{
                startService(this)
            }

        }
    }
}