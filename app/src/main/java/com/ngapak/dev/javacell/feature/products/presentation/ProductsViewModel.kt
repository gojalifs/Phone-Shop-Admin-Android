package com.ngapak.dev.javacell.feature.products.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.products.domain.model.Product
import com.ngapak.dev.javacell.feature.products.domain.usecase.ProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductsViewModel(private val productsUseCase: ProductsUseCase) : ViewModel() {
    private var _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> get() = _showBottomSheet

    private var _products = MutableStateFlow<Resource<MutableList<Product>>>(
        Resource.Success(arrayListOf())
    )
    val products: StateFlow<Resource<MutableList<Product>>> get() = _products

    private var _currentProduct = MutableStateFlow<Product?>(null)
    val currentProduct: StateFlow<Product?> get() = _currentProduct

    private var _addProductState = MutableStateFlow(true)
    val addProductState: StateFlow<Boolean> get() = _addProductState

    private val _isUpdate = MutableStateFlow(false)
    val isUpdate: StateFlow<Boolean> get() = _isUpdate

    fun setIsUpdate(isUpdate: Boolean) {
        _isUpdate.value = isUpdate
    }

    fun getAllProducts() {
        val productList = productsUseCase.getAllProducts()
        viewModelScope.launch {
            productList.catch {
                this.emit(Resource.Error("Something error happened"))
            }.collectLatest { resource ->
                val result = resource.data?.toMutableList()
                if (result != null) {
                    _products.value = Resource.Success(result)
                } else {
                    _products.value = Resource.Loading()
                }
            }
        }
    }

    fun setCurrentProduct(product: Product) {
        _currentProduct.value = product
    }

    fun clearCurrentProduct() {
        _currentProduct.value = null
    }

    fun showBottomSheet() {
        _showBottomSheet.value = true
    }

    fun hideBottomSheet() {
        _showBottomSheet.value = false
    }

    fun save(product: Product) {
        viewModelScope.launch {
            productsUseCase.saveNewProduct(product)
                .catch {
                    this.emit(Resource.Error("Something Error Happened"))
                }
                .collectLatest {
                    if (it.data == true) {
                        _products.value.data?.add(product)
                        hideBottomSheet()
                    }
                }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            productsUseCase.updateProduct(product)
                .catch {
                    this.emit(Resource.Error("Something Error Happened"))
                }
                .collectLatest {
                    if (it.data == true) {
                        setIsUpdate(false)
                        hideBottomSheet()
                        getAllProducts()
                    }
                }
        }
    }
}