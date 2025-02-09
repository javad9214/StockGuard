package com.example.composetrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.example.composetrainer.data.local.entity.InvoiceProductCrossRef

@Dao
interface InvoiceProductDao {
    @Insert
    suspend fun insertCrossRef(crossRef: InvoiceProductCrossRef)

    @Delete
    suspend fun deleteCrossRef(crossRef: InvoiceProductCrossRef)
}