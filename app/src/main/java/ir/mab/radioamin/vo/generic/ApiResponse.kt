package ir.mab.radioamin.vo.generic

data class ApiResponse<T>(val successData: SuccessResponse<T>, val errorData: ErrorResponse)