package ir.mab.radioamin.vo

import ir.mab.radioamin.vo.enums.MusicUrlType

data class MusicUrl(
    val id: Long,
    val musicUrlType: MusicUrlType,
    val url: String,
)