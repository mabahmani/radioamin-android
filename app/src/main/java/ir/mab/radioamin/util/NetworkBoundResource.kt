package ir.mab.radioamin.util

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.mab.radioamin.vo.generic.ErrorResponse
import ir.mab.radioamin.vo.generic.Resource
import ir.mab.radioamin.vo.generic.SuccessResponse
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

abstract class NetworkBoundResource<ResultType>(private val dispatchersIO: CoroutineDispatcher) {
    private val gson = Gson()
    private val errorType = object : TypeToken<ErrorResponse>() {}.type

    fun asLiveData() = liveData<Resource<ResultType>>(dispatchersIO) {
        emit(Resource.loading(null))

        val dbData = loadFromDb()

        if (shouldFetch(dbData)){

            emit(Resource.loading(dbData))

            try {
                val response = createCall()

                if(response.isSuccessful){
                    Timber.d("isSuccessful %s", response.body())
                    emit(Resource.success(response.body()!!.data))
                    saveCallResult(response.body()!!.data)
                }
                else{
                    val errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()!!.charStream(), errorType)
                    emit(Resource.error(response.code(), response.message(),null,errorResponse))
                }
            }

            catch (throwable: Throwable){
                when (throwable) {
                    is IOException -> {
                        emit(Resource.error(null,throwable.message?:"",null,null))
                    }
                    is HttpException -> {
                        emit(Resource.error(throwable.code(),throwable.message(),null,null))
                    }
                    else -> {
                        emit(Resource.error(null,throwable.message?:"",null,null))
                    }
                }
            }
        }
        else{
            emit(Resource.success(dbData))
        }
    }

    abstract suspend fun loadFromDb(): ResultType?
    abstract fun shouldFetch(data: ResultType?): Boolean
    abstract suspend fun createCall(): Response<SuccessResponse<ResultType>>
    abstract suspend fun saveCallResult(data: ResultType): ResultType?
}