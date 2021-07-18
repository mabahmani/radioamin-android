package ir.mab.radioamin.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mab.radioamin.di.IoDispatcher
import ir.mab.radioamin.repo.DeviceAlbumRepository
import ir.mab.radioamin.repo.DevicePlaylistRepository
import ir.mab.radioamin.repo.DeviceSongRepository
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

}