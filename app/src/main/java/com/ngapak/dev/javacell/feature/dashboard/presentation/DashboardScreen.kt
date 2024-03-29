package com.ngapak.dev.javacell.feature.dashboard.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.di.Injection
import com.ngapak.dev.javacell.shared.component.Center
import com.ngapak.dev.javacell.utils.Converter.toRupiah

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DashboardScreen(
    goToOrders: () -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(Injection.provideHomeUseCase())),
) {
    var refreshing by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (homeViewModel.orders.value.data.isNullOrEmpty()) {
            homeViewModel.getLatestOrders()
        }
    }

    val state = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            refreshing = true
            homeViewModel.getLatestOrders()
            refreshing = false
        })

    Box {
        LazyColumn(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .pullRefresh(state)
        ) {
            item {
                NewOrdersCard(
                    goToOrders = { goToOrders() },
                    homeViewModel = homeViewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                )
            }
        }
        PullRefreshIndicator(false, state, modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun NewOrdersCard(
    goToOrders: () -> Unit,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val orders = homeViewModel.orders.collectAsState().value.data
    Card(onClick = { goToOrders() }, modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (orders?.isEmpty() == true || orders == null) "No New Order Today" else "New Order Arrived...",
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            modifier = modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        )

        homeViewModel.orders.collectAsState().value.let { resource ->
            when (resource) {
                is Resource.Loading -> Center() { CircularProgressIndicator() }
                is Resource.Success -> {
                    resource.data?.map { order ->
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "${order.product?.name}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(text = "Receiver : ${order.address?.receiverName}")
                            Text(text = "${order.product?.price?.toRupiah()}")
                        }
                        HorizontalDivider(color = Color.White)
                    }
                }

                is Resource.Error -> Text(text = "${resource.message}")
            }
        }

        TextButton(
            onClick = { goToOrders() },
            modifier = modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Load More . . .")
        }
    }
}

