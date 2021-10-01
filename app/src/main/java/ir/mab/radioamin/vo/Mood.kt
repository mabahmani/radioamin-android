package ir.mab.radioamin.vo

data class Mood(
    val id: Long,
    val name: String,
    val genres: List<Genre>
)
