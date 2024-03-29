package com.ngapak.dev.javacell.feature.products.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.di.Injection
import com.ngapak.dev.javacell.feature.products.domain.model.Product
import com.ngapak.dev.javacell.utils.Converter.toRupiah
import com.ngapak.dev.javacell.utils.Utils

@Composable
fun ManageProductScreen(
    modifier: Modifier = Modifier,
    pViewModel: ProductsViewModel = viewModel(
        factory = ProductsViewModelFactory(Injection.provideProductsUseCase())
    )
) {
    LaunchedEffect(Unit) {
        pViewModel.getAllProducts()
    }

    pViewModel.products.collectAsState().value.let { resource ->
        when (resource) {
            is Resource.Loading -> {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            }

            is Resource.Success -> {
                ProductsGrid(products = resource.data, viewModel = pViewModel)
            }

            is Resource.Error -> {
                Utils.showToast(LocalContext.current, resource.message.toString())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsGrid(
    viewModel: ProductsViewModel,
    modifier: Modifier = Modifier,
    products: List<Product>? = emptyList(),
) {
    val showBottomSheet = viewModel.showBottomSheet.collectAsState().value

    if ((products != null && products.isEmpty()) || products.isNullOrEmpty()) {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No product available. Tap to try again or try to add new one",
                textAlign = TextAlign.Center,
                modifier = modifier.clickable { viewModel.getAllProducts() })
        }
    } else {
        if (showBottomSheet) {
            ModalBottomSheet(onDismissRequest = {
                viewModel.hideBottomSheet()
                viewModel.clearCurrentProduct()
            }) {
                CustomModalBottom(
                    product = viewModel.currentProduct.collectAsState().value,
                    modifier = modifier
                        .padding(
                            bottom = WindowInsets.navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding()
                        )
                        .fillMaxWidth(),
                    viewModel = viewModel,
                )
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(184.dp),
            modifier = modifier,
            contentPadding = PaddingValues(8.dp),

            ) {
            items(products, key = { it.id ?: "" }) { product ->
                ProductCard(product, onCardClick = {
                    viewModel.setCurrentProduct(product)
                    viewModel.setIsUpdate(true)
                    viewModel.showBottomSheet()
                })
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onCardClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = { onCardClick() },
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .height(250.dp),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(product.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = product.name,
            modifier = Modifier.weight(1f),
            contentScale = ContentScale.Crop,
        )
        Text(
            text = "${product.name}",
            fontSize = 24.sp,
            modifier = modifier.padding(horizontal = 8.dp)
        )
        Text(text = "${product.price?.toRupiah()}", modifier = modifier.padding(horizontal = 8.dp))
        Text(text = "Stock : ${product.stock} pcs", modifier = modifier.padding(horizontal = 8.dp))
    }
}

@Composable
fun CustomModalBottom(
    modifier: Modifier,
    viewModel: ProductsViewModel,
    product: Product? = null,
) {
    var id by remember { mutableStateOf(product?.id ?: "") }
    var name by remember { mutableStateOf(product?.name ?: "") }
    var price by remember { mutableStateOf(product?.price ?: 0) }
    var priceText by remember { mutableStateOf(product?.price?.toRupiah()) }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var imgUrl by remember { mutableStateOf(product?.imageUrl ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "") }
    var stock by remember { mutableStateOf(product?.stock ?: 0) }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Product Name . . .") },
        )
        TextField(
            value = price.toString(),
            placeholder = { Text(text = "Rp") },
            onValueChange = {
                price = it.toIntOrNull() ?: 0
                priceText = price.toRupiah()
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Product Price . . .") },
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Product Description . . .") },
        )
        TextField(
            value = imgUrl,
            onValueChange = { imgUrl = it },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            label = { Text(text = "Product Image . . .") },
        )
        TextField(
            value = "$stock",
            onValueChange = { stock = it.toIntOrNull() ?: 0 },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Stock . . .") },
        )
        Button(
            onClick = {
                if (viewModel.isUpdate.value) {
                    val updatedProduct = Product(
                        id = id,
                        name = name,
                        stock = stock,
                        description = description,
                        category = category,
                        imageUrl = imgUrl,
                        price = price
                    )

                    viewModel.updateProduct(updatedProduct)
                } else {
                    val newProduct = Product(
                        name = name,
                        stock = stock,
                        description = description,
                        category = category,
                        imageUrl = imgUrl,
                        price = price
                    )
                    viewModel.save(newProduct)
                }
            },
            modifier = modifier.fillMaxWidth(),
        ) {
            Text(text = "Save")
        }
    }
}
