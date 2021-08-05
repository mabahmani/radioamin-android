package ir.mab.radioamin.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mab.radioamin.di.IoDispatcher
import ir.mab.radioamin.repo.*
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
class RepoModule {

    @Provides
    fun provideDevicePlaylistRepository(
        application: Application,
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher
    ): DevicePlaylistRepository {
        return DevicePlaylistRepository(application, coroutineDispatcher)
    }

    @Provides
    fun provideDeviceAlbumRepository(
        application: Application,
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher
    ): DeviceAlbumRepository {
        return DeviceAlbumRepository(application, coroutineDispatcher)
    }

    @Provides
    fun provideDeviceSongRepository(
        application: Application,
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher
    ): DeviceSongRepository {
        return DeviceSongRepository(application, coroutineDispatcher)
    }

    @Provides
    fun provideDeviceArtistRepository(
        application: Application,
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher
    ): DeviceArtistRepository {
        return DeviceArtistRepository(application, coroutineDispatcher)
    }

    @Provides
    fun provideDeviceGenreRepository(
        application: Application,
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher
    ): DeviceGenreRepository {
        return DeviceGenreRepository(application, coroutineDispatcher)
    }

}