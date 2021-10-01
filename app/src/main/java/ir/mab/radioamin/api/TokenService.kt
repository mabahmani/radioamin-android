package ir.mab.radioamin.api

import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.vo.generic.SuccessResponse
import ir.mab.radioamin.vo.res.JwtRes
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface TokenService {
    @POST(AppConstants.Base.VersionOne.ANONYMOUS + "/users/new-token")
    fun refreshAccessToken(@Header("Authorization") refreshToken: String): Call<SuccessResponse<JwtRes>>
}