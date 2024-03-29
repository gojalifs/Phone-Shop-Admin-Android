package com.ngapak.dev.javacell.feature.order.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Paid
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngapak.dev.javacell.data.Resource

@Composable
fun OnDeliveryScreen(
    orderViewModel: OrderViewModel,
    goToDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        orderViewModel.getOnDelivery()
    }
    orderViewModel.orders.collectAsState().value.let { resource ->
        when (resource) {
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = modifier.padding(horizontal = 16.dp),
                ) {
                    items(resource.data ?: emptyList(), key = { it.id ?: "" }) {
                        Card(onClick = {
                            goToDetail()
                            orderViewModel.setOrder(it)
                        }) {
                            Row(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = modifier.weight(1f)) {
                                    Text(text = "${it.product?.name}", fontSize = 20.sp)
                                    Text(text = "Order #${it.id}")
                                    Text(text = "Status : ${it.deliveryStatus}", color = Color.Gray)
                                }
                                Icon(Icons.Rounded.Paid, contentDescription = "Paid")
                            }
                        }
                    }
                }
            }

            is Resource.Error -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "${resource.message}")
                }
            }
        }
    }
}

