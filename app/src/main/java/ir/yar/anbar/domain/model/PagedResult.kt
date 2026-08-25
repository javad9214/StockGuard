package ir.yar.anbar.domain.model

/**
 * One page of a paginated result. Domain-side counterpart of the paged
 * API/DAO responses, so upper layers never see transport types.
 */
data class PagedResult<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)
