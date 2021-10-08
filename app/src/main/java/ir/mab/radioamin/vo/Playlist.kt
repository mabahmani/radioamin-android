package ir.mab.radioamin.vo

data class Playlist(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val cover: Cover,
    val user: User,
    val musics: List<Music>
)