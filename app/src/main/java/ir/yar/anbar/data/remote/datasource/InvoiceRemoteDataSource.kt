package ir.yar.anbar.data.remote.datasource

import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.remote.api.ApiServiceInvoice
import ir.yar.anbar.data.remote.dto.request.InvoiceSyncRequestDto
import ir.yar.anbar.data.remote.dto.response.ApiResponseDto
import ir.yar.anbar.data.remote.dto.response.InvoicePullResponseDto
import ir.yar.anbar.data.remote.dto.response.SyncedInvoiceDto
import javax.inject.Inject

/**
 * Remote data source for invoice sync. Thin wrapper around [ApiServiceInvoice],
 * mirroring [UserProductRemoteDataSource].
 */
class InvoiceRemoteDataSource @Inject constructor(
    private val apiService: ApiServiceInvoice
) {

    /** Push a batch of local invoices; returns localId → serverId mappings */
    suspend fun pushInvoices(
        invoices: List<InvoiceSyncRequestDto>
    ): ApiResponse<ApiResponseDto<List<SyncedInvoiceDto>>> =
        apiService.pushInvoices(invoices)

    /** Pull invoices changed on the server since the cursor (paged) */
    suspend fun pullInvoices(
        since: Long,
        page: Int = 0,
        size: Int = 50
    ): ApiResponse<ApiResponseDto<InvoicePullResponseDto>> =
        apiService.pullInvoices(since, page, size)
}