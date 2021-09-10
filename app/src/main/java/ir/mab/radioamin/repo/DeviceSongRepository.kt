package ir.mab.radioamin.repo

import android.app.Application
import android.content.SharedPreferences
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.vo.devicefiles.DeviceSong
import ir.mab.radioamin.vo.devicefiles.DeviceSongFolder
import ir.mab.radioamin.vo.devicefiles.DeviceSongTag
import ir.mab.radioamin.vo.generic.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.images.Artwork
import org.jaudiotagger.tag.images.ArtworkFactory
import java.io.File


class DeviceSongRepository(
    private val application: Application,
    private val sharedPreferences: SharedPreferences,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : DeviceFilesRepository(application) {

    suspend fun getDeviceSongs(): LiveData<Resource<List<DeviceSong>>> {
        return liveData(dispatcherIO) {
            val songs = mutableListOf<DeviceSong>()

            emit(Resource.loading(null))

            val collection = getSongsUri()

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} = ?"

            val selectionArgs = arrayOf("1")

            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            val blackList = sharedPreferences.getStringSet(AppConstants.Prefs.BLACK_LIST_FOLDERS, mutableSetOf())

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
                        val duration =
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                        val artist =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                        val album =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                        val contentUri: Uri = getSongUri(id)
                        val data: String =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                        val displayName: String =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))

                        val path = data.substringBeforeLast(displayName)

                        if(blackList.isNullOrEmpty() || !blackList.contains(path)){
                            songs.add(
                                DeviceSong(
                                    id,
                                    albumId,
                                    name,
                                    artist,
                                    album,
                                    duration,
                                    contentUri,
                                    data
                                )
                            )
                        }
                    }

                    emit(Resource.success(songs))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }

    suspend fun getDeviceSongsFolders(): LiveData<Resource<List<DeviceSongFolder>>> {
        return liveData(dispatcherIO) {

            val folders = mutableListOf<DeviceSongFolder>()

            emit(Resource.loading(null))

            val collection = getSongsUri()

            val projection = arrayOf(
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} = ?"

            val selectionArgs = arrayOf("1")


            try {
                queryMediaStore(
                    collection,
                    projection,
                    selection,
                    selectionArgs,
                    null
                ).use {

                    val blackList = sharedPreferences.getStringSet(AppConstants.Prefs.BLACK_LIST_FOLDERS, mutableSetOf())

                    while (it != null && it.moveToNext()) {

                        val data: String =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                        val displayName: String =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))

                        val path = data.substringBeforeLast(displayName)

                        var add = true
                        for (folder in folders){
                            if (folder.path == path) {
                                add = false
                                break
                            }
                        }

                        if (add){
                            if (!blackList.isNullOrEmpty() && blackList.contains(path)){
                                folders.add(DeviceSongFolder(path, false))
                            }
                            else{
                                folders.add(DeviceSongFolder(path, true))
                            }
                        }
                    }

                    emit(Resource.success(folders))
                }
            } catch (ex: Exception) {
                emit(Resource.error(null, ex.toString(), null, null))
            }
        }
    }

    suspend fun getDeviceSong(songId: Long): LiveData<Resource<DeviceSong>> {
        return liveData(dispatcherIO) {

            emit(Resource.loading(null))

            val collection = getSongUri(songId)

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATA
            )


            try {
                queryMediaStore(
                    collection,
                    projection,
                    null,
                    null,
                    null
                ).use {

                    while (it != null && it.count > 0) {

                        it.moveToFirst()

                        val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                        val albumId =
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                        val name =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                        val duration =
                            it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                        val artist =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                        val album =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                        val contentUri: Uri = getSongUri(id)
                        val data: String =
                            it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))

                        emit(
                            Resource.success(
                                DeviceSong(
                                    id,
                                    albumId,
                                    name,
                                    artist,
                                    album,
                                    duration,
                                    contentUri,
                                    data
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

    fun getDeviceSongTags(path: String): LiveData<Resource<DeviceSongTag>> {
        return liveData(dispatcherIO) {
            emit(Resource.loading(null))

            try {
                val audioFileIO = AudioFileIO.read(File(path))

                emit(
                    Resource.success(
                        DeviceSongTag(
                            audioFileIO.tagOrCreateAndSetDefault.getFirst(FieldKey.TITLE),
                            audioFileIO.tagOrCreateAndSetDefault.getFirst(FieldKey.ALBUM),
                            audioFileIO.tagOrCreateAndSetDefault.getFirst(FieldKey.ARTIST),
                            audioFileIO.tagOrCreateAndSetDefault.getFirst(FieldKey.GENRE),
                            audioFileIO.tagOrCreateAndSetDefault.getFirst(FieldKey.LANGUAGE),
                            audioFileIO.tagOrCreateAndSetDefault.getFirst(FieldKey.COUNTRY),
                            audioFileIO.tagOrCreateAndSetDefault.getFirst(FieldKey.YEAR),
                            audioFileIO.tagOrCreateAndSetDefault.getFirst(FieldKey.LYRICS)
                        )
                    )
                )
            } catch (ex: java.lang.Exception) {
                emit(Resource.error(null, ex.message.toString(), null, null))
            }
        }
    }

    fun writeDeviceSongTags(
        path: String,
        deviceSongTag: DeviceSongTag,
        coverArtUri: Uri?
    ): LiveData<Resource<Boolean>> {
        return liveData(dispatcherIO) {
            emit(Resource.loading(null))

            try {

                val audioFileIO = AudioFileIO.read(File(path))

                val tag = audioFileIO.tagOrCreateAndSetDefault
                tag.setField(FieldKey.TITLE, deviceSongTag.title)
                tag.setField(FieldKey.ALBUM, deviceSongTag.album)
                tag.setField(FieldKey.ARTIST, deviceSongTag.artist)
                tag.setField(FieldKey.GENRE, deviceSongTag.genre)
                tag.setField(FieldKey.LANGUAGE, deviceSongTag.language)
                tag.setField(FieldKey.COUNTRY, deviceSongTag.country)
                if (deviceSongTag.year.isNullOrBlank())
                    tag.setField(FieldKey.YEAR, "0")
                else
                    tag.setField(FieldKey.YEAR, deviceSongTag.year)
                tag.setField(FieldKey.LYRICS, deviceSongTag.lyrics)

                if (coverArtUri != null) {
                    tag.deleteArtworkField()
                    tag.setField(getArtworkFile(coverArtUri))
                }

                audioFileIO.commit()

                MediaScannerConnection.scanFile(application, arrayOf(path), null, null)

                emit(
                    Resource.success(true)
                )

            } catch (ex: java.lang.Exception) {
                emit(Resource.error(null, ex.message.toString(), false, null))
            }
        }
    }

    fun deleteDeviceSong(songId: Long): LiveData<Resource<Boolean>> {
        return liveData(dispatcherIO) {

            emit(Resource.loading(null))

            try {
                val contentUri = getSongUri(songId)
                application.contentResolver.delete(contentUri, null, null)
                emit(Resource.success(true))

            } catch (ex: java.lang.Exception) {
                emit(Resource.error(null, ex.message.toString(), false, null))
            }
        }
    }

    private fun getArtworkFile(uri: Uri): Artwork {

        var path = ""

        val projection = arrayOf(
            MediaStore.Images.Media.DATA
        )

        queryMediaStore(
            uri,
            projection,
            null,
            null,
            null
        ).use {
            if (it != null && it.count > 0) {
                it.moveToFirst()
                path = it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
            }
        }

        return ArtworkFactory.createArtworkFromFile(File(path))
    }
}

