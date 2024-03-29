package com.ngapak.dev.javacell.feature.sales_report.domain

import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.sales_report.data.SalesReportRepository
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.Date

class SalesReportInteractor(private val salesReportRepository: SalesReportRepository) :
    SalesReportUseCase {
    override fun makeReport(startDate: Date, endDate: Date): Flow<Resource<List<Transaction>>> {
        return salesReportRepository.makeReport(startDate, endDate)
    }
}