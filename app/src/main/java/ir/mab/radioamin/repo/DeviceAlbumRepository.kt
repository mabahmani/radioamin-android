package ir.mab.radioamin.repo

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import ir.mab.radioamin.vo.DeviceAlbum
import ir.mab.radioamin.vo.DeviceSong
import ir.mab.radioamin.vo.generic.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DeviceAlbumRepository(
    private val application: Application,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : DeviceFilesRepository(application) {

    suspend fun getDeviceAlbums(): LiveData<Resource<List<DeviceAlbum>>> {
        return liveData(dispatcherIO) {
            val albums = mutableListOf<DeviceAlbum>()

            emit(Resource.loading(null))

            val collection = getAlbumsUri()

            val projection = arrayOf(
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST
            )

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

                        albums.add(DeviceAlbum(id, name, artist))

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

            var album: DeviceAlbum? = null;

            emit(Resource.loading(null))

            val collection = getAlbumsUri()

            val projection = arrayOf(
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST
            )

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

                        album = DeviceAlbum(id, name, artist)
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

            val collection = getSongsUri()

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
                        val contentUri: Uri = getSongUri(id)

                        songs.add(DeviceSong(id,albumId, name, artist, duration, contentUri))

                    }

                    emit(Resource.success(songs))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }
}

