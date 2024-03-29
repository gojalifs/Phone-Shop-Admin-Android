package com.ngapak.dev.javacell.feature.splash.domain.usecase

import com.ngapak.dev.javacell.feature.splash.domain.repository.ISplashRepository

class SplashInteractor(private val splashRepository: ISplashRepository) : SplashUseCase {
    override fun checkSession(): Boolean {
        return splashRepository.checkSession()
    }
}