package ir.yar.anbar.domain.util


sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(
        val message: String,
        val code: Int? = null,
        val data: T? = null
    ) : Resource<T>()
}