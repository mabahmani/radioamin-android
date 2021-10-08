package ir.mab.radioamin.vo.res

import ir.mab.radioamin.vo.Album
import ir.mab.radioamin.vo.Music
import ir.mab.radioamin.vo.Playlist
import ir.mab.radioamin.vo.Singer
import ir.mab.radioamin.vo.enums.TopicType

data class HomeTopicsRes(
    val topics: List<Topic>
)

data class Topic(
    val title: String,
    val topicType: TopicType,
    val musics: List<Music>,
    val playlists: List<Playlist>,
    val albums: List<Album>,
    val singers: List<Singer>
)