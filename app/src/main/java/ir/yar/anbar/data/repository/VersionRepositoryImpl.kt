package ir.yar.anbar.data.repository

import ir.yar.anbar.data.remote.datasource.VersionRemoteDataSource
import ir.yar.anbar.data.remote.dto.response.AppVersionInfo
import ir.yar.anbar.data.remote.dto.response.UpdateStatus
import ir.yar.anbar.data.remote.dto.response.checkUpdateStatus
import ir.yar.anbar.data.remote.dto.response.toDomainModel

import ir.yar.anbar.data.remote.util.ApiResponseHandler
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

    override suspend fun checkForUpdates(
        currentVersionCode: Int
    ): Flow<Resource<Pair<AppVersionInfo, UpdateStatus>>> {
        return ApiResponseHandler.handleApiResponse(
            apiCall = { remoteDataSource.getAndroidVersion() },
            mapper = { dto ->
                val versionInfo = dto.toDomainModel()
                val updateStatus = versionInfo.checkUpdateStatus(currentVersionCode)
                Pair(versionInfo, updateStatus)
            }
        )
    }

    override suspend fun getVersionByPlatform(
        platform: String
    ): Flow<Resource<AppVersionInfo>> {
        return ApiResponseHandler.handleApiResponse(
            apiCall = { remoteDataSource.getVersionByPlatform(platform) },
            mapper = { it.toDomainModel() }
        )
    }

    override suspend fun getAllVersions(): Flow<Resource<List<AppVersionInfo>>> {
        return ApiResponseHandler.handleApiResponse(
            apiCall = { remoteDataSource.getAllVersions() },
            mapper = { list -> list.map { it.toDomainModel() } }
        )
    }
}