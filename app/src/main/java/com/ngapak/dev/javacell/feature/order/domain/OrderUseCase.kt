package com.ngapak.dev.javacell.feature.order.domain

import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.Flow

interface OrderUseCase {
    fun getLatestOrders(): Flow<Resource<List<Transaction>>>

    fun getOnDeliveryOrders(): Flow<Resource<List<Transaction>>>

    fun updateReceipt(id: String, receipt: String): Flow<Resource<Boolean>>

    fun getCompletedOrders(): Flow<Resource<List<Transaction>>>
}