package ir.yar.anbar.domain.usecase.userpreferences

import ir.yar.anbar.domain.model.UnitOfMeasure
import ir.yar.anbar.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SaveVisibleUnitsUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(units: Set<String>) {
        // Unknown names are dropped rather than rejected so a stale stored
        // value can never poison the pickers — but at least one valid unit
        // must remain, or there would be nothing left to pick from
        val valid = units.mapNotNull { UnitOfMeasure.fromName(it)?.name }.toSet()
        require(valid.isNotEmpty()) { "At least one unit must stay visible" }
        repository.saveVisibleUnits(valid)
    }
}
