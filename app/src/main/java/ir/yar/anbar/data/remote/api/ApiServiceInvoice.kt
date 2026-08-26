package ir.yar.anbar.data.remote.api

import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.remote.dto.request.InvoiceSyncRequestDto
import ir.yar.anbar.data.remote.dto.response.ApiResponseDto
import ir.yar.anbar.data.remote.dto.response.InvoicePullResponseDto
import ir.yar.anbar.data.remote.dto.response.SyncedInvoiceDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServiceInvoice {

    /**
     * Push sync: upload a batch of local invoices (new, changed or deleted).
     * The server upserts by (userId, localId); the response maps each pushed
     * localId to its serverId so the device can mark rows synced.
     */
    @POST("api/invoices/sync")
    suspend fun pushInvoices(
        @Body invoices: List<InvoiceSyncRequestDto>
    ): ApiResponse<ApiResponseDto<List<SyncedInvoiceDto>>>

    /**
     * Pull sync: invoices changed on the server since the cursor, including
     * soft-deleted ones (device mirrors deletions). since is the serverTime
     * from the previous pull, 0 for a full first sync.
     */
    @GET("api/invoices/sync")
    suspend fun pullInvoices(
        @Query("since") since: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): ApiResponse<ApiResponseDto<InvoicePullResponseDto>>
}