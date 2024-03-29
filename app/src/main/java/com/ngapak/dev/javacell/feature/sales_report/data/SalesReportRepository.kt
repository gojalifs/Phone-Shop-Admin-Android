package com.ngapak.dev.javacell.feature.sales_report.data

import com.google.firebase.FirebaseException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.sales_report.domain.ISalesReportRepository
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date

class SalesReportRepository(private val auth: FirebaseAuth, private val db: FirebaseFirestore) :
    ISalesReportRepository {
    override fun makeReport(startDate: Date, endDate: Date): Flow<Resource<List<Transaction>>> =
        flow {
            emit(Resource.Loading())

            if (auth.currentUser != null) {
                try {
                    val a = Timestamp(endDate).toDate()
                    if (auth.currentUser != null) {
                        val ref = db.collectionGroup("transactions")
                            .whereEqualTo("deliveryStatus", "shipped")
                            .whereEqualTo("paymentStatus", "paid")
                            .whereGreaterThanOrEqualTo("updatedAt", Timestamp(startDate))
                            .whereLessThanOrEqualTo("updatedAt", Timestamp(endDate))
                            .orderBy("updatedAt", Query.Direction.DESCENDING)

                        val transactions = ref.get().await().toObjects(Transaction::class.java)

                        emit(Resource.Success(transactions))
                    }
                } catch (e: FirebaseException) {
                    emit(Resource.Error("Failed update receipt. Code ${e.message}"))
                } catch (e: Exception) {
                    emit(Resource.Error("Failed update receipt. Code ${e.message}"))
                }
            }
        }

}