package com.ngapak.dev.javacell.feature.stock_report.domain

import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.Flow

interface IStockReportRepository {
    fun getStockReport(): Flow<Resource<List<Transaction>>>
}