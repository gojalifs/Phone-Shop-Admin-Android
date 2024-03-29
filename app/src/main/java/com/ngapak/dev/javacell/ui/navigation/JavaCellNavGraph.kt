package com.ngapak.dev.javacell.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ngapak.dev.javacell.MainScreen
import com.ngapak.dev.javacell.di.Injection
import com.ngapak.dev.javacell.feature.auth.presentation.LoginScreen
import com.ngapak.dev.javacell.feature.auth.presentation.RegisterScreen
import com.ngapak.dev.javacell.feature.order.presentation.OrderDetailScreen
import com.ngapak.dev.javacell.feature.order.presentation.OrderViewModel
import com.ngapak.dev.javacell.feature.order.presentation.OrderViewModelFactory
import com.ngapak.dev.javacell.feature.splash.presentation.SplashScreen

@Composable
fun JavaCellNavGraph(navHostController: NavHostController) {
    val orderViewModel: OrderViewModel =
        viewModel(factory = OrderViewModelFactory(Injection.provideOrderUseCase()))

    NavHost(
        navController = navHostController,
        startDestination = JavaCellNavigation.SPLASH_ROUTE,
    ) {
        composable(JavaCellNavigation.SPLASH_ROUTE) {
            SplashScreen(navHostController = navHostController)
        }
        composable(JavaCellNavigation.LOGIN_ROUTE) {
            LoginScreen(
                navigateToRegister = {
                    navHostController.navigate(JavaCellNavigation.REGISTER_ROUTE)
                },
                navigateToHome = {
                    navHostController.navigate(JavaCellNavigation.HOME_ROUTE) {
                        popUpTo(JavaCellNavigation.LOGIN_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(JavaCellNavigation.REGISTER_ROUTE) {
            RegisterScreen()
        }

        composable(JavaCellNavigation.HOME_ROUTE) {
            MainScreen(
                orderViewModel = orderViewModel,
                goToDetail = { navHostController.navigate(JavaCellNavigation.ORDER_DETAIL_ROUTE) }
            )
        }

        composable(JavaCellNavigation.ORDER_DETAIL_ROUTE) {
            OrderDetailScreen(
                orderViewModel = orderViewModel,
                navigateUp = { navHostController.navigateUp() })
        }

//        composable(JavaCellNavigation.REPORTING_ROUTE) {
//            SalesReportScreen()
//        }

    }
}
