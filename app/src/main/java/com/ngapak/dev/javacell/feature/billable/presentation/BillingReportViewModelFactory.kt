package com.ngapak.dev.javacell.feature.billable.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ngapak.dev.javacell.feature.stock_report.domain.StockReportUseCase
import com.ngapak.dev.javacell.feature.stock_report.presentation.StockReportViewModel

@Suppress("UNCHECKED_CAST")
class BillingReportViewModelFactory(private val stockReportUseCase: StockReportUseCase) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StockReportViewModel::class.java) -> {
                StockReportViewModel(stockReportUseCase) as T
            }

            else -> throw IllegalArgumentException("unknown view model class: ${modelClass.name}")
        }
    }
}