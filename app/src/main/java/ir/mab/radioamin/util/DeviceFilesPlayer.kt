package ir.mab.radioamin.util

import android.app.Activity
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ir.mab.radioamin.ui.deviceonly.DeviceFilesActivity
import ir.mab.radioamin.vo.devicefiles.DeviceSong

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

        }
    }

    fun Activity.addDeviceFilesToPlayerQueue(deviceSongs: List<DeviceSong>) {
        if (this is DeviceFilesActivity) {

            for (song in deviceSongs) {
                player.addMediaItem(
                    MediaItem.Builder().setUri(song.contentUri).setMediaId(song.id.toString())
                        .setTag(song).build()
                )
                queuePlaylistSongs.add(song)
            }

            playerQueueSongsAdapter.notifyDataSetChanged()

            if (playerBottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN){
                player.volume = 1f

                player.prepare()

                playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            }
        }
    }

    fun Activity.addDeviceFilesToPlayNext(deviceSongs: List<DeviceSong>) {
        if (this is DeviceFilesActivity) {

            var addIndex = player.currentWindowIndex

            if (addIndex == 0)
                addIndex = -1

            for (song in deviceSongs) {
                addIndex ++
                player.addMediaItem(
                    addIndex,
                    MediaItem.Builder().setUri(song.contentUri).setMediaId(song.id.toString())
                        .setTag(song).build()
                )
                queuePlaylistSongs.add(addIndex,song)
            }

            playerQueueSongsAdapter.notifyDataSetChanged()

            if (playerBottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN){
                player.volume = 1f

                player.prepare()


                playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            }
        }
    }
}