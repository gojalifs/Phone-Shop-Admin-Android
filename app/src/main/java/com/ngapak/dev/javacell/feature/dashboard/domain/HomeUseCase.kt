package com.ngapak.dev.javacell.feature.dashboard.domain

import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.Flow

interface HomeUseCase {
    fun getLatestOrders(): Flow<Resource<List<Transaction>>>
}