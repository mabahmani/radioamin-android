package ir.mab.radioamin.repo

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import ir.mab.radioamin.vo.DevicePlaylist
import ir.mab.radioamin.vo.DeviceSong
import ir.mab.radioamin.vo.generic.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class DevicePlaylistRepository(
    private val application: Application,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : DeviceFilesRepository(application) {

    suspend fun getDevicePlaylists(): LiveData<Resource<List<DevicePlaylist>>> {
        return liveData(dispatcherIO) {

            val playlists = mutableListOf<DevicePlaylist>()

            emit(Resource.loading(null))

            val collection = getPlaylistsUri()

            val projection = arrayOf(
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
            )

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

                        playlists.add(
                            DevicePlaylist(
                                id,
                                name,
                                getPlaylistMembersCount(id)
                            )
                        )
                    }

                    emit(Resource.success(playlists))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }

    suspend fun getDevicePlaylist(playlistId: Long): LiveData<Resource<DevicePlaylist>> {
        return liveData(dispatcherIO) {

            emit(Resource.loading(null))

            val collection = getPlaylistsUri()

            val projection = arrayOf(
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
            )

            val selection = "${MediaStore.Audio.Playlists._ID} = ?"
            val selectionArgs = arrayOf(playlistId.toString())

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

                        val id =
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID))
                        val name =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME))


                        emit(
                            Resource.success(
                                DevicePlaylist(
                                    id,
                                    name,
                                    getPlaylistMembersCount(id)
                                )
                            )
                        )

                    }

                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }

    suspend fun getPlaylistMembers(playlistId: Long): LiveData<Resource<List<DeviceSong>>> {

        return liveData(dispatcherIO) {
            val songs = mutableListOf<DeviceSong>()

            emit(Resource.loading(null))

            val collection = getPlaylistMembersUri(playlistId)

            val projection = arrayOf(
                MediaStore.Audio.Playlists.Members.AUDIO_ID,
                MediaStore.Audio.Playlists.Members.TITLE,
                MediaStore.Audio.Playlists.Members.ARTIST,
                MediaStore.Audio.Playlists.Members.DURATION,
                MediaStore.Audio.Playlists.Members.ALBUM_ID
            )

            val sortOrder = "${MediaStore.Audio.Playlists.Members.PLAY_ORDER} ASC"

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
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID))
                        val title =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.TITLE))
                        val artist =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ARTIST))
                        val duration =
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DURATION))
                        val albumId =
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ALBUM_ID))
                        val contentUri: Uri = getSongUri(id)

                        songs.add(
                            DeviceSong(
                                id,
                                albumId,
                                title,
                                artist,
                                duration,
                                contentUri)
                        )
                    }

                    emit(Resource.success(songs))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
                Timber.e(ex)
            }

        }

    }

    suspend fun editPlaylist(
        newName: String,
        songs: List<DeviceSong>,
        playlistId: Long,
        nameChanged: Boolean
    ): LiveData<Resource<Boolean>> {
        return liveData(dispatcherIO) {

            emit(Resource.loading(null))


            if (nameChanged) {
                try {
                    updatePlaylistName(newName, playlistId)
                    updatePlaylistOrders(songs, playlistId)

                    emit(Resource.success(true))

                } catch (ex: Exception) {
                    emit(Resource.error(null, ex.message.toString(), false, null))
                }
            } else {
                try {
                    updatePlaylistOrders(songs, playlistId)
                    emit(Resource.success(true))

                } catch (ex: Exception) {
                    emit(Resource.error(null, ex.message.toString(), false, null))
                }
            }


        }

    }

    suspend fun movePlaylistMember(playlistId: Long, from: Int, to: Int) {
        withContext(dispatcherIO) {
            try {
                val res = MediaStore.Audio.Playlists.Members.moveItem(
                    application.contentResolver,
                    playlistId,
                    from,
                    to
                )
                Timber.d("movePlaylistMember %s", res)
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }

    suspend fun deletePlaylist(playlistId: Long): LiveData<Resource<Boolean>> {

        return liveData(dispatcherIO) {

            emit(Resource.loading(null))

            val resolver = application.contentResolver

            val playlistCollection = getPlaylistsUri()

            val selection = "${MediaStore.Audio.Playlists._ID} = ?"
            val selectionArgs = arrayOf(playlistId.toString())

            try {
                resolver.delete(
                    playlistCollection,
                    selection,
                    selectionArgs
                )

                emit(Resource.success(true))

            } catch (ex: java.lang.Exception) {
                emit(Resource.error(null, ex.message.toString(), true, null))
            }
        }

    }

    suspend fun createNewPlaylist(name: String): LiveData<Resource<Uri>> {

        return liveData(dispatcherIO) {

            emit(Resource.loading(null))

            val resolver = application.contentResolver

            val playlistCollection = getPlaylistsUri()

            val newPlaylistDetails = ContentValues().apply {
                put(MediaStore.Audio.Playlists.NAME, name)
            }

            try {
                val result = resolver.insert(playlistCollection, newPlaylistDetails)
                emit(Resource.success(result))
            } catch (ex: java.lang.Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }

    suspend fun addSongsToPlaylist(
        songs: List<DeviceSong>,
        playlistId: Long
    ): LiveData<Resource<Boolean>> {

        return liveData(dispatcherIO) {
            emit(Resource.loading(null))

            val resolver = application.contentResolver

            val playlistCollection = getPlaylistMembersUri(playlistId)

            var lastPlayOrder = getPlaylistMembersCount(playlistId)
            val contentValuesList: Array<ContentValues?> = arrayOfNulls(songs.size)

            songs.forEachIndexed { index, song ->
                val newSongDetails = ContentValues().apply {
                    put(MediaStore.Audio.Playlists.Members.AUDIO_ID, song.id)
                    put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, lastPlayOrder++)
                }
                contentValuesList[index] = newSongDetails
            }

            try {
                resolver.bulkInsert(playlistCollection, contentValuesList)
                emit(Resource.success(true))
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.message.toString(), false, null))
            }
        }
    }

    private fun getPlaylistMembersCount(playlistId: Long): Int {

        var membersCount = 0

        val collection = getPlaylistMembersUri(playlistId)

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

    private fun updatePlaylistName(name: String, playlistId: Long) {
        val resolver = application.contentResolver

        val playlistCollection = getPlaylistsUri()

        val selection = "${MediaStore.Audio.Playlists._ID} = ?"
        val selectionArgs = arrayOf(playlistId.toString())

        val updatedPlaylistDetails = ContentValues().apply {
            put(MediaStore.Audio.Playlists.NAME, name)
        }

        resolver.update(
            playlistCollection,
            updatedPlaylistDetails,
            selection,
            selectionArgs
        )
    }

    private fun updatePlaylistOrders(songs: List<DeviceSong>, playlistId: Long) {

        val resolver = application.contentResolver

        val playlistCollection = getPlaylistMembersUri(playlistId)

        val selection = "${MediaStore.Audio.Playlists.Members.AUDIO_ID} = ?"

        songs.forEachIndexed { index, deviceSong ->
            val selectionArgs = arrayOf(deviceSong.id.toString())
            val updatedPlaylistDetails = ContentValues().apply {
                put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, index + 1)
            }
            resolver.update(
                playlistCollection,
                updatedPlaylistDetails,
                selection,
                selectionArgs
            )
        }
    }

}