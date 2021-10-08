package ir.mab.radioamin.api

import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.vo.*
import ir.mab.radioamin.vo.generic.Page
import ir.mab.radioamin.vo.generic.SuccessResponse
import ir.mab.radioamin.vo.res.HomeTopicsRes
import ir.mab.radioamin.vo.res.JwtRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST(AppConstants.Base.VersionOne.ANONYMOUS + "/users/login-google")
    suspend fun loginUserByGoogle(@Body googleIdToken: String): Response<SuccessResponse<JwtRes>>

    @GET(AppConstants.Base.VersionOne.CONSUMER + "/home-topics")
    suspend fun getHomeTopics(): Response<SuccessResponse<HomeTopicsRes>>

    @GET(AppConstants.Base.VersionOne.CONSUMER + "/music")
    suspend fun getMusics(
        @Query("direction") direction:String = "DESC",
        @Query("page") page:Int = 0,
        @Query("size") size:Int = 10,
        @Query("sort") sort:String = "id",
        @Query("musicType") musicType:String = "VOCAL",
    ): Response<SuccessResponse<Page<Music>>>

    @GET(AppConstants.Base.VersionOne.CONSUMER + "/album")
    suspend fun getAlbums(
        @Query("direction") direction:String = "DESC",
        @Query("page") page:Int = 0,
        @Query("size") size:Int = 10,
        @Query("sort") sort:String = "id"
    ): Response<SuccessResponse<Page<Album>>>

    @GET(AppConstants.Base.VersionOne.CONSUMER + "/genre")
    suspend fun getGenres(
        @Query("direction") direction:String = "DESC",
        @Query("page") page:Int = 0,
        @Query("size") size:Int = 10,
        @Query("sort") sort:String = "id"
    ): Response<SuccessResponse<Page<Genre>>>

    @GET(AppConstants.Base.VersionOne.CONSUMER + "/artist")
    suspend fun getArtists(
        @Query("direction") direction:String = "DESC",
        @Query("page") page:Int = 0,
        @Query("size") size:Int = 10,
        @Query("sort") sort:String = "id"
    ): Response<SuccessResponse<Page<Singer>>>

    @GET(AppConstants.Base.VersionOne.CONSUMER + "/playlist")
    suspend fun getPlaylists(
        @Query("direction") direction:String = "DESC",
        @Query("page") page:Int = 0,
        @Query("size") size:Int = 10,
        @Query("sort") sort:String = "id"
    ): Response<SuccessResponse<Page<Playlist>>>
}