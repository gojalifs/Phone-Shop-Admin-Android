package com.ngapak.dev.javacell.feature.dashboard.domain

import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.Flow

class HomeInteractor(private val homeRepository: IHomeRepository) : HomeUseCase {
    override fun getLatestOrders(): Flow<Resource<List<Transaction>>> {
        return homeRepository.getLatestOrders()
    }
}