package ir.mab.radioamin.api

import android.content.SharedPreferences
import ir.mab.radioamin.util.AppConstants
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class ApiServiceInterceptor(val sharedPreferences: SharedPreferences): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()
        Timber.d("intercept %s", originalRequest.url)

        val accessToken = sharedPreferences.getString(AppConstants.Prefs.ACCESS_TOKEN, null)

        val requestBuilder = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")

        val request = requestBuilder.build()

        return chain.proceed(request)

    }

}