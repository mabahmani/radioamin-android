package ir.mab.radioamin.vo

data class Singer(
    val id: Long,
    val name: String,
    val avatar: Avatar,
    val albums: List<Album>,
    val musics: List<Music>,
    val followCounts: Long
)