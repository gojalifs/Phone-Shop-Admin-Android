package com.ngapak.dev.javacell.feature.stock_report.presentation

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

class StockReportViewModel(private val stockReportUseCase: StockReportUseCase) : ViewModel() {
    private val _stockReport = MutableStateFlow<Resource<List<Transaction>>>(
        Resource.Success(emptyList())
    )

    val stockReport: StateFlow<Resource<List<Transaction>>> get() = _stockReport

    fun getStockReport() {
        val calls = stockReportUseCase.getStockReport()
        viewModelScope.launch {
            calls.catch {
                this.emit(Resource.Error("${it.message}"))
            }.collectLatest {
                val data = it.data
                _stockReport.value = it
                if (data != null) {
                    _stockReport.value = Resource.Success(data)
                    Log.d("TAG VM", "makeReport: vm ${it.data}")
                } else {
                    _stockReport.value = Resource.Loading()
                }
            }
        }
    }
}