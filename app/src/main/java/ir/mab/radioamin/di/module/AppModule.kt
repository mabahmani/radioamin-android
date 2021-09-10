package ir.mab.radioamin.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mab.radioamin.api.ApiService
import ir.mab.radioamin.api.ApiServiceInterceptor
import ir.mab.radioamin.util.AppConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideSharedPreferences(
        application: Application
    ): SharedPreferences {
        return application.getSharedPreferences(
            AppConstants.Prefs.SHARED_PREFS_NAME,
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
        player.setHandleAudioBecomingNoisy(true)
        return player
    }

    @Provides
    fun provideOkHttpClient(
        sharedPreferences: SharedPreferences
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val builder = OkHttpClient().newBuilder()
            .addInterceptor(ApiServiceInterceptor(sharedPreferences))
            .addInterceptor(logging)
        return builder.build()
    }

    @Provides
    fun provideApiService(
        okHttpClient: OkHttpClient
    ): ApiService {

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConstants.Base.URL)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder()
                .setLenient()
                .create()))
            .client(okHttpClient)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}