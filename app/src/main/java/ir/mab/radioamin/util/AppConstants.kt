package ir.mab.radioamin.util

object AppConstants {

    object Prefs{
        const val ACCESS_TOKEN = "ACCESS_TOKEN"
        const val REFRESH_TOKEN = "REFRESH_TOKEN"
        const val IS_LOGIN = "IS_LOGIN"
        const val SHARED_PREFS_NAME = "Radioamin_Prefs"
        const val BLACK_LIST_FOLDERS = "BLACK_LIST_FOLDERS"
    }
    object Arguments{
        const val PLAYLIST_ID = "PLAYLIST_ID"
        const val PLAYLIST_NAME = "PLAYLIST_NAME"
        const val ALBUM_NAME = "ALBUM_NAME"
        const val ALBUM_ID = "ALBUM_ID"
        const val ARTIST_ID = "ARTIST_ID"
        const val ARTIST_NAME = "ARTIST_NAME"
        const val GENRE_ID = "GENRE_ID"
        const val GENRE_NAME = "GENRE_NAME"
        const val SONG_DATA = "SONG_DATA"
    }
    object Notifications{
        const val PLAYER_NOTIFICATION_ID = 1001
        const val PLAYER_NOTIFICATION_CHANNEL_ID = "RadioaminDeviceFilesPlayerNotificationChannel"
    }

    object RequestCode{
        const val ONE_TAP_SIGN_IN_REQUEST_CODE = 2001
    }

    object Base{
        const val URL = "http://192.168.1.50:8080/api/"
        object VersionOne{
            const val CONSUMER: String = URL + "v1/consumer"
            const val ANONYMOUS: String = URL + "v1/anonymous"
        }
    }
}