package ir.yar.anbar.domain.usecase.userpreferences


import ir.yar.anbar.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SaveStockRunoutLimitUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(limit: Int) {
        repository.saveStockRunoutLimit(limit)
    }
}