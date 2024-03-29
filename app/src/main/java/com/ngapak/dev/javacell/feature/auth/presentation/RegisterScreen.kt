package com.ngapak.dev.javacell.feature.auth.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.di.Injection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(Injection.provideAuthUseCase())
    )
) {
    var buttonContent by remember { mutableStateOf("REGISTER") }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp, 24.dp, 16.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Register Here. . .")
        TextField(
            value = name,
            onValueChange = {
                name = it
                authViewModel.checkInput(name, email, password, phone)
            },
            modifier.fillMaxWidth(),
            isError = authViewModel.isNameValid(name),
            label = { Text(text = "Fullname") },
        )
        TextField(value = email, onValueChange = {
            email = it
            authViewModel.checkInput(name, email, password, phone)
        },
            modifier.fillMaxWidth(),
            isError = authViewModel.isEmailValid(email),
            label = {
                Text(
                    text = "Email"
                )
            })
        TextField(
            value = password,
            onValueChange = {
                password = it
                authViewModel.checkInput(name, email, password, phone)
            },
            modifier.fillMaxWidth(),
            isError = authViewModel.isPasswordValid(password),
            label = {
                Text(
                    text = "Password"
                )
            })
        TextField(value = phone, onValueChange = {
            phone = it
            authViewModel.checkInput(name, email, password, phone)
        },
            modifier.fillMaxWidth(),
            isError = authViewModel.isPhoneValid(phone),
            label = {
                Text(
                    text = "Phone Number"
                )
            })
        Button(
            onClick = {
                buttonContent = ""
                if (authViewModel.checkInput(name, email, password, phone)) {
                    CoroutineScope(Dispatchers.IO).launch {
                        authViewModel.signUpWithPassword(email, password).collect { result ->
                            withContext(Dispatchers.Main) {
                                when (result) {
                                    is Resource.Loading -> {
                                        buttonContent = ""
                                    }

                                    is Resource.Success -> {
                                        Log.d("TAG", "RegisterScreen: Success register")
                                    }

                                    is Resource.Error -> {
                                        Log.d("TAG", "RegisterScreen: ${result.message}")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Log.d("TAG", "RegisterScreen: failed")
                }
            },
            modifier = modifier.align(Alignment.CenterHorizontally)
        ) {
            authViewModel.isLoading.collectAsState(null).value.let {
                if (it == true) {
                    CircularProgressIndicator()
                } else {
                    Text(text = buttonContent)
                }
            }
        }
    }
}
