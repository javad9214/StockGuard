package ir.yar.anbar.data.remote.util


import com.google.gson.Gson
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.retrofit.errorBody
import com.skydoves.sandwich.retrofit.statusCode
import ir.yar.anbar.data.remote.dto.response.ResponseDto
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


object ApiResponseHandler {

    @PublishedApi
    internal val gson = Gson()

    /**
     * The server's error bodies carry the same {resCode, resMessage} envelope
     * as success bodies (BaseController.generateErrorResponse), so surface
     * resMessage. Non-envelope bodies (e.g. Spring Security 401 JSON) and
     * unreadable bodies fall back to the HTTP status code.
     */
    @PublishedApi
    internal fun ApiResponse.Failure.Error.envelopeMessage(): String {
        val body = runCatching { errorBody?.string() }.getOrNull()
        val resMessage = body?.let {
            runCatching { gson.fromJson(it, ResponseDto<Unit>::class.java).resMessage }.getOrNull()
        }
        return resMessage ?: "HTTP ${statusCode.code}"
    }

    /**
     * Handles ApiResponse and converts it to Flow<Resource<T>>
     */
    inline fun <T, R> handleApiResponse(
        crossinline apiCall: suspend () -> ApiResponse<T>,
        crossinline mapper: (T) -> R
    ): Flow<Resource<R>> = flow {
        emit(Resource.Loading())

        try {
            when (val response = apiCall()) {
                is ApiResponse.Success -> {
                    emit(Resource.Success(mapper(response.data)))
                }
                is ApiResponse.Failure.Error -> {
                    emit(
                        Resource.Error(
                            message = response.envelopeMessage(),
                            code = response.statusCode.code
                        )
                    )
                }
                is ApiResponse.Failure.Exception -> {
                    emit(
                        Resource.Error(
                            message = response.message ?: "Unknown error occurred"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(
                Resource.Error(
                    message = e.message ?: "Network error occurred"
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    /**
     *  Handles ResponseDto
     */
    inline fun <T, R> handleApiResponseWithMessage(
        crossinline apiCall: suspend () -> ApiResponse<ResponseDto<T>>,
        crossinline mapper: (T) -> R
    ): Flow<Resource<R>> = flow {
        emit(Resource.Loading())

        try {
            when (val response = apiCall()) {
                is ApiResponse.Success -> {
                    val responseDto = response.data
                    if (responseDto.isOk && responseDto.info != null) {
                        emit(Resource.Success(mapper(responseDto.info)))
                    } else {
                        emit(
                            Resource.Error(
                                message = responseDto.resMessage ?: "Unknown error occurred"
                            )
                        )
                    }
                }
                is ApiResponse.Failure.Error -> {
                    emit(
                        Resource.Error(
                            message = response.envelopeMessage(),
                            code = response.statusCode.code
                        )
                    )
                }
                is ApiResponse.Failure.Exception -> {
                    emit(
                        Resource.Error(
                            message = response.message ?: "Unknown error occurred"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(
                Resource.Error(
                    message = e.message ?: "Network error occurred"
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Handles ApiResponse without mapping (when T and R are the same)
     */
    inline fun <T> handleApiResponse(
        crossinline apiCall: suspend () -> ApiResponse<T>
    ): Flow<Resource<T>> = flow {
        emit(Resource.Loading())

        try {
            when (val response = apiCall()) {
                is ApiResponse.Success -> {
                    emit(Resource.Success(response.data))
                }
                is ApiResponse.Failure.Error -> {
                    emit(
                        Resource.Error(
                            message = response.envelopeMessage(),
                            code = response.statusCode.code
                        )
                    )
                }
                is ApiResponse.Failure.Exception -> {
                    emit(
                        Resource.Error(
                            message = response.message ?: "Unknown error occurred"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(
                Resource.Error(
                    message = e.message ?: "Network error occurred"
                )
            )
        }
    }.flowOn(Dispatchers.IO)


    /**
     * Handles ResponseDto without mapping
     */
    inline fun <T : Any> handleApiResponseWithMessage(
        crossinline apiCall: suspend () -> ApiResponse<ResponseDto<T>>
    ): Flow<Resource<T>> = flow {
        emit(Resource.Loading())

        try {
            when (val response = apiCall()) {
                is ApiResponse.Success -> {
                    val responseDto = response.data
                    if (responseDto.isOk && responseDto.info != null) {
                        emit(Resource.Success(responseDto.info))
                    } else {
                        emit(
                            Resource.Error(
                                message = responseDto.resMessage ?: "Unknown error occurred"
                            )
                        )
                    }
                }
                is ApiResponse.Failure.Error -> {
                    emit(
                        Resource.Error(
                            message = response.envelopeMessage(),
                            code = response.statusCode.code
                        )
                    )
                }
                is ApiResponse.Failure.Exception -> {
                    emit(
                        Resource.Error(
                            message = response.message ?: "Unknown error occurred"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(
                Resource.Error(
                    message = e.message ?: "Network error occurred"
                )
            )
        }
    }.flowOn(Dispatchers.IO)


    /**
     * Handles ApiResponse for simple suspend functions (not Flow)
     */
    suspend inline fun <T, R> handleApiResponseSuspend(
        crossinline apiCall: suspend () -> ApiResponse<T>,
        crossinline mapper: (T) -> R
    ): Resource<R> {
        return try {
            when (val response = apiCall()) {
                is ApiResponse.Success -> {
                    Resource.Success(mapper(response.data))
                }
                is ApiResponse.Failure.Error -> {
                    Resource.Error(
                        message = response.envelopeMessage(),
                        code = response.statusCode.code
                    )
                }
                is ApiResponse.Failure.Exception -> {
                    Resource.Error(
                        message = response.message ?: "Unknown error occurred"
                    )
                }
            }
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "Network error occurred"
            )
        }
    }


    /**
     *  Handles ResponseDto for simple suspend functions (not Flow)
     */
    suspend inline fun <T> handleApiResponseSuspendWithMessage(
        crossinline apiCall: suspend () -> ApiResponse<ResponseDto<T>>
    ): Resource<T> {
        return try {
            when (val response = apiCall()) {
                is ApiResponse.Success -> {
                    val responseDto = response.data
                    if (responseDto.isOk && responseDto.info != null) {
                        Resource.Success(responseDto.info)
                    } else {
                        Resource.Error(
                            message = responseDto.resMessage ?: "Unknown error occurred"
                        )
                    }
                }
                is ApiResponse.Failure.Error -> {
                    Resource.Error(
                        message = response.envelopeMessage(),
                        code = response.statusCode.code
                    )
                }
                is ApiResponse.Failure.Exception -> {
                    Resource.Error(
                        message = response.message ?: "Unknown error occurred"
                    )
                }
            }
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "Network error occurred"
            )
        }
    }

    /**
     * Handles ApiResponse for simple suspend functions without mapping
     */
    suspend inline fun <T> handleApiResponseSuspend(
        crossinline apiCall: suspend () -> ApiResponse<T>
    ): Resource<T> {
        return try {
            when (val response = apiCall()) {
                is ApiResponse.Success -> {
                    Resource.Success(response.data)
                }
                is ApiResponse.Failure.Error -> {
                    Resource.Error(
                        message = response.envelopeMessage(),
                        code = response.statusCode.code
                    )
                }
                is ApiResponse.Failure.Exception -> {
                    Resource.Error(
                        message = response.message ?: "Unknown error occurred"
                    )
                }
            }
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "Network error occurred"
            )
        }
    }
}
