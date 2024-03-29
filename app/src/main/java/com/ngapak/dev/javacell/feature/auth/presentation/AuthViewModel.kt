package com.ngapak.dev.javacell.feature.auth.presentation

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.feature.auth.domain.usecase.AuthUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AuthViewModel(private val authUseCase: AuthUseCase) : ViewModel() {
    private var _isLoading = MutableStateFlow(false)
    val isLoading: MutableStateFlow<Boolean> get() = _isLoading

    private var _isInputValid = MutableStateFlow(false)
    val isInputValid = _isInputValid

    fun login(email: String, password: String): Flow<Resource<FirebaseUser?>> {
        _isLoading.value = true
        val result = authUseCase.signInWithPassword(email, password)

        viewModelScope.launch {
            result
                .catch {
                    _isLoading.value = false
                }
                .collect {
                    Log.d("TAG ViewModel", "login: ${it.data?.email}")
                    _isLoading.value = false
                }
        }
        return result
    }

    fun signUpWithPassword(email: String, password: String): Flow<Resource<Boolean>> {
        val result = authUseCase.signUpWithPassword(email, password)
        viewModelScope.launch {
            result.catch {
                _isLoading.value = false
            }
                .collect {
                    Log.d("TAG ViewModel", "login: true")
                    _isLoading.value = false
                }
        }
        return result
    }

    fun signOut(): Flow<Resource<Boolean>> {
        Log.d("TAG", "signOut: viewmodel")
        val result = authUseCase.signOut()

        viewModelScope.launch {
            result.collect {
                Log.d("TAG", "signOut: ${it.data}")
            }
        }
        Log.d("TAG", "signOut: after success")

        return result
    }

    fun isEmailValid(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isNameValid(name: String): Boolean {
        return name.isNotEmpty()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }

    fun isPhoneValid(phone: String): Boolean {
        return phone.isNotEmpty()
    }

    fun checkInput(name: String, email: String, password: String, phone: String): Boolean {
        val result =
            isEmailValid(email) && isNameValid(name) && isPasswordValid(password) && isPhoneValid(
                phone
            )
        _isInputValid.value = result
        return result
    }
}