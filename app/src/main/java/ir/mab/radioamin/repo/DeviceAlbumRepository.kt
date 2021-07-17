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
import ir.mab.radioamin.vo.DeviceAlbum
import ir.mab.radioamin.vo.DeviceSong
import ir.mab.radioamin.vo.generic.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class DeviceAlbumRepository(
    private val application: Application,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getDeviceAlbums(): LiveData<Resource<List<DeviceAlbum>>> {
        return liveData(dispatcherIO) {
            val albums = mutableListOf<DeviceAlbum>()

            emit(Resource.loading(null))

            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Albums.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST)

            val sortOrder = "${MediaStore.Audio.Albums.ALBUM} ASC"

            try {
                queryMediaStore(
                    collection,
                    projection,
                    null,
                    null,
                    sortOrder
                ).use {

                    while (it != null && it.moveToNext()) {

                        val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID))
                        val name =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM))
                        val artist =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST))

                        var bitmapResult: Bitmap? = null

                        try {
                            val resolver = application.contentResolver

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                val contentUri: Uri = ContentUris.withAppendedId(
                                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                                    id
                                )
                                bitmapResult = application.contentResolver.loadThumbnail(
                                    contentUri,
                                    Size(128, 128),
                                    null
                                )
                            } else {
                                val contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://media/external/audio/albumart"),
                                    id
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

                        albums.add(DeviceAlbum(id, name, artist, bitmapResult))

                    }

                    emit(Resource.success(albums))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }

    suspend fun getDeviceAlbum(albumId: Long): LiveData<Resource<DeviceAlbum>> {
        return liveData(dispatcherIO) {

            var album:DeviceAlbum? = null;

            emit(Resource.loading(null))

            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Albums.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST)

            val sortOrder = "${MediaStore.Audio.Albums.ALBUM} ASC"

            val selection = "${MediaStore.Audio.Albums._ID} = ?"

            val selectionArgs = arrayOf(albumId.toString())

            try {
                queryMediaStore(
                    collection,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                ).use {

                    while (it != null && it.moveToNext()) {

                        val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID))
                        val name =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM))
                        val artist =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST))

                        var bitmapResult: Bitmap? = null

                        try {
                            val resolver = application.contentResolver

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                val contentUri: Uri = ContentUris.withAppendedId(
                                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                                    id
                                )
                                bitmapResult = application.contentResolver.loadThumbnail(
                                    contentUri,
                                    Size(128, 128),
                                    null
                                )
                            } else {
                                val contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://media/external/audio/albumart"),
                                    id
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

                        album = DeviceAlbum(id, name, artist, bitmapResult)
                    }

                    emit(Resource.success(album))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }

    suspend fun getDeviceAlbumSongs(albumId: Long): LiveData<Resource<List<DeviceSong>>> {
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
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
            )

            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            val selection = "${MediaStore.Audio.Media.ALBUM_ID} = ?"

            val selectionArgs = arrayOf(albumId.toString())

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

                        songs.add(DeviceSong(id, name, artist, duration, contentUri, null))

                    }

                    emit(Resource.success(songs))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
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

