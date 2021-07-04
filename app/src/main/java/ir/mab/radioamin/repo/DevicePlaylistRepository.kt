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

class DevicePlaylistRepository(
    private val application: Application,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) {

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
                MediaStore.Audio.Playlists.NAME)

            val sortOrder = "${MediaStore.Audio.Playlists.NAME} ASC"

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
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID))
                        val name =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME))

                        playlists.add(DevicePlaylist(id, name, getPlaylistMembersCount(id)))

                    }

                    emit(Resource.success(playlists))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }

    suspend fun getPlaylistMembers(playlistId: Long): List<DeviceSong> {
        val songs = mutableListOf<DeviceSong>()

        val collection =
            MediaStore.Audio.Playlists.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, playlistId)

        val projection = arrayOf(
            MediaStore.Audio.Playlists.Members.AUDIO_ID,
            MediaStore.Audio.Playlists.Members.TITLE,
            MediaStore.Audio.Playlists.Members.ARTIST,
            MediaStore.Audio.Playlists.Members.DURATION,
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
                    val title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.TITLE))
                    val artist = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ARTIST))
                    val duration = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DURATION))
                    val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                    songs.add(DeviceSong(id, title, artist, duration, contentUri))
                }

            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }

        return mutableListOf()
    }

    suspend fun updatePlaylistName(name:String, playlistId:Long){
        val resolver = application.contentResolver

        val playlistCollection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Playlists.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
            } else {
                MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
            }

        val selection = "${MediaStore.Audio.Playlists._ID} = ?"
        val selectionArgs = arrayOf(playlistId.toString())

        val updatedPlaylistDetails = ContentValues().apply {
            put(MediaStore.Audio.Playlists.NAME, name)
        }

        val res = resolver.update(
            playlistCollection,
            updatedPlaylistDetails,
            selection,
            selectionArgs)

        Timber.d("updatePlaylistName %s", res)
    }

    suspend fun deletePlaylist(playlistId:Long){
        val resolver = application.contentResolver

        val playlistCollection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Playlists.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
            } else {
                MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
            }

        val selection = "${MediaStore.Audio.Playlists._ID} = ?"
        val selectionArgs = arrayOf(playlistId.toString())

        val res = resolver.delete(
            playlistCollection,
            selection,
            selectionArgs)
    }

    suspend fun addNewPlaylist(name: String) {

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
            put(MediaStore.Audio.Playlists.NAME, name)
        }
        val res = resolver.insert(playlistCollection, newPlaylistDetails)

    }

    suspend fun addNewSongToPlaylist(songId: Long, playlistId: Long) {
        val resolver = application.contentResolver

        val playlistCollection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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

        val newSongDetails = ContentValues().apply {
            put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songId)
            put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, 0)
        }

        val res = resolver.insert(playlistCollection, newSongDetails)
    }

    private fun getPlaylistMembersCount(playlistId: Long): Int {

        var membersCount = 0

        val collection =
            MediaStore.Audio.Playlists.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, playlistId)

        val projection = arrayOf(
            MediaStore.Audio.Playlists.Members._ID
        )

        try {
            queryMediaStore(
                collection,
                projection,
                null,
                null,
                null
            ).use {
                if (it != null)
                    membersCount = it.count
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }

        return membersCount
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