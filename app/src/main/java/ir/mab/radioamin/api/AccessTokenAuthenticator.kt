package ir.mab.radioamin.api

import android.content.SharedPreferences
import androidx.core.content.edit
import ir.mab.radioamin.ui.main.MainActivity
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.vo.res.JwtRes
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber

class AccessTokenAuthenticator(private val tokenService: TokenService, val sharedPreferences: SharedPreferences):
    Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {

        Timber.d("authenticate")
        val accessToken = sharedPreferences.getString(AppConstants.Prefs.ACCESS_TOKEN, null)

        Timber.d("authenticate accessToken %s", accessToken)

        if (!isRequestWithAccessToken(response) || accessToken == null) {
            Timber.d("authenticate accessToken is null")
            return null
        }

        synchronized (this) {
            val newAccessToken = sharedPreferences.getString(AppConstants.Prefs.ACCESS_TOKEN, null)
            Timber.d("authenticate accessToken %s newAccessToken %s ",accessToken, newAccessToken)
            // Access token is refreshed in another thread.
            if (!newAccessToken.isNullOrEmpty() && accessToken != newAccessToken) {
                return newRequestWithAccessToken(response.request, newAccessToken)
            }

            // Need to refresh an access token
            val updatedAccessToken = refreshAccessToken()
            Timber.d("authenticate updatedAccessToken %s ",updatedAccessToken)
            return newRequestWithAccessToken(response.request, updatedAccessToken)
        }
    }

    private fun isRequestWithAccessToken(response: Response): Boolean {
        val header = response.request.header("Authorization")
        return header != null && header.startsWith("Bearer")
    }

    private fun newRequestWithAccessToken(request: Request, accessToken: String?): Request? {
        if (accessToken.isNullOrEmpty()){
            return null
        }
        return request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }

    private fun refreshAccessToken(): String? {
        val refreshToken = sharedPreferences.getString(AppConstants.Prefs.REFRESH_TOKEN, null)

        if (!refreshToken.isNullOrEmpty()){
            val response = tokenService.refreshAccessToken("Bearer $refreshToken").execute()
            Timber.d("authenticate refreshAccessToken %s ",response)
            return if (response.isSuccessful){
                saveToken(response.body()?.data)
            } else{
                MainActivity.userLogout.postValue(true)
                null
            }
        }
        return null
    }

    private fun saveToken(jwtRes: JwtRes?): String?{

        if (jwtRes != null){

            sharedPreferences.edit {
                putString(AppConstants.Prefs.ACCESS_TOKEN, jwtRes.accessToken)
                putString(AppConstants.Prefs.REFRESH_TOKEN, jwtRes.refreshToken)
            }

            return jwtRes.accessToken
        }

        return null

    }
}
