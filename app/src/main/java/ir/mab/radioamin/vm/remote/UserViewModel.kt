package ir.mab.radioamin.vm.remote

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mab.radioamin.api.ApiService
import ir.mab.radioamin.di.IoDispatcher
import ir.mab.radioamin.util.NetworkBoundResource
import ir.mab.radioamin.vo.generic.Resource
import ir.mab.radioamin.vo.generic.SuccessResponse
import ir.mab.radioamin.vo.res.JwtRes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val apiService: ApiService,
    @IoDispatcher private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    fun loginByGoogle(googleIdToken: String): LiveData<Resource<JwtRes>>{

        return object: NetworkBoundResource<JwtRes>(dispatcherIO){
            override suspend fun loadFromDb(): JwtRes? {
                return null
            }

            override fun shouldFetch(data: JwtRes?): Boolean {
                return true
            }

            override suspend fun createCall(): Response<SuccessResponse<JwtRes>> {
                return apiService.loginUserByGoogle(googleIdToken)
            }

            override suspend fun saveCallResult(data: JwtRes): JwtRes? {
                return null
            }
        }.asLiveData()
    }
}