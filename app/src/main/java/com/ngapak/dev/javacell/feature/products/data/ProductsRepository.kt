package com.ngapak.dev.javacell.feature.products.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.products.domain.model.Product
import com.ngapak.dev.javacell.feature.products.domain.repository.IProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ProductsRepository(private val auth: FirebaseAuth, private val db: FirebaseFirestore) :
    IProductsRepository {
    override fun getAllProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())

        try {
            Log.d("TAG repository", "getAllProducts: getting products")
            if (auth.currentUser != null) {
                val productRef = db.collection("products")
                val products =
                    productRef.get().await().toObjects(Product::class.java).toMutableList()
                Log.d("TAG repo get", "getAllProducts: ${products.isEmpty()} ${products.size}")
                emit(Resource.Success(products))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error getting product"))
        }
    }

    override fun saveNewProduct(product: Product): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            db.collection("products").add(product).await()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error("Something error happened"))
            Log.d("TAG Repository", "saveNewProduct: ${e.message}")
        }
    }

    override fun updateProduct(product: Product): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val ref = db.collection("products").document("${product.id}")
            val data = product::class.java
            var update = ref.set(product, SetOptions.merge())

            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error("Something Error happened. ${e.message}"))
        }
    }
}