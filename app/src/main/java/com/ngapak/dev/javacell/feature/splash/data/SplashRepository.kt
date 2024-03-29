package com.ngapak.dev.javacell.feature.splash.data

import com.google.firebase.auth.FirebaseAuth
import com.ngapak.dev.javacell.feature.splash.domain.repository.ISplashRepository

class SplashRepository(private val auth: FirebaseAuth) : ISplashRepository {
    override fun checkSession(): Boolean {
        return auth.currentUser != null
    }
}