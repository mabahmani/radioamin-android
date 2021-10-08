package ir.mab.radioamin.vo.generic

data class SuccessResponse<T>(val message: String, val data: T)