package ir.mab.radioamin.repo

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import ir.mab.radioamin.vo.DeviceGenre
import ir.mab.radioamin.vo.DeviceSong
import ir.mab.radioamin.vo.generic.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class DeviceGenreRepository(
    application: Application,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : DeviceFilesRepository(application) {

    suspend fun getDeviceGenres(): LiveData<Resource<List<DeviceGenre>>> {
        return liveData(dispatcherIO) {

            val genres = mutableListOf<DeviceGenre>()

            emit(Resource.loading(null))

            val collection = getGenresUri()

            val projection = arrayOf(
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME
            )

            val sortOrder = "${MediaStore.Audio.Genres.NAME} ASC"

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
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID))
                        val name =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME))

                        genres.add(
                            DeviceGenre(
                                id,
                                name,
                                getGenreMembersCount(id),
                            )
                        )
                    }

                    emit(Resource.success(genres))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }

    suspend fun getDeviceGenre(genreId: Long): LiveData<Resource<DeviceGenre>> {
        return liveData(dispatcherIO) {

            emit(Resource.loading(null))

            val collection = getGenresUri()

            val projection = arrayOf(
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME
            )

            val selection = "${MediaStore.Audio.Genres._ID} = ?"
            val selectionArgs = arrayOf(genreId.toString())

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
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID))
                        val name =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME))

                        emit(
                            Resource.success(
                                DeviceGenre(
                                    id,
                                    name,
                                    getGenreMembersCount(id),
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

    suspend fun getGenreMembers(genreId: Long): LiveData<Resource<List<DeviceSong>>> {

        return liveData(dispatcherIO) {
            val songs = mutableListOf<DeviceSong>()

            emit(Resource.loading(null))

            val collection = getGenreMembersUri(genreId)

            val projection = arrayOf(
                MediaStore.Audio.Genres.Members.AUDIO_ID,
                MediaStore.Audio.Genres.Members.TITLE,
                MediaStore.Audio.Genres.Members.ARTIST,
                MediaStore.Audio.Genres.Members.DURATION,
                MediaStore.Audio.Genres.Members.ALBUM_ID)

            val sortOrder = "${MediaStore.Audio.Genres.Members.DEFAULT_SORT_ORDER} ASC"

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
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.AUDIO_ID))
                        val title =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.TITLE))
                        val artist =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.ARTIST))
                        val duration =
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.DURATION))
                        val albumId =
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.ALBUM_ID))
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

    private fun getGenreMembersCount(genreId: Long): Int {

        var membersCount = 0

        val collection = getGenreMembersUri(genreId)

        val projection = arrayOf(
            MediaStore.Audio.Genres.Members._ID)

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

}