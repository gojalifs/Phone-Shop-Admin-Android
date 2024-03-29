package com.ngapak.dev.javacell.feature.order.domain

import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.Flow

class OrderInteractor(private val orderRepository: IOrderRepository) : OrderUseCase {
    override fun getLatestOrders(): Flow<Resource<List<Transaction>>> {
        return orderRepository.getLatestOrders()
    }

    override fun getOnDeliveryOrders(): Flow<Resource<List<Transaction>>> {
        return orderRepository.getOnDeliveryOrders()
    }

    override fun updateReceipt(id: String, receipt: String): Flow<Resource<Boolean>> {
        return orderRepository.updateReceipt(id, receipt)
    }

    override fun getCompletedOrders(): Flow<Resource<List<Transaction>>> {
        return orderRepository.getCompletedOrder()
    }
}