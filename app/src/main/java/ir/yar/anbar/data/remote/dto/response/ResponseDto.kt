package ir.yar.anbar.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Envelope the server wraps every response in: {resCode, resMessage, info}.
 * resCode mirrors the server's ResponseCode.Code constants.
 */
data class ResponseDto<T>(
    @SerializedName("resCode")
    val resCode: Int,

    @SerializedName("resMessage")
    val resMessage: String? = null,

    @SerializedName("info")
    val info: T? = null
) {
    companion object {
        /** Server ResponseCode.Code.OK_OPERATION */
        const val CODE_OK = 1
    }

    val isOk: Boolean get() = resCode == CODE_OK
}
