package com.ngapak.dev.javacell.feature.products.domain.repository

import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.products.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface IProductsRepository {
    fun getAllProducts(): Flow<Resource<List<Product>>>
    fun saveNewProduct(product: Product): Flow<Resource<Boolean>>
    fun updateProduct(product: Product): Flow<Resource<Boolean>>
}