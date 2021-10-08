package ir.mab.radioamin.vm.remote

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mab.radioamin.api.ApiService
import ir.mab.radioamin.di.IoDispatcher
import ir.mab.radioamin.util.safeApiCall
import ir.mab.radioamin.vo.*
import ir.mab.radioamin.vo.generic.Page
import ir.mab.radioamin.vo.generic.Resource
import ir.mab.radioamin.vo.generic.SuccessResponse
import ir.mab.radioamin.vo.res.HomeTopicsRes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiService: ApiService,
    private val application: Application,
    @IoDispatcher private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private var homeTopicsResult: LiveData<Resource<SuccessResponse<HomeTopicsRes>>> = MutableLiveData()
    private var musicsResult: LiveData<Resource<SuccessResponse<Page<Music>>>> = MutableLiveData()
    private var albumsResult: LiveData<Resource<SuccessResponse<Page<Album>>>> = MutableLiveData()
    private var artistsResult: LiveData<Resource<SuccessResponse<Page<Singer>>>> = MutableLiveData()
    private var genresResult: LiveData<Resource<SuccessResponse<Page<Genre>>>> = MutableLiveData()
    private var playlistsResult: LiveData<Resource<SuccessResponse<Page<Playlist>>>> = MutableLiveData()

    fun getHomeTopics(forceRefresh: Boolean = false): LiveData<Resource<SuccessResponse<HomeTopicsRes>>> {
        if (homeTopicsResult.value == null || forceRefresh) {
            viewModelScope.launch {
                homeTopicsResult =
                    safeApiCall(dispatcherIO, homeTopicsResult.value?.data, application) {
                        apiService.getHomeTopics()
                    }
            }
        }
        return homeTopicsResult
    }

    fun getMusics(
        forceRefresh: Boolean = false,
        direction: String = "DESC",
        page: Int = 0,
        size: Int = 10,
        sort: String = "id",
        musicType: String = "VOCAL"
    ): LiveData<Resource<SuccessResponse<Page<Music>>>> {

        if (musicsResult.value == null || forceRefresh) {
            viewModelScope.launch {
                musicsResult = safeApiCall(dispatcherIO, musicsResult.value?.data, application) {
                    apiService.getMusics(direction, page, size, sort, musicType)
                }
            }
        }

        return musicsResult
    }

    fun getAlbums(
        forceRefresh: Boolean = false,
        direction: String = "DESC",
        page: Int = 0,
        size: Int = 10,
        sort: String = "id",
    ): LiveData<Resource<SuccessResponse<Page<Album>>>> {

        if (albumsResult.value == null || forceRefresh) {
            viewModelScope.launch {
                albumsResult = safeApiCall(dispatcherIO, albumsResult.value?.data, application) {
                    apiService.getAlbums(direction,page,size,sort)
                }
            }
        }

        return albumsResult
    }

    fun getArtists(
        forceRefresh: Boolean = false,
        direction: String = "DESC",
        page: Int = 0,
        size: Int = 10,
        sort: String = "id",
    ): LiveData<Resource<SuccessResponse<Page<Singer>>>> {

        if (artistsResult.value == null || forceRefresh) {
            viewModelScope.launch {
                artistsResult = safeApiCall(dispatcherIO, artistsResult.value?.data, application) {
                    apiService.getArtists(direction,page,size,sort)
                }
            }
        }

        return artistsResult
    }

    fun getGenres(
        forceRefresh: Boolean = false,
        direction: String = "DESC",
        page: Int = 0,
        size: Int = 10,
        sort: String = "id",
    ): LiveData<Resource<SuccessResponse<Page<Genre>>>> {

        if (genresResult.value == null || forceRefresh) {
            viewModelScope.launch {
                genresResult = safeApiCall(dispatcherIO, genresResult.value?.data, application) {
                    apiService.getGenres(direction,page,size,sort)
                }
            }
        }

        return genresResult
    }

    fun getPlaylists(
        forceRefresh: Boolean = false,
        direction: String = "DESC",
        page: Int = 0,
        size: Int = 10,
        sort: String = "id",
    ): LiveData<Resource<SuccessResponse<Page<Playlist>>>> {

        if (playlistsResult.value == null || forceRefresh) {
            viewModelScope.launch {
                playlistsResult = safeApiCall(dispatcherIO, playlistsResult.value?.data, application) {
                    apiService.getPlaylists(direction,page,size,sort)
                }
            }
        }

        return playlistsResult
    }

}