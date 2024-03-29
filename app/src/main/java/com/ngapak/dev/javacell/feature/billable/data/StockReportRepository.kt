package com.ngapak.dev.javacell.feature.billable.data

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.stock_report.domain.IStockReportRepository
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class StockReportRepository(private val auth: FirebaseAuth, private val db: FirebaseFirestore) :
    IStockReportRepository {
    override fun getStockReport(): Flow<Resource<List<Transaction>>> = flow {
        emit(Resource.Loading())

        if (auth.currentUser != null) {
            try {
                val task = db.collectionGroup("transactions")
                    .whereEqualTo("paymentStatus", "waiting")
                    .orderBy("createdAt", Query.Direction.ASCENDING)
                    .get()

                val result = task.await().toObjects(Transaction::class.java)

                emit(Resource.Success(result))
            } catch (e: FirebaseException) {
                emit(Resource.Error("Error getting data. Message : ${e.localizedMessage}"))
            } catch (e: Exception) {
                emit(Resource.Error("Error Happened. Try Again Later ${e.localizedMessage}"))
            }
        }
    }
}