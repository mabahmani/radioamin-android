package ir.mab.radioamin.vo

data class Profile(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val displayName: String,
    val bio: String,
    val avatar: Avatar
)
