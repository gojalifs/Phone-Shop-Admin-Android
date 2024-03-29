package com.ngapak.dev.javacell.feature.order.domain

import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.Flow

interface IOrderRepository {
    fun getLatestOrders(): Flow<Resource<List<Transaction>>>

    fun getOnDeliveryOrders(): Flow<Resource<List<Transaction>>>

    fun updateReceipt(id: String, receipt: String): Flow<Resource<Boolean>>

    fun getCompletedOrder(): Flow<Resource<List<Transaction>>>

}