package com.ngapak.dev.javacell.feature.products.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ngapak.dev.javacell.feature.products.domain.usecase.ProductsUseCase

class ProductsViewModelFactory(private val productsUseCase: ProductsUseCase) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProductsViewModel::class.java) -> ProductsViewModel(
                productsUseCase
            ) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}