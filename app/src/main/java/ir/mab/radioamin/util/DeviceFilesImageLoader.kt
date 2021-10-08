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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

object DeviceFilesImageLoader {
    private const val VOLUME_EXTERNAL = "external"
    private const val ALBUM_ART_URI = "content://media/external/audio/albumart"
    private const val READ_ONLY_MODE = "r"
    private const val THUMBNAIL_WIDTH = 128
    private const val THUMBNAIL_HEIGHT = 128

    suspend fun Context.getDeviceAlbumThumbnail(albumId: Long): Bitmap? {
        val contentResolver = this.contentResolver
        var bitmap: Bitmap?

        withContext(Dispatchers.IO) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    bitmap = contentResolver.loadThumbnail(
                        getDeviceAlbumUri(albumId),
                        Size(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT),
                        null
                    )
                } else {
                    val albumUri = ContentUris.withAppendedId(
                        Uri.parse(ALBUM_ART_URI),
                        albumId
                    )

                    contentResolver.openFileDescriptor(
                        albumUri,
                        READ_ONLY_MODE
                    )
                        .use { pfd ->

                            bitmap = BitmapFactory.decodeFileDescriptor(pfd!!.fileDescriptor)
                                .scale(
                                    THUMBNAIL_WIDTH,
                                    THUMBNAIL_HEIGHT, false
                                )

                        }
                }
            } catch (ex: Exception) {
                Timber.e(ex)
                bitmap = null
            }
        }

        return bitmap
    }

    suspend fun Context.getOriginalAlbumArt(albumId: Long): Bitmap? {
        val contentResolver = this.contentResolver
        var bitmap: Bitmap?
        withContext(Dispatchers.IO) {
            try {
                val albumUri = ContentUris.withAppendedId(
                    Uri.parse(ALBUM_ART_URI),
                    albumId
                )

                contentResolver.openFileDescriptor(
                    albumUri,
                    READ_ONLY_MODE
                ).use { pfd ->

                    bitmap = BitmapFactory.decodeFileDescriptor(pfd!!.fileDescriptor)
                }
            } catch (ex: Exception) {
                Timber.e(ex)
                bitmap = null
            }
        }
        return bitmap
    }

    fun Context.getOriginalAlbumArtSync(albumId: Long): Bitmap? {
        val contentResolver = this.contentResolver
        var bitmap: Bitmap?
        try {
            val albumUri = ContentUris.withAppendedId(
                Uri.parse(ALBUM_ART_URI),
                albumId
            )

            contentResolver.openFileDescriptor(
                albumUri,
                READ_ONLY_MODE
            ).use { pfd ->

                bitmap = BitmapFactory.decodeFileDescriptor(pfd!!.fileDescriptor)
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            bitmap = null
        }
        return bitmap
    }

    suspend fun Context.getDeviceArtistThumbnail(artistId: Long): Bitmap? {

        var thumbnail: Bitmap? = null

        try {
            this.contentResolver.query(
                getDeviceArtistAlbumsUri(artistId),
                arrayOf(MediaStore.Audio.Artists.Albums.ALBUM_ID),
                null,
                null,
                null
            ).use {
                if (it != null && it.count > 0) {
                    it.moveToFirst()
                    val albumId =
                        it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Artists.Albums.ALBUM_ID))
                    thumbnail = getDeviceAlbumThumbnail(albumId)
                }
            }

            return thumbnail

        }catch (ex: java.lang.Exception){
            Timber.d(ex)
            return null
        }
    }

    suspend fun Context.getDevicePlaylistThumbnail(playlistId: Long): Bitmap? {

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
                val albumId =
                    it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ALBUM_ID))
                thumbnail = getDeviceAlbumThumbnail(albumId)
            }
        }

        return thumbnail
    }

    private fun getDeviceAlbumUri(albumId: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)
    }

    private fun getDeviceArtistAlbumsUri(artistId: Long): Uri {
        return MediaStore.Audio.Artists.Albums.getContentUri(VOLUME_EXTERNAL, artistId)
    }

    private fun getDevicePlaylistMembersUri(playlistId: Long): Uri {
        return MediaStore.Audio.Playlists.Members.getContentUri(VOLUME_EXTERNAL, playlistId)
    }

}