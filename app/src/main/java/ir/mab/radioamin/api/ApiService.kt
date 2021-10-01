package ir.mab.radioamin.api

import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.vo.generic.SuccessResponse
import ir.mab.radioamin.vo.res.HomeTopicsRes
import ir.mab.radioamin.vo.res.JwtRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST(AppConstants.Base.VersionOne.ANONYMOUS + "/users/login-google")
    suspend fun loginUserByGoogle(@Body googleIdToken: String): Response<SuccessResponse<JwtRes>>

    @GET(AppConstants.Base.VersionOne.CONSUMER + "/home-topics")
    suspend fun getHomeTopics(): Response<SuccessResponse<HomeTopicsRes>>
}