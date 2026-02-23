package ir.yar.anbar.domain.usecase.userpreferences

import ir.yar.anbar.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStockRunoutLimitUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Int> = repository.stockRunoutLimit
}