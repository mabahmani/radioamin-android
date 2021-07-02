package ir.mab.radioamin.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mab.radioamin.di.IoDispatcher
import ir.mab.radioamin.repo.DeviceFilesRepository
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
class RepoModule {

    @Provides
    fun provideDeviceFilesRepository(
        application: Application,
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher
    ): DeviceFilesRepository {
        return DeviceFilesRepository(application, coroutineDispatcher)
    }

}