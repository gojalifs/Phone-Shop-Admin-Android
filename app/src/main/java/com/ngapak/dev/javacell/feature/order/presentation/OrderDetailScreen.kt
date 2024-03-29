package com.ngapak.dev.javacell.feature.order.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.utils.Converter.toRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun OrderDetailScreen(
    orderViewModel: OrderViewModel,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showReceiptModal by remember { mutableStateOf(false) }

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    var itemCount by remember { mutableStateOf(15) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1500)
        itemCount += 5
        refreshing = false
    }

    val refreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = { refresh() })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "${orderViewModel.transaction.collectAsState().value.data?.product?.name}") },
                navigationIcon = {
                    IconButton(onClick = { navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Navigate Up"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            orderViewModel.transaction.collectAsState().value.let {
                when (it) {
                    is Resource.Success -> {
                        if (it.data?.receiptNumber == null) {
                            FloatingActionButton(
                                onClick = {
                                    showReceiptModal = true
                                }) {
                                Text(text = "Add receipt", modifier = Modifier.padding(16.dp, 8.dp))
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    ) { paddingValues ->
        orderViewModel.orders.collectAsState().value.let {
            when (it) {
                is Resource.Loading -> CircularProgressIndicator()
                is Resource.Success -> {
                    Box(
                        modifier
                            .padding(paddingValues)
                            .pullRefresh(refreshState)
                    ) {
                        LazyColumn(
                            modifier = modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (showReceiptModal) {
                                item {
                                    ReceiptModalBottom(
                                        hideModal = { showReceiptModal = false },
                                        orderViewModel = orderViewModel,
                                        modifier = modifier,
                                    )
                                }
                            }
                            item { AddressCard(orderViewModel, modifier = modifier) }
                            item { OrderDetail(modifier = modifier.fillMaxWidth(), orderViewModel) }
                            item { DeliveryCard(orderViewModel = orderViewModel) }
                        }

                        PullRefreshIndicator(
                            refreshing,
                            refreshState,
                            Modifier.align(Alignment.TopCenter)
                        )
                    }
                }

                is Resource.Error -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = "${it.message}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptModalBottom(
    hideModal: () -> Unit,
    orderViewModel: OrderViewModel,
    modifier: Modifier = Modifier
) {
    var receiptNumber by remember { mutableStateOf("") }
    val resource = orderViewModel.transaction.collectAsState().value

    orderViewModel.transaction.collectAsState().value.let {
        if (it.data?.receiptNumber != null && it.data.receiptNumber.isNotEmpty()) {
            hideModal()
        }
    }

    ModalBottomSheet(onDismissRequest = { hideModal() }) {
        Column(
            modifier = Modifier
                .padding(
                    bottom = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Add Receipt Number")
            TextField(
                value = receiptNumber,
                onValueChange = { receiptNumber = it },
                modifier = modifier.fillMaxWidth()
            )
            Button(onClick = {
                orderViewModel.updateReceipt(receiptNumber)
            }) {
                resource.let {
                    when (it) {
                        is Resource.Loading -> {
                            CircularProgressIndicator()
                        }

                        else -> {
                            Text(text = "Submit")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddressCard(orderViewModel: OrderViewModel, modifier: Modifier = Modifier) {
    Card(
        onClick = {}, modifier = modifier.fillMaxWidth()
    ) {
        orderViewModel.transaction.collectAsState().value.let { resource ->
            Column(modifier = modifier.padding(8.dp)) {
                Text(text = "Shipping Address")
                HorizontalDivider(modifier.padding(vertical = 4.dp))
                Text(
                    text = "${resource.data?.address?.receiverName}",
                    fontSize = 20.sp,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                Text(text = "${resource.data?.address?.phone}")
                Text(
                    text = "${resource.data?.address?.addressDetail}, ${resource.data?.address?.address}",
                    color = Color.Gray
                )
                Text(text = "${resource.data?.address?.note}", color = Color.Gray)
            }
        }
    }
}


@Composable
fun OrderDetail(modifier: Modifier = Modifier, viewModel: OrderViewModel) {
    val order = viewModel.transaction.collectAsState().value
    Card {
        Column(
            modifier = modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row {
                Text(
                    text = "${order.data?.product?.name}",
                    fontSize = 20.sp,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = modifier.weight(1f),
                )
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("${order.data?.product?.imageUrl}")
                        .crossfade(true).build(),
                    contentDescription = "Product",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(
                            shape = RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 8.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp
                            )
                        )
                        .size(48.dp),
                )
            }
            Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Quantity", color = Color.Gray)
                Text(text = "${order.data?.qty}")
            }
            Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Sub Total", color = Color.Gray)
                Text(text = "${order.data?.totalPrice?.toRupiah()}")
            }
            Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Delivery Fee", color = Color.Gray)
                Text(text = 0.toRupiah())
            }
            Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Grand Total", color = Color.Gray)
                Text(text = "${order.data?.totalPrice?.toRupiah()}")
            }
        }
    }
}

@Composable
fun DeliveryCard(orderViewModel: OrderViewModel, modifier: Modifier = Modifier) {
    Card {
        Column(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier.fillMaxWidth()
            ) {
                Text(text = "Delivery Status", modifier = modifier.weight(1f), color = Color.Gray)
                orderViewModel.transaction.collectAsState().value.let {
                    Text(text = "${it.data?.deliveryStatus}")
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier.fillMaxWidth()
            ) {
                Text(text = "Receipt Number", modifier = modifier.weight(1f), color = Color.Gray)
                orderViewModel.transaction.collectAsState().value.let {
                    if (it.data?.receiptNumber == null) {
                        Text(text = "Waiting for receipt input")
                    } else {
                        Text(text = "${it.data.receiptNumber}")
                    }
                }
            }
        }
    }
}
