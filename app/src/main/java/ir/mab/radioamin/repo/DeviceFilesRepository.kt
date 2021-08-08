package ir.mab.radioamin.repo

import android.app.Application
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

open class DeviceFilesRepository(private val application: Application) {

    companion object {
        private const val VOLUME_EXTERNAL = "external"
    }

    fun queryMediaStore(
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

    fun getPlaylistsUri(): Uri {
        return MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
    }

    fun getPlaylistUri(playlistId: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, playlistId)
    }

    fun getPlaylistMembersUri(playlistId: Long): Uri {
        return MediaStore.Audio.Playlists.Members.getContentUri(VOLUME_EXTERNAL, playlistId)
    }

    fun getSongsUri(): Uri {
        return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    fun getSongUri(songId: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)
    }

    fun getArtistsUri(): Uri {
        return MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
    }

    fun getAlbumUri(albumId: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)
    }

    fun getAlbumsUri(): Uri {
        return MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
    }

    fun getGenresUri(): Uri {
        return MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI
    }

    fun getGenreUri(genreId: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, genreId)
    }

    fun getGenreMembersUri(genreId: Long): Uri {
        return MediaStore.Audio.Genres.Members.getContentUri(VOLUME_EXTERNAL, genreId)
    }
}