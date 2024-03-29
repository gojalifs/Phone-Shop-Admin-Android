package com.ngapak.dev.javacell.feature.splash.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ngapak.dev.javacell.di.Injection
import com.ngapak.dev.javacell.ui.navigation.JavaCellNavigation

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    initViewModel: SplashViewModel = viewModel(
        factory = SplashViewModelFactory(Injection.provideSplashUseCase())
    ),
    navHostController: NavHostController,
) {
    LaunchedEffect(Unit) {
        if (initViewModel.checkSession) {
            navHostController.navigate(JavaCellNavigation.HOME_ROUTE) {
                popUpTo(JavaCellNavigation.SPLASH_ROUTE) { inclusive = true }
            }
        } else {
            navHostController.navigate(JavaCellNavigation.LOGIN_ROUTE) {
                popUpTo(JavaCellNavigation.SPLASH_ROUTE) { inclusive = true }
            }
        }
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
    }
}