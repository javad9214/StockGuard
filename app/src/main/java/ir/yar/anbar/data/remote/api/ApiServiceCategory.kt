package ir.yar.anbar.data.remote.api

import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.remote.dto.response.ApiResponseDto
import ir.yar.anbar.data.remote.dto.response.CategoryWithSubcategoriesDto
import retrofit2.http.GET

interface ApiServiceCategory {

    @GET("api/categories")
    suspend fun getCategories(): ApiResponse<ApiResponseDto<List<CategoryWithSubcategoriesDto>>>
}
