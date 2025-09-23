package com.example.composetrainer.domain.usecase.servermainproduct

import com.example.composetrainer.domain.repository.ServerMainProductRepository
import javax.inject.Inject

class DeleteProductFromMainServerUseCase @Inject constructor(
    private val repository: ServerMainProductRepository
) {

}