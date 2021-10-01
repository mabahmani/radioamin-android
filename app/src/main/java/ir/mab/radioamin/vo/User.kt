package ir.mab.radioamin.vo

data class User(
    val id: Long,
    val email: String,
    val active: Boolean,
    val createdAt: Long,
    val profile: Profile
)
