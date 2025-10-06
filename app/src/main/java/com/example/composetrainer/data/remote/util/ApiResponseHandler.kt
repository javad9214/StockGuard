package com.example.composetrainer.data.remote.util

import com.example.composetrainer.data.remote.dto.ApiResponseDto
import com.example.login.domain.util.Resource
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.retrofit.statusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


object ApiResponseHandler {

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
                            message = response.message(),
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
     *  Handles ApiResponseDto format with success/error fields
     */
    inline fun <T, R> handleApiResponseWithMessage(
        crossinline apiCall: suspend () -> ApiResponse<ApiResponseDto<T>>,
        crossinline mapper: (T) -> R
    ): Flow<Resource<R>> = flow {
        emit(Resource.Loading())

        try {
            when (val response = apiCall()) {
                is ApiResponse.Success -> {
                    val apiResponseDto = response.data
                    if (apiResponseDto.success && apiResponseDto.data != null) {
                        emit(Resource.Success(mapper(apiResponseDto.data)))
                    } else {
                        emit(
                            Resource.Error(
                                message = apiResponseDto.error ?: "Unknown error occurred"
                            )
                        )
                    }
                }
                is ApiResponse.Failure.Error -> {
                    emit(
                        Resource.Error(
                            message = response.message(),
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
                            message = response.message(),
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
     * Handles ApiResponseDto format with success/error fields without mapping
     */
    inline fun <T : Any> handleApiResponseWithMessage(
        crossinline apiCall: suspend () -> ApiResponse<ApiResponseDto<T>>
    ): Flow<Resource<T>> = flow {
        emit(Resource.Loading())

        try {
            when (val response = apiCall()) {
                is ApiResponse.Success -> {
                    val apiResponseDto = response.data
                    if (apiResponseDto.success && apiResponseDto.data != null) {
                        emit(Resource.Success(apiResponseDto.data))
                    } else {
                        emit(
                            Resource.Error(
                                message = apiResponseDto.error ?: "Unknown error occurred"
                            )
                        )
                    }
                }
                is ApiResponse.Failure.Error -> {
                    emit(
                        Resource.Error(
                            message = response.message(),
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
                        message = response.message(),
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
     *  Handles ApiResponseDto format with success/error fields for simple suspend functions (not Flow)
     */
    suspend inline fun <T> handleApiResponseSuspendWithMessage(
        crossinline apiCall: suspend () -> ApiResponse<ApiResponseDto<T>>
    ): Resource<T> {
        return try {
            when (val response = apiCall()) {
                is ApiResponse.Success -> {
                    val apiResponseDto = response.data
                    if (apiResponseDto.success && apiResponseDto.data != null) {
                        Resource.Success(apiResponseDto.data)
                    } else {
                        Resource.Error(
                            message = apiResponseDto.error ?: "Unknown error occurred"
                        )
                    }
                }
                is ApiResponse.Failure.Error -> {
                    Resource.Error(
                        message = response.message(),
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
                        message = response.message(),
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