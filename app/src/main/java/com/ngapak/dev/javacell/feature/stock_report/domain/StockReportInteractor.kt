package com.ngapak.dev.javacell.feature.billable.domain

import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.stock_report.domain.IStockReportRepository
import com.ngapak.dev.javacell.feature.stock_report.domain.StockReportUseCase
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.Flow

class StockReportInteractor(private val stockReportRepository: IStockReportRepository) :
    StockReportUseCase {
    override fun getStockReport(): Flow<Resource<List<Transaction>>> {
        return stockReportRepository.getStockReport()
    }
}