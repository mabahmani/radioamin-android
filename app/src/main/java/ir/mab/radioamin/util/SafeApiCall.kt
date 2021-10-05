package ir.mab.radioamin.util

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.mab.radioamin.vo.enums.ErrorStatus
import ir.mab.radioamin.vo.generic.ErrorResponse
import ir.mab.radioamin.vo.generic.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException

private val gson: Gson = Gson()
private val errorType = object : TypeToken<ErrorResponse>() {}.type

suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, data: T? = null, context: Context? = null, apiCall: suspend () -> Response<T>): LiveData<Resource<T>> {
    return liveData(dispatcher) {
        try {

            emit(Resource.loading(data))

            val response =  apiCall.invoke()

            if (response.isSuccessful){
                emit(Resource.success(response.body()))
            }
            else{
                CoroutineScope(Dispatchers.Main).launch {
                    context?.errorToast(convertToErrorResponse(response.errorBody()!!).toString())
                }
                emit(Resource.error(response.code(),response.message(), response.body() ,convertToErrorResponse(response.errorBody()!!), ErrorStatus.HTTP_EXCEPTION))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> {
                    Timber.d(throwable)
                    if (throwable is ConnectException || throwable is UnknownHostException){
                        CoroutineScope(Dispatchers.Main).launch {
                            context?.networkErrorToast()
                        }
                        emit(Resource.error(null,throwable.message.toString(),null,null, ErrorStatus.NETWORK_CONNECTION_ERROR))
                    }
                    else{
                        CoroutineScope(Dispatchers.Main).launch {
                            context?.errorToast(throwable.message.toString())
                        }
                        emit(Resource.error(null,throwable.message.toString(),null,null, ErrorStatus.IO_EXCEPTION))
                    }
                }
                is HttpException -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        context?.errorToast(convertToErrorResponse(throwable.response()!!.errorBody()!!).toString())
                    }
                    val code = throwable.code()
                    emit(Resource.error(code, throwable.message.toString(), null,convertToErrorResponse(throwable.response()!!.errorBody()!!), ErrorStatus.HTTP_EXCEPTION))
                }
                else -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        context?.errorToast(throwable.message.toString())
                    }
                    emit(Resource.error(null,throwable.message.toString(),null, null, null))
                }
            }
        }
    }
}

private fun convertToErrorResponse(errorBody: ResponseBody): ErrorResponse?{
    return gson.fromJson(errorBody.charStream(), errorType)
}
