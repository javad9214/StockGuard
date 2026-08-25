package ir.yar.anbar.data.repository

import ir.yar.anbar.data.remote.datasource.VersionRemoteDataSource
import ir.yar.anbar.data.remote.dto.response.toDomain
import ir.yar.anbar.data.remote.util.ApiResponseHandler
import ir.yar.anbar.domain.model.AppVersionInfo
import ir.yar.anbar.domain.model.UpdateStatus
import ir.yar.anbar.domain.model.checkUpdateStatus
import ir.yar.anbar.domain.repository.VersionRepository
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of VersionRepository
 */
class VersionRepositoryImpl @Inject constructor(
    private val remoteDataSource: VersionRemoteDataSource
) : VersionRepository {

    override fun checkForUpdates(
        currentVersionCode: Int
    ): Flow<Resource<Pair<AppVersionInfo, UpdateStatus>>> {
        return ApiResponseHandler.handleApiResponse(
            apiCall = { remoteDataSource.getAndroidVersion() },
            mapper = { dto ->
                val versionInfo = dto.toDomain()
                val updateStatus = versionInfo.checkUpdateStatus(currentVersionCode)
                Pair(versionInfo, updateStatus)
            }
        )
    }
}
