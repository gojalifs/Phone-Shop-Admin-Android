package com.ngapak.dev.javacell.feature.billable.domain

import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.Flow

interface BillingUseCase {
    fun getBillingReport(): Flow<Resource<List<Transaction>>>
}