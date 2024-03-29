package com.ngapak.dev.javacell.feature.auth.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.ngapak.dev.javacell.data.Resource
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun signInWithPassword(email: String, password: String): Flow<Resource<FirebaseUser?>>
    fun signUpWithPassword(email: String, password: String): Flow<Resource<Boolean>>
    fun signOut(): Flow<Resource<Boolean>>
}