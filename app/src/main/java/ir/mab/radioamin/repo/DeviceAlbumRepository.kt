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

