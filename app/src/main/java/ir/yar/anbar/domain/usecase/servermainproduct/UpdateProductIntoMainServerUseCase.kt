package ir.yar.anbar.domain.usecase.servermainproduct

import ir.yar.anbar.domain.repository.ServerMainProductRepository
import javax.inject.Inject

class UpdateProductIntoMainServerUseCase @Inject constructor(
    private val repository: ServerMainProductRepository
) {
}