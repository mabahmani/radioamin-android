package ir.mab.radioamin.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mab.radioamin.util.AppConstants


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideSharedPreferences(
        application: Application
    ): SharedPreferences {
        return application.getSharedPreferences(
            AppConstants.PREFS.SHARED_PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    fun provideExoplayer(
        application: Application
    ): SimpleExoPlayer {
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()
        val player = SimpleExoPlayer.Builder(application).build()
        player.setAudioAttributes(audioAttributes, true)
        return player
    }
}