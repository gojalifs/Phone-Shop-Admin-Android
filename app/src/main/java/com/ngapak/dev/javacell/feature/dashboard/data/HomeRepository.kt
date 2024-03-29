package com.ngapak.dev.javacell.feature.dashboard.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.dashboard.domain.IHomeRepository
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class HomeRepository(private val auth: FirebaseAuth, private val db: FirebaseFirestore) :
    IHomeRepository {
    override fun getLatestOrders(): Flow<Resource<List<Transaction>>> = flow {
        emit(Resource.Loading())

        try {
            if (auth.currentUser != null) {
                val ref = db.collectionGroup("transactions")
                    .whereEqualTo("deliveryStatus", "packing")
                    .whereEqualTo("paymentStatus", "paid")
                    .limit(3L)

                val snapshots = ref.get().await().toObjects(Transaction::class.java)

                emit(Resource.Success(snapshots))
            }
        } catch (e: Exception) {
            Log.e("TAG Repo", "getNewOrders: ${e.message} ${e.printStackTrace()}")
            emit(Resource.Error(e.message.toString()))
        }
    }
}
