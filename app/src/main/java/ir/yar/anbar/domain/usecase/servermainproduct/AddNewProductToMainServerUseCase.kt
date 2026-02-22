package ir.yar.anbar.domain.usecase.servermainproduct

import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.repository.ServerMainProductRepository
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddNewProductToMainServerUseCase @Inject constructor(
    private val repository: ServerMainProductRepository
)