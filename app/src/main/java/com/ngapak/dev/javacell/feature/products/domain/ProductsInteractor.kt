package com.ngapak.dev.javacell.feature.products.domain

import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.products.data.ProductsRepository
import com.ngapak.dev.javacell.feature.products.domain.model.Product
import com.ngapak.dev.javacell.feature.products.domain.usecase.ProductsUseCase
import kotlinx.coroutines.flow.Flow

class ProductsInteractor(private val productsRepository: ProductsRepository) : ProductsUseCase {
    override fun getAllProducts(): Flow<Resource<List<Product>>> {
        return productsRepository.getAllProducts()
    }

    override fun saveNewProduct(product: Product): Flow<Resource<Boolean>> {
        return productsRepository.saveNewProduct(product)
    }

    override fun updateProduct(product: Product): Flow<Resource<Boolean>> {
        return productsRepository.updateProduct(product)
    }
}