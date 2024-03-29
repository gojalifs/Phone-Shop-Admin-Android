package com.ngapak.dev.javacell.feature.products.domain.usecase

import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.products.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductsUseCase {
    fun getAllProducts(): Flow<Resource<List<Product>>>
    fun saveNewProduct(product: Product): Flow<Resource<Boolean>>
    fun updateProduct(product: Product): Flow<Resource<Boolean>>
}