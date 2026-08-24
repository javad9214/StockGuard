package ir.yar.anbar.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class CategoryWithSubcategoriesDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("icon")
    val icon: String? = null,

    @SerializedName("subcategories")
    val subcategories: List<SubcategoryDto> = emptyList()
)
