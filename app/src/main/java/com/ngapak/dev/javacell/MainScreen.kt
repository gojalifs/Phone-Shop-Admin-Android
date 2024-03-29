package com.ngapak.dev.javacell

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ngapak.dev.javacell.di.Injection
import com.ngapak.dev.javacell.feature.billable.presentation.BillingReportScreen
import com.ngapak.dev.javacell.feature.dashboard.presentation.DashboardScreen
import com.ngapak.dev.javacell.feature.dashboard.presentation.HomeViewModel
import com.ngapak.dev.javacell.feature.dashboard.presentation.HomeViewModelFactory
import com.ngapak.dev.javacell.feature.order.presentation.CompletedOrdersScreen
import com.ngapak.dev.javacell.feature.order.presentation.ManageOrdersScreen
import com.ngapak.dev.javacell.feature.order.presentation.OnDeliveryScreen
import com.ngapak.dev.javacell.feature.order.presentation.OrderViewModel
import com.ngapak.dev.javacell.feature.products.presentation.ManageProductScreen
import com.ngapak.dev.javacell.feature.products.presentation.ProductsViewModel
import com.ngapak.dev.javacell.feature.products.presentation.ProductsViewModelFactory
import com.ngapak.dev.javacell.feature.sales_report.presentation.SalesReportScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    goToDetail: () -> Unit,
    orderViewModel: OrderViewModel,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(Injection.provideHomeUseCase())),
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    BackHandler(homeViewModel.position.collectAsState().value != 0) {
        Log.d("(TAG", "MainScreen: handled")
        homeViewModel.setPosition(0)
    }

    BackHandler(drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    Scaffold(
        topBar = {
            homeViewModel.position.collectAsState().value.let {
                when (it) {
                    1 -> CustomTopBar(
                        drawerState = drawerState,
                        scope = scope,
                        title = "Product List",
                    )

                    2 -> CustomTopBar(drawerState = drawerState, scope = scope, title = "New Order")

                    3 -> CustomTopBar(
                        drawerState = drawerState,
                        scope = scope,
                        title = "on Delivery Order"
                    )

                    else -> CustomTopBar(drawerState = drawerState, scope = scope)
                }
            }
        },

        floatingActionButton = {
            if (homeViewModel.position.collectAsState().value == 1) {
                ProductFab()
            }
        },

        ) { paddingValues ->
        ModalNavigationDrawer(
            drawerState = drawerState,
            modifier = modifier.padding(paddingValues),
            drawerContent = {
                DrawerContent(scope, drawerState, homeViewModel, modifier)
            }
        ) {
            homeViewModel.position.collectAsState().value.let {
                when (it) {
                    0 -> {
                        DashboardScreen(
                            goToOrders = { homeViewModel.setPosition(2) },
                            homeViewModel = homeViewModel,
                        )
                    }

                    1 -> ManageProductScreen()
                    2 -> ManageOrdersScreen(orderViewModel = orderViewModel, { goToDetail() })
                    3 -> OnDeliveryScreen(orderViewModel, { goToDetail() })
                    4 -> CompletedOrdersScreen(
                        orderViewModel = orderViewModel,
                        goToDetail = { goToDetail() },
                    )

                    5 -> SalesReportScreen(
                        goToDetail = { goToDetail() },
                        orderDetailViewModel = orderViewModel,
                    )

                    6 -> BillingReportScreen(
                        goToDetail = { goToDetail() },
                        orderDetailViewModel = orderViewModel,
                    )

//                    7 -> StockReportScreen(
//                        goToDetail = { goToDetail() },
//                        orderDetailViewModel = orderViewModel,
//                    )

                    else ->
                        Text(text = "Screen not yet implemented")
                }
            }
        }
    }
}

@Composable
fun ProductFab(
    productViewModel: ProductsViewModel = viewModel(
        factory = ProductsViewModelFactory(Injection.provideProductsUseCase())
    )
) {
    ExtendedFloatingActionButton(onClick = {
        productViewModel.clearCurrentProduct()
        productViewModel.showBottomSheet()
    }) {
        Icon(Icons.Rounded.Add, contentDescription = null)
        Text(text = "Add New product")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: String = "Java Electronic Admin",
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        if (drawerState.isClosed) {
                            Log.d("TAG", "HomeScreen: TAPPED")
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Navigation Drawer",
                )
            }
        },
    )
}

@Composable
fun DrawerContent(
    scope: CoroutineScope,
    state: DrawerState,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {

//    var selectedItem by remember { mutableStateOf(0) }
    var selectedItem = homeViewModel.position.collectAsState().value
    val scrollState = rememberScrollState()

    ModalDrawerSheet(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(scrollState)
    ) {
        homeViewModel.items.mapIndexed { index, menuItem ->
            NavigationDrawerItem(
                label = { Text(text = menuItem.title) },
                selected = index == selectedItem,
                icon = { Icon(menuItem.icon, contentDescription = null) },
                onClick = {
                    selectedItem = index
                    scope.launch {
                        state.close()
                        homeViewModel.setPosition(index)
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

