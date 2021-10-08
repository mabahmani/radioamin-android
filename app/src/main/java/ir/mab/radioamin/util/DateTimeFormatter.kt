package ir.mab.radioamin.util

import java.util.concurrent.TimeUnit

object DateTimeFormatter {
    fun millisToHumanTime(duration: Long) : String{
        val min = TimeUnit.MILLISECONDS.toMinutes(duration)
        val sec = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(min)
        return String.format("%02d:%02d", min, sec)
    }
}