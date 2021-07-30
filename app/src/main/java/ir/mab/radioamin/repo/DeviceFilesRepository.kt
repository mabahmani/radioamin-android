package ir.mab.radioamin.repo

import android.app.Application
import android.content.ContentUris
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.core.graphics.scale
import timber.log.Timber

open class DeviceFilesRepository(private val application: Application) {

    companion object {
        private const val ALBUM_ART_URI = "content://media/external/audio/albumart"
        private const val READ_ONLY_MODE = "r"
        private const val THUMBNAIL_WIDTH = 128
        private const val THUMBNAIL_HEIGHT = 128
    }

    fun queryMediaStore(
        collection: Uri,
        projection: Array<String>,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return application.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
    }

    fun getOriginalAlbumArt(albumId: Long): Bitmap? {
        try {
            val albumUri = ContentUris.withAppendedId(
                Uri.parse(ALBUM_ART_URI),
                albumId
            )

            application.contentResolver.openFileDescriptor(albumUri, READ_ONLY_MODE).use { pfd ->

                return BitmapFactory.decodeFileDescriptor(pfd!!.fileDescriptor)
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            return null
        }
    }

    fun getThumbnailAlbumArt(albumId: Long): Bitmap? {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return application.contentResolver.loadThumbnail(
                    getAlbumUri(albumId),
                    Size(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT),
                    null
                )
            } else {
                val albumUri = ContentUris.withAppendedId(
                    Uri.parse(ALBUM_ART_URI),
                    albumId
                )

                application.contentResolver.openFileDescriptor(albumUri, READ_ONLY_MODE)
                    .use { pfd ->

                        return BitmapFactory.decodeFileDescriptor(pfd!!.fileDescriptor)
                            .scale(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, false)

                    }
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            return null
        }

    }

    fun getPlaylistsUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Playlists.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Audio.Playlists.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        }
    }

    fun getPlaylistUri(playlistId: Long): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentUris.withAppendedId(
                MediaStore.Audio.Playlists.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                ), playlistId
            )
        } else {
            ContentUris.withAppendedId(
                MediaStore.Audio.Playlists.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                ), playlistId
            )
        }
    }

    fun getPlaylistMembersUri(playlistId: Long): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Playlists.Members.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY, playlistId
            )
        } else {
            MediaStore.Audio.Playlists.Members.getContentUri(
                MediaStore.VOLUME_EXTERNAL, playlistId
            )
        }
    }

    fun getSongsUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Audio.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        }
    }

    fun getSongUri(songId: Long): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                ),
                songId
            )

        } else {
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                ),
                songId
            )
        }
    }

    fun getArtistsUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Artists.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Audio.Artists.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        }
    }

    fun getArtistsUri(artistId: Long): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentUris.withAppendedId(
                MediaStore.Audio.Artists.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                ), artistId
            )

        } else {
            ContentUris.withAppendedId(
                MediaStore.Audio.Artists.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                ), artistId
            )
        }
    }

    fun getAlbumsUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Albums.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Audio.Albums.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        }
    }

    fun getAlbumUri(albumId: Long): Uri {
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
}