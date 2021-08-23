package ir.mab.radioamin.util

import android.app.Activity
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ir.mab.radioamin.ui.deviceonly.DeviceFilesActivity
import ir.mab.radioamin.vo.DeviceSong

object DeviceFilesPlayer {
    fun Activity.setDeviceFilesPlayerPlaylist(deviceSongs: List<DeviceSong>, startPosition: Int) {
        if (this is DeviceFilesActivity) {
            player.clearMediaItems()
            player.stop()

            for (song in deviceSongs) {
                player.addMediaItem(
                    MediaItem.Builder().setUri(song.contentUri).setMediaId(song.id.toString())
                        .setTag(song).build()
                )
            }

            player.volume = 1f
            player.seekToDefaultPosition(startPosition)

            player.prepare()
            player.play()

            playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        }
    }
}