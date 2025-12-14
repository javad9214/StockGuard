package ir.yar.anbar.domain.model

import ir.yar.anbar.data.local.entity.SupplierEntity


// Domain Model
data class Supplier(
    val id: Long,
    val name: String,
    val phone: String?,
    val email: String?,
    val address: String?,
    val note: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean
)

// Mapping Extension Functions
fun SupplierEntity.toDomain(): Supplier {
    return Supplier(
        id = this.id,
        name = this.name,
        phone = this.phone,
        email = this.email,
        address = this.address,
        note = this.note,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isDeleted = this.isDeleted
    )
}

fun Supplier.toEntity(): SupplierEntity {
    return SupplierEntity(
        id = this.id,
        name = this.name,
        phone = this.phone,
        email = this.email,
        address = this.address,
        note = this.note,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isDeleted = this.isDeleted
    )
}

// List mapping extensions
fun List<SupplierEntity>.toDomain(): List<Supplier> {
    return this.map { it.toDomain() }
}

fun List<Supplier>.toEntity(): List<SupplierEntity> {
    return this.map { it.toEntity() }
}