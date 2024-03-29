package com.ngapak.dev.javacell.feature.sales_report.domain

import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ISalesReportRepository {
    fun makeReport(startDate:Date, endDate:Date): Flow<Resource<List<Transaction>>>
}