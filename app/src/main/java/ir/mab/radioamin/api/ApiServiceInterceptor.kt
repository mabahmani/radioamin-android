package ir.mab.radioamin.api

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class ApiServiceInterceptor(val sharedPreferences: SharedPreferences): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        Timber.d("intercept %s", originalRequest.url)
        return chain.proceed(originalRequest)
//        val requestBuilder = originalRequest.newBuilder()
//            .header("Authorization", "AuthToken")
//
//        val request = requestBuilder.build()
//
//        return chain.proceed(request)
    }

}