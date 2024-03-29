package com.ngapak.dev.javacell.feature.order.data

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.order.domain.IOrderRepository
import com.ngapak.dev.javacell.shared.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class OrderRepository(private val auth: FirebaseAuth, private val db: FirebaseFirestore) :
    IOrderRepository {
    override fun getLatestOrders(): Flow<Resource<List<Transaction>>> = flow {
        emit(Resource.Loading())

        try {
            if (auth.currentUser != null) {
                val ref = db.collectionGroup("transactions")
                    .whereEqualTo("deliveryStatus", "packing")
                    .whereEqualTo("paymentStatus", "paid")
                    .orderBy("updatedAt", Query.Direction.DESCENDING)

                val snapshots = ref.get().await().toObjects(Transaction::class.java)

                emit(Resource.Success(snapshots))
            }
        } catch (e: Exception) {
            Log.e("TAG Repo", "getNewOrders: ${e.message} ${e.printStackTrace()}")
            emit(Resource.Error(e.message.toString()))
        }
    }

    override fun getOnDeliveryOrders(): Flow<Resource<List<Transaction>>> = flow {
        emit(Resource.Loading())

        try {
            if (auth.currentUser != null) {
                val ref = db.collectionGroup("transactions")
                    .whereEqualTo("paymentStatus", "paid")
                    .whereEqualTo("deliveryStatus", "delivering")

                val snapshots = ref.get().await().toObjects(Transaction::class.java)
                emit(Resource.Success(snapshots))
            }
        } catch (e: Exception) {
            Log.e("TAG Repo", "getNewOrders: ${e.message} ${e.printStackTrace()}")
            emit(Resource.Error(e.message.toString()))
        }
    }

    override fun getCompletedOrder(): Flow<Resource<List<Transaction>>> = flow {
        emit(Resource.Loading())

        try {
            if (auth.currentUser != null) {
                val ref = db.collectionGroup("transactions")
                    .whereEqualTo("deliveryStatus", "shipped")
                    .whereEqualTo("paymentStatus", "paid")

                val transactions = ref.get().await().toObjects(Transaction::class.java)

                emit(Resource.Success(transactions))
            }
        } catch (e: Exception) {
            Log.e("TAG Repo", "getNewOrders: ${e.message} ${e.printStackTrace()}")
            emit(Resource.Error(e.message.toString()))
        }
    }

    override fun updateReceipt(id: String, receipt: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        if (auth.currentUser != null) {
            try {
                val updateFields: Map<String, Any> = hashMapOf(
                    "receiptNumber" to receipt,
                    "deliveryStatus" to "delivering",
                    "updatedAt" to Timestamp.now(),
                )

                val ref = db
                    .collection("users").document(auth.currentUser!!.uid)
                    .collection("transactions").document(id)

                ref.update(updateFields).await()
                emit(Resource.Success(true))
            } catch (e: FirebaseException) {
                emit(Resource.Error("Failed update receipt. Code ${e.message}", false))
            } catch (e: Exception) {
                emit(Resource.Error("Failed update receipt. Code ${e.message}", false))
            }
        }
    }

}