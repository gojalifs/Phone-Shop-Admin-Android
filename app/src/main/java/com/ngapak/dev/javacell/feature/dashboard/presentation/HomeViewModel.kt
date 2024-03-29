package com.ngapak.dev.javacell.feature.dashboard.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backpack
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PointOfSale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.dashboard.domain.HomeUseCase
import com.ngapak.dev.javacell.shared.model.Transaction
import com.ngapak.dev.javacell.ui.navigation.JavaCellNavigation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val homeUseCase: HomeUseCase) : ViewModel() {
    val items = listOf(
        MenuItem("Dashboard", Icons.Rounded.Home, JavaCellNavigation.DASHBOARD_ROUTE),
        MenuItem("Manage Products", Icons.Rounded.Backpack, JavaCellNavigation.ORDERS_ROUTE),
        MenuItem("Manage New Orders", Icons.Rounded.PointOfSale, JavaCellNavigation.ORDERS_ROUTE),
        MenuItem(
            "Track Order On Delivery",
            Icons.Rounded.PointOfSale,
            JavaCellNavigation.ORDERS_ROUTE
        ),
        MenuItem(
            "Track Completed Order",
            Icons.Rounded.PointOfSale,
            JavaCellNavigation.ORDERS_ROUTE
        ),
        MenuItem("Sales Report", Icons.Rounded.PointOfSale, JavaCellNavigation.ORDERS_ROUTE),
        MenuItem("Billing Report", Icons.Rounded.PointOfSale, JavaCellNavigation.ORDERS_ROUTE),
//        MenuItem("Stock Report", Icons.Rounded.PointOfSale, JavaCellNavigation.ORDERS_ROUTE),
    )

    private var _position = MutableStateFlow(0)
    val position: StateFlow<Int> get() = _position

    private val _orders = MutableStateFlow<Resource<List<Transaction>>>(Resource.Loading())
    val orders: StateFlow<Resource<List<Transaction>>> get() = _orders

    fun getLatestOrders() {
        val newOrders = homeUseCase.getLatestOrders()

        viewModelScope.launch {
            newOrders
                .catch {
                    this.emit(Resource.Error("Something error, ${it.message}"))
                }
                .collectLatest { resource ->
                    _orders.value = resource
                }
        }
    }

    fun setPosition(index: Int) {
        _position.value = index
    }
}