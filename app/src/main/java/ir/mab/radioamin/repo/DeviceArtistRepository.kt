package ir.mab.radioamin.repo

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
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
) : DeviceFilesRepository(application) {

    suspend fun getDeviceArtists(): LiveData<Resource<List<DeviceArtist>>> {
        return liveData(dispatcherIO) {

            val artists = mutableListOf<DeviceArtist>()

            emit(Resource.loading(null))

            val collection = getArtistsUri()

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
                                getThumbnailAlbumArt(getFirstArtistSongAlbumId(id))
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

    suspend fun getDeviceArtistSongs(artistId: Long): LiveData<Resource<List<DeviceSong>>> {
        return liveData(dispatcherIO) {

            val songs = mutableListOf<DeviceSong>()

            emit(Resource.loading(null))

            val collection = getSongsUri()

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
                        val albumId =
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                        val name =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                        val artist =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                        val duration =
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))

                        val contentUri: Uri = getSongUri(id)

                        songs.add(
                            DeviceSong(
                                id,
                                name,
                                artist,
                                duration,
                                contentUri,
                                getThumbnailAlbumArt(albumId)
                            )
                        )

                    }

                    emit(Resource.success(songs))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }


    private fun getFirstArtistSongAlbumId(artistId: Long): Long {

        val collection = getSongsUri()

        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM_ID
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
                return if (it != null && it.count > 0) {
                    it.moveToFirst()

                    it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                } else {
                    -1
                }
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            return -1
        }
    }

}

