package com.ngapak.dev.javacell.feature.splash.presentation

import androidx.lifecycle.ViewModel
import com.ngapak.dev.javacell.feature.splash.domain.usecase.SplashUseCase

class SplashViewModel(private val splashUseCase: SplashUseCase) : ViewModel() {
    val checkSession = splashUseCase.checkSession()
}