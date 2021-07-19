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
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import ir.mab.radioamin.vo.DeviceArtist
import ir.mab.radioamin.vo.DeviceSong
import ir.mab.radioamin.vo.generic.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class DeviceArtistRepository(
    private val application: Application,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getDeviceArtists(): LiveData<Resource<List<DeviceArtist>>>{
        return liveData(dispatcherIO) {

            val artists = mutableListOf<DeviceArtist>()

            emit(Resource.loading(null))

            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Artists.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
            )

            val sortOrder = "${MediaStore.Audio.Artists.ARTIST} ASC"

            try {
                queryMediaStore(
                    collection,
                    projection,
                    null,
                    null,
                    sortOrder
                ).use {

                    while (it != null && it.moveToNext()) {

                        val id =
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID))
                        val name =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST))
                        val numberOfTracks =
                            it.getInt(it.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS))

                        artists.add(
                            DeviceArtist(
                                id,
                                name,
                                numberOfTracks,
                                getFirstArtistSongsAlbumArtBitmap(id)
                            )
                        )

                    }

                    emit(Resource.success(artists))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }

    suspend fun getDeviceArtistSongs(artistId: Long): LiveData<Resource<List<DeviceSong>>>{
        return liveData(dispatcherIO) {

            val songs = mutableListOf<DeviceSong>()

            emit(Resource.loading(null))

            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
            )

            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            val selection = "${MediaStore.Audio.Media.ARTIST_ID} = ?"

            val selectionArgs = arrayOf(artistId.toString())

            try {
                queryMediaStore(
                    collection,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                ).use {

                    while (it != null && it.moveToNext()) {

                        val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                        val albumId = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                        val name =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                        val artist =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                        val duration =
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))

                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        songs.add(DeviceSong(id, name, artist, duration, contentUri, getAlbumArtBitmap(id, albumId)))

                    }

                    emit(Resource.success(songs))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }

    private fun getAlbumArtBitmap(songId: Long, albumId: Long): Bitmap? {

        val contentUri: Uri
        var bitmapResult: Bitmap? = null

        try {
            val resolver = application.contentResolver

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    songId
                )
                bitmapResult = application.contentResolver.loadThumbnail(
                    contentUri,
                    Size(128, 128),
                    null
                )
            } else {
                contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )
                val readOnlyMode = "r"
                resolver.openFileDescriptor(contentUri, readOnlyMode).use { pfd ->
                    bitmapResult =
                        BitmapFactory.decodeFileDescriptor(pfd!!.fileDescriptor)
                }
            }

        } catch (ex: Exception) {
            Timber.e(ex)
        }

        return bitmapResult
    }

    private fun getFirstArtistSongsAlbumArtBitmap(artistId: Long): Bitmap? {

        var contentUri: Uri? = null
        var bitmapResult: Bitmap? = null

        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM_ID,
        )

        val selection = "${MediaStore.Audio.Media.ARTIST_ID} = ?"

        val selectionArgs = arrayOf(artistId.toString())

        try {
            queryMediaStore(
                collection,
                projection,
                selection,
                selectionArgs,
                null
            ).use {
                if (it != null && it.count > 0) {
                    it.moveToFirst()
                    val songId =
                        it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    val albumId =
                        it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))

                    try {
                        val resolver = application.contentResolver

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            contentUri = ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                songId
                            )
                            bitmapResult = application.contentResolver.loadThumbnail(
                                contentUri!!,
                                Size(128, 128),
                                null
                            )
                        } else {
                            contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://media/external/audio/albumart"),
                                albumId
                            )
                            val readOnlyMode = "r"
                            resolver.openFileDescriptor(contentUri!!, readOnlyMode).use { pfd ->
                                bitmapResult =
                                    BitmapFactory.decodeFileDescriptor(pfd!!.fileDescriptor)
                            }
                        }

                    } catch (ex: Exception) {
                        Timber.e(ex)
                    }

                }
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }

        return bitmapResult
    }

    private fun queryMediaStore(
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

}

