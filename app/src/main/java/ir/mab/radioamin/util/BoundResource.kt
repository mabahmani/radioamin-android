package ir.mab.radioamin.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import ir.mab.radioamin.vo.generic.ApiResponse
import ir.mab.radioamin.vo.generic.Resource
import retrofit2.Response

abstract class BoundResource<ResultType, RequestType> {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        @Suppress("LeakingThis")
        val dbSource = loadFromDb()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(newData))
        }

        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)

            if (response.isSuccessful) {
                saveCallResult(response.body()?.successData?.content)
                result.addSource(loadFromDb()) { newData ->
                    setValue(Resource.success(newData))
                }
            } else {
                result.addSource(dbSource) { newData ->
                    setValue(
                        Resource.error(
                            response.code(),
                            response.message(),
                            newData,
                            response.body()?.errorData
                        )
                    )
                }
            }
        }
    }

    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    protected abstract fun loadFromDb(): LiveData<ResultType>

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract fun createCall(): LiveData<Response<ApiResponse<RequestType>>>

    protected abstract fun saveCallResult(data: RequestType?)

}