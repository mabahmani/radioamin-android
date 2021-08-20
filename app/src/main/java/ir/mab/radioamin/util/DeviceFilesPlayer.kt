package ir.mab.radioamin.util

import android.app.Activity
import com.google.android.exoplayer2.MediaItem
import ir.mab.radioamin.ui.deviceonly.DeviceFilesActivity
import ir.mab.radioamin.vo.DeviceSong

object DeviceFilesPlayer {
    fun Activity.setDeviceFilesPlayerPlaylist(deviceSongs: List<DeviceSong>){
        if (this is DeviceFilesActivity){
            player.clearMediaItems()
            player.stop()

            for (song in deviceSongs){
                player.addMediaItem(MediaItem.fromUri(song.contentUri!!))
            }

            player.prepare()
            player.play()

            //binding.motionParent.transitionToState(R.id.playerExpanded)

        }
    }
}