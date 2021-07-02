package ir.mab.radioamin.vo.generic

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val errorData: ErrorResponse?,
    val message: String?,
    val responseCode: Int?
) {
    companion object {

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null, null, null)
        }

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null,null, null)
        }

        fun <T> error(responseCode: Int?, msg: String, data: T?, errorData: ErrorResponse?): Resource<T> {
            return Resource(Status.ERROR, data, errorData, msg, responseCode)
        }

    }
}
