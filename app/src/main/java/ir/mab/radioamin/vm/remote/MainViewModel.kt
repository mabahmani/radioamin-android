package ir.mab.radioamin.vm.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mab.radioamin.api.ApiService
import ir.mab.radioamin.di.IoDispatcher
import ir.mab.radioamin.util.safeApiCall
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
    @IoDispatcher private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private var homeTopicsResult: LiveData<Resource<SuccessResponse<HomeTopicsRes>>> = MutableLiveData()

    fun getHomeTopics(): LiveData<Resource<SuccessResponse<HomeTopicsRes>>>{
        if (homeTopicsResult.value == null){
            viewModelScope.launch {
                homeTopicsResult = safeApiCall(dispatcherIO){
                    apiService.getHomeTopics()
                }
            }
        }
        return homeTopicsResult
    }

}