package ir.mab.radioamin.repo

import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import ir.mab.radioamin.vo.DevicePlaylist
import ir.mab.radioamin.vo.DeviceSong
import ir.mab.radioamin.vo.generic.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class DeviceFilesRepository(
    private val application: Application,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getDeviceSongs(): LiveData<Resource<List<DeviceSong>>> {
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
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
            )

            val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

            try {
                queryMediaStore(
                    collection,
                    projection,
                    null,
                    null,
                    sortOrder
                ).use {

                    while (it != null && it.moveToNext()) {

                        val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                        val name =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                        val duration =
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                        val artist =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        songs.add(DeviceSong(id, name, artist, duration, contentUri))

                    }

                    emit(Resource.success(songs))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }

    suspend fun getDevicePlaylists(): LiveData<Resource<List<DevicePlaylist>>> {
        return liveData(dispatcherIO) {

            val playlists = mutableListOf<DevicePlaylist>()

            emit(Resource.loading(null))

            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Playlists.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.DISPLAY_NAME
            )

            val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

            try {
                queryMediaStore(
                    collection,
                    projection,
                    null,
                    null,
                    sortOrder
                ).use {

                    while (it != null && it.moveToNext()) {

                        val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                        val name =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))


                        playlists.add(DevicePlaylist(id, name, getPlaylistMembers(id)))

                    }

                    emit(Resource.success(playlists))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }

    private fun getPlaylistMembers(playlistId: Long): List<DeviceSong> {
        val songIds = mutableListOf<Long>()

        val collection =
            MediaStore.Audio.Playlists.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, playlistId)

        val projection = arrayOf(
            MediaStore.Audio.Playlists.Members.AUDIO_ID
        )

        try {
            queryMediaStore(
                collection,
                projection,
                null,
                null,
                null
            ).use {

                while (it != null && it.moveToNext()) {
                    val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID))
                    songIds.add(id)
                }

            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }

        return findSongsByIds(songIds)
    }

    private fun findSongsByIds(songIds : List<Long>): List<DeviceSong>{

        val songs = mutableListOf<DeviceSong>()

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
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
        )

        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
        val selection = "${MediaStore.Audio.Media._ID} = ?"

        for (songId in songIds){
            val selectionArgs = arrayOf(songId.toString())
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
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                        val duration =
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                        val artist =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        songs.add(DeviceSong(id, name, artist, duration, contentUri))

                    }

                }
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }

        return songs
    }

    fun addNewPlaylist(name: String){
        Timber.d("addNewPlaylist %s", name)

        val resolver = application.contentResolver

        val playlistCollection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Playlists.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
            } else {
                MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
            }

        val newPlaylistDetails = ContentValues().apply {
            put(MediaStore.Audio.Playlists.DISPLAY_NAME, name)
        }
        val res = resolver.insert(playlistCollection, newPlaylistDetails)
        Timber.d("addNewPlaylist %s", res.toString())
    }

    fun addNewSongToPlaylist(songId: Long, playlistId: Long){
        Timber.d("addNewSongToPlaylist %s %s", songId, playlistId)

        val resolver = application.contentResolver

        val playlistCollection =
            MediaStore.Audio.Playlists.Members.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY,
                playlistId
            )

        val newSongDetails = ContentValues().apply {
            put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songId)
        }
        val res = resolver.insert(playlistCollection, newSongDetails)
        Timber.d("addNewPlaylist %s", res.toString())
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

