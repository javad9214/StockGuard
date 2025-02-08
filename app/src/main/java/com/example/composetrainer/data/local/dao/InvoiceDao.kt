package com.example.composetrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.composetrainer.data.local.entity.InvoiceEntity

@Dao
interface InvoiceDao {

    @Insert
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

}