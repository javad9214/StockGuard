package ir.yar.anbar.domain.usecase.userpreferences

import ir.yar.anbar.domain.model.UnitOfMeasure
import ir.yar.anbar.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SaveDefaultUnitUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(unit: String) {
        // Exact enum names only — anything else could never be parsed back
        // by UnitOfMeasure.fromName and would corrupt the preference
        require(UnitOfMeasure.fromName(unit) != null) { "Unknown unit: $unit" }
        repository.saveDefaultUnit(unit)
    }
}
