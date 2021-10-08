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
import ir.mab.radioamin.vo.res.JwtRes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val apiService: ApiService,
    @IoDispatcher private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    fun loginByGoogle(googleIdToken: String): LiveData<Resource<SuccessResponse<JwtRes>>>{

        var result: LiveData<Resource<SuccessResponse<JwtRes>>> = MutableLiveData()


        viewModelScope.launch {

            result = safeApiCall(dispatcherIO) {
                apiService.loginUserByGoogle(googleIdToken)
            }
        }

        return result
    }
}