package com.ngapak.dev.javacell.feature.sales_report.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ngapak.dev.javacell.feature.sales_report.domain.SalesReportUseCase

@Suppress("UNCHECKED_CAST")
class SalesReportViewModelFactory(private val salesReportUseCase: SalesReportUseCase) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SalesReportViewModel::class.java) -> {
                SalesReportViewModel(salesReportUseCase) as T
            }

            else -> throw IllegalArgumentException("unknown view model class: ${modelClass.name}")
        }
    }
}