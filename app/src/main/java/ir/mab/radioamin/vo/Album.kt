package ir.mab.radioamin.vo

data class Album(
    val id: Long,
    val name: String,
    val releaseDate: Long,
    val singer: Singer,
    val cover: Cover,
    val musics:List<Music>
)