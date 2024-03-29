package com.ngapak.dev.javacell.feature.billable.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.stock_report.domain.StockReportUseCase
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BillingViewModel(private val stockReportUseCase: StockReportUseCase) : ViewModel() {
    private val _billingReports = MutableStateFlow<Resource<List<Transaction>>>(
        Resource.Success(emptyList())
    )

    val billingReports: StateFlow<Resource<List<Transaction>>> get() = _billingReports

    fun getBillingReports() {
        val calls = stockReportUseCase.getStockReport()
        viewModelScope.launch {
            calls.catch {
                this.emit(Resource.Error("${it.message}"))
            }.collectLatest {
                val data = it.data
                _billingReports.value = it
                if (data != null) {
                    _billingReports.value = Resource.Success(data)
                    Log.d("TAG VM", "makeReport: vm ${it.data}")
                } else {
                    _billingReports.value = Resource.Loading()
                }
            }
        }
    }
}