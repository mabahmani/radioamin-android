package ir.mab.radioamin.util

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.core.graphics.scale
import timber.log.Timber

object DeviceFilesImageLoader {
    private const val ALBUM_ART_URI = "content://media/external/audio/albumart"
    private const val READ_ONLY_MODE = "r"
    private const val THUMBNAIL_WIDTH = 128
    private const val THUMBNAIL_HEIGHT = 128

    suspend fun Context.getDeviceAlbumThumbnail(albumId: Long): Bitmap?{
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return this.contentResolver.loadThumbnail(
                    getDeviceAlbumUri(albumId),
                    Size(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT),
                    null
                )
            } else {
                val albumUri = ContentUris.withAppendedId(
                    Uri.parse(ALBUM_ART_URI),
                    albumId
                )

                this.contentResolver.openFileDescriptor(albumUri,
                    READ_ONLY_MODE
                )
                    .use { pfd ->

                        return BitmapFactory.decodeFileDescriptor(pfd!!.fileDescriptor)
                            .scale(
                                THUMBNAIL_WIDTH,
                                THUMBNAIL_HEIGHT, false)

                    }
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            return null
        }
    }

    suspend fun Context.getOriginalAlbumArt(albumId: Long): Bitmap? {
        try {
            val albumUri = ContentUris.withAppendedId(
                Uri.parse(ALBUM_ART_URI),
                albumId
            )

            this.contentResolver.openFileDescriptor(albumUri,
                READ_ONLY_MODE
            ).use { pfd ->

                return BitmapFactory.decodeFileDescriptor(pfd!!.fileDescriptor)
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            return null
        }
    }

    suspend fun Context.getDeviceArtistThumbnail(artistId: Long): Bitmap?{

        var thumbnail: Bitmap? = null

        this.contentResolver.query(
            getDeviceArtistAlbumsUri(artistId),
            arrayOf(MediaStore.Audio.Artists.Albums.ALBUM_ID),
            null,
            null,
            null
        ).use {
            if (it != null && it.count > 0) {
                it.moveToFirst()
                val albumId = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Artists.Albums.ALBUM_ID))
                thumbnail = getDeviceAlbumThumbnail(albumId)
            }
        }

        return thumbnail
    }

    suspend fun Context.getDevicePlaylistThumbnail(playlistId: Long): Bitmap?{

        var thumbnail: Bitmap? = null

        this.contentResolver.query(
            getDevicePlaylistMembersUri(playlistId),
            arrayOf(MediaStore.Audio.Playlists.Members.ALBUM_ID),
            null,
            null,
            null
        ).use {
            if (it != null && it.count > 0) {
                it.moveToFirst()
                val albumId = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ALBUM_ID))
                thumbnail = getDeviceAlbumThumbnail(albumId)
            }
        }

        return thumbnail
    }

    private fun getDeviceAlbumUri(albumId: Long): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentUris.withAppendedId(
                MediaStore.Audio.Albums.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                ), albumId
            )

        } else {
            ContentUris.withAppendedId(
                MediaStore.Audio.Albums.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                ), albumId
            )
        }
    }

    private fun getDeviceArtistAlbumsUri(artistId: Long): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Artists.Albums.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY,
                artistId
            )
        } else {
            MediaStore.Audio.Artists.Albums.getContentUri(
                MediaStore.VOLUME_EXTERNAL,
                artistId
            )
        }
    }

    private fun getDevicePlaylistMembersUri(playlistId: Long): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Playlists.Members.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY,
                playlistId
            )
        } else {
            MediaStore.Audio.Playlists.Members.getContentUri(
                MediaStore.VOLUME_EXTERNAL,
                playlistId
            )
        }
    }

}