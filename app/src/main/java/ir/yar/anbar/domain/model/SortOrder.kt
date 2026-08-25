package ir.yar.anbar.domain.model

/**
 * Sort order for product listings. Lives in the domain layer so use cases
 * can accept it without importing UI types.
 */
enum class SortOrder {
    ASCENDING, // Oldest first
    DESCENDING // Newest first (default)
}
