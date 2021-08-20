package ir.mab.radioamin.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.android.exoplayer2.SimpleExoPlayer
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
        application: Application): SharedPreferences {
        return application.getSharedPreferences(AppConstants.PREFS.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    fun provideExoplayer(
        application: Application): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(application).build()
    }
}