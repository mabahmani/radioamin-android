package ir.mab.radioamin.vo

import ir.mab.radioamin.vo.enums.MusicType

data class Music(
    val id: Long,
    val name: String,
    val published: Boolean,
    val musicType: MusicType,
    val singer: Singer,
    val cover: Cover,
    val genres: List<Genre>,
    val language: Language,
    val album: Album,
    val lyric: String,
    val musicUrls: List<MusicUrl>
    )