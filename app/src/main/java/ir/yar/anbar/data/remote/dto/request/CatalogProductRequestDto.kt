package ir.yar.anbar.data.remote.dto.request

/**
 * Admin create/update body for POST/PUT /api/admin/catalog/products. The
 * server binds this JSON straight onto its CatalogProduct entity, so:
 * - subcategory must be a nested {"id": ...} reference (the entity has a
 *   ManyToOne object field, not a subcategoryId column),
 * - the NOT NULL columns without defaults (imageSource, externalSource,
 *   externalSourceId) must be sent explicitly. externalSourceId is unique
 *   per externalSource, so repeated manual creates must not reuse a value.
 * Fields the server sets itself (status, isActive, qualityScore,
 * adoptionCount, normalizedName) are omitted.
 */
data class CatalogProductRequestDto(
    val name: String,
    val barcode: String? = null,
    val description: String? = null,
    val brand: String? = null,
    val manufacturer: String? = null,
    val subcategory: SubcategoryRef,
    val imageUrl: String? = null,
    val imageSource: String,
    val suggestedPrice: Long? = null,
    val unit: String? = null, // enum name only, e.g. KILOGRAM; anything else is rejected with 400
    val tags: String? = null,
    val externalSource: String,
    val externalSourceId: Long
) {
    data class SubcategoryRef(val id: Long)
}
