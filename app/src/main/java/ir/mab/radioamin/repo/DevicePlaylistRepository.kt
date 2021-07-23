package ir.mab.radioamin.repo

import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
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
                                getPlaylistMembersCount(id),
                                getFirstPlaylistMembersAlbumArtBitmap(id)
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

    suspend fun getPlaylistMembers(playlistId: Long): LiveData<Resource<List<DeviceSong>>> {

        return liveData(dispatcherIO) {
            val songs = mutableListOf<DeviceSong>()
            var bitmapResult: Bitmap? = null
            emit(Resource.loading(null))

            val collection =
                MediaStore.Audio.Playlists.Members.getContentUri(
                    MediaStore.VOLUME_EXTERNAL,
                    playlistId
                )

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
                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        val resolver = application.contentResolver

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            bitmapResult = application.contentResolver.loadThumbnail(
                                contentUri,
                                Size(128, 128),
                                null
                            )
                        } else {
                            val albumUri = ContentUris.withAppendedId(
                                Uri.parse("content://media/external/audio/albumart"),
                                albumId
                            )
                            val readOnlyMode = "r"
                            resolver.openFileDescriptor(albumUri, readOnlyMode).use { pfd ->
                                bitmapResult =
                                    BitmapFactory.decodeFileDescriptor(pfd!!.fileDescriptor)
                            }
                        }
                        songs.add(DeviceSong(id, title, artist, duration, contentUri, bitmapResult))
                    }

                    emit(Resource.success(songs))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
                Timber.e(ex)
            }

        }

    }

    suspend fun updatePlaylistName(name: String, playlistId: Long) {
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
            selectionArgs
        )

    }

    suspend fun deletePlaylist(playlistId: Long) {
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
            selectionArgs
        )
    }

    suspend fun addNewPlaylist(name: String): LiveData<Resource<Uri>> {

        return liveData(dispatcherIO) {

            emit(Resource.loading(null))

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

            try {
                val result = resolver.insert(playlistCollection, newPlaylistDetails)
                emit(Resource.success(result))
            } catch (ex: java.lang.Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }

    suspend fun addNewSongsToPlaylist(
        songs: List<DeviceSong>,
        playlistId: Long
    ): LiveData<Resource<Boolean>> {

        return liveData(dispatcherIO) {
            emit(Resource.loading(null))

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

    private fun getFirstPlaylistMembersAlbumArtBitmap(playlistId: Long): Bitmap? {

        var contentUri: Uri? = null
        var bitmapResult: Bitmap? = null

        val collection =
            MediaStore.Audio.Playlists.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, playlistId)

        val projection = arrayOf(
            MediaStore.Audio.Playlists.Members.AUDIO_ID,
            MediaStore.Audio.Playlists.Members.ALBUM_ID,
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
                if (it != null && it.count > 0) {
                    it.moveToFirst()
                    val id =
                        it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID))
                    val albumId =
                        it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ALBUM_ID))

                    try {
                        val resolver = application.contentResolver

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            contentUri = ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                id
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