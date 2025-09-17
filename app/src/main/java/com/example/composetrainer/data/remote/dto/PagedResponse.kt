package com.example.composetrainer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PagedResponse<T>(
    @SerializedName("content")
    val content: List<T>,

    @SerializedName("page")
    val page: Int,

    @SerializedName("size")
    val size: Int,

    @SerializedName("totalElements")
    val totalElements: Long,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("last")
    val last: Boolean
)
