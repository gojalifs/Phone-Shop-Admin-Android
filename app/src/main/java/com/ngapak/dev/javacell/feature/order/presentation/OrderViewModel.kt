package com.ngapak.dev.javacell.feature.order.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.order.domain.OrderUseCase
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrderViewModel(private val orderUseCase: OrderUseCase) : ViewModel() {

    private val _orders = MutableStateFlow<Resource<List<Transaction>>>(Resource.Loading())
    val orders: StateFlow<Resource<List<Transaction>>> get() = _orders

    private val _transaction = MutableStateFlow<Resource<Transaction>>(Resource.Loading())
    val transaction: StateFlow<Resource<Transaction>> get() = _transaction

    fun getLatestOrders() {
        val newOrders = orderUseCase.getLatestOrders()

        viewModelScope.launch {
            newOrders
                .catch {
                    this.emit(Resource.Error("Something error, ${it.message}"))
                }
                .collectLatest { resource ->
                    val data = resource.data?.toMutableList()
                    _orders.value = resource
                    if (data != null) {
                        _orders.value = Resource.Success(data)
                    }
                }
        }
    }

    fun getOnDelivery() {
        val onDelivery = orderUseCase.getOnDeliveryOrders()

        viewModelScope.launch {
            onDelivery
                .catch {
                    this.emit(Resource.Error("Something error, ${it.message}"))
                }
                .collectLatest { resource ->
                    val data = resource.data?.toMutableList()
                    _orders.value = resource
                    if (data != null) {
                        _orders.value = Resource.Success(data)
                    }
                }
        }
    }

    fun getCompletedOrders() {
        val completedOrders = orderUseCase.getCompletedOrders()

        viewModelScope.launch {
            completedOrders
                .catch {
                    this.emit(Resource.Error("Something error happened, Message ${it.message}"))
                }
                .collectLatest { resource ->
                    val data = resource.data?.toMutableList()
                    _orders.value = resource
                    if (data != null) {
                        _orders.value = Resource.Success(data)
                    }
                }
        }
    }

    fun setOrder(transaction: Transaction) {
        _transaction.value = Resource.Success(transaction)
    }

    fun updateReceipt(receipt: String) {
        if (_transaction.value.data?.id != null) {
            val update = orderUseCase.updateReceipt(_transaction.value.data?.id!!, receipt)

            viewModelScope.launch {
                update
                    .catch {
                        this.emit(Resource.Error("Something error, ${it.message}"))
                    }
                    .collectLatest {
                        if (it.data == true) {
                            val tr = _transaction.value.data!!.copy(
                                receiptNumber = receipt,
                                deliveryStatus = "delivering"
                            )

                            Log.d("TAG", "updateReceipt: entah ${it.message}")

                            _transaction.value = Resource.Success(tr)

                        }
                    }
            }
        } else {
            _transaction.value = Resource.Error("Failed Update Receipt")
        }
    }
}