package com.ngapak.dev.javacell.feature.sales_report.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.sales_report.domain.SalesReportUseCase
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

class SalesReportViewModel(private val salesReportUseCase: SalesReportUseCase) : ViewModel() {
    private val _report =
        MutableStateFlow<Resource<List<Transaction>>>(Resource.Success(emptyList()))
    val report get() = _report

    fun makeReport(startDate: Date, endDate: Date) {
        val calls = salesReportUseCase.makeReport(startDate, endDate)
        viewModelScope.launch {
            calls.catch {
                this.emit(Resource.Error("${it.message}"))
            }.collectLatest {
                val data = it.data
                _report.value = it
                if (data != null) {
                    _report.value = Resource.Success(data)
                    Log.d("TAG VM", "makeReport: vm ${it.data}")
                } else {
                    _report.value = Resource.Loading()
                }
            }
        }
    }
}