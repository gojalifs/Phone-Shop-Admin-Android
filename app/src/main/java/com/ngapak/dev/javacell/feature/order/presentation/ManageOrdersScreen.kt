package com.ngapak.dev.javacell.feature.order.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Paid
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.shared.component.Center
import com.ngapak.dev.javacell.utils.Converter.toWibDate
import com.ngapak.dev.javacell.utils.Converter.toWibTime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ManageOrdersScreen(
    orderViewModel: OrderViewModel,
    goToDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
//        if (orderViewModel.orders.value.data == null || orderViewModel.orders.value.data?.isEmpty() == true) {
        orderViewModel.getLatestOrders()
//        }
    }

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        orderViewModel.getLatestOrders()
        refreshing = false
    }

    val refreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = ::refresh)

    orderViewModel.orders.collectAsState().value.let { resource ->
        when (resource) {
            is Resource.Loading -> Center { CircularProgressIndicator() }
            is Resource.Success -> {
                Box(modifier.pullRefresh(refreshState)) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxSize(),
                    ) {
                        if (resource.data?.isEmpty() == true) {
                            item { Center { Text(text = "There is no new orders :(") } }
                        } else {
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
                                            Text(
                                                text = "${it.product?.name}",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                            Text(text = "Order #${it.id}")
                                            Text(
                                                text = "On ${it.createdAt?.toWibDate()} ${it.createdAt?.toWibTime()}",
                                                color = Color.Gray,
                                            )
                                        }
                                        Icon(Icons.Rounded.Paid, contentDescription = "Paid")
                                    }
                                }
                            }
                        }
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
                    Text(text = "${resource.message}")
                }
            }
        }
    }
}

