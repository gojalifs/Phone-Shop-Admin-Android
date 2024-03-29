package com.ngapak.dev.javacell.feature.auth.presentation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.di.Injection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(Injection.provideAuthUseCase())),
    context: Context = LocalContext.current
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isHide by remember { mutableStateOf(true) }
    var buttonContent by remember { mutableStateOf("LOGIN") }

    authViewModel.isLoading.collectAsState().value.let { result ->
        buttonContent = if (result) {
            ""
        } else {
            "LOGIN"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Please Login")
        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            label = { Text(text = "Email") }
        )
        TextField(value = password, onValueChange = { password = it },
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (isHide) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = { isHide = !isHide }) {
                    if (isHide) {
                        Icon(Icons.Rounded.Visibility, contentDescription = "Show Password")
                    } else {
                        Icon(Icons.Rounded.VisibilityOff, contentDescription = "Hide Password")
                    }
                }
            },
            label = { Text(text = "Password") }
        )
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    authViewModel.login(email, password).collect {
                        withContext(Dispatchers.Main) {
                            when (it) {
                                is Resource.Success -> navigateToHome()
                                is Resource.Loading -> Log.d("TAG", "LoginScreen: Loading")
                                is Resource.Error -> {
                                    Log.d("TAG", "LoginScreen: ${it.message}")
                                    Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                }
            },
            enabled = buttonContent.isNotEmpty(),
            modifier = modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            if (buttonContent.isEmpty()) {
                CircularProgressIndicator()
            } else {
                Text(text = buttonContent)
            }
        }
        Text(
            text = "or",
            modifier.padding(top = 16.dp)
        )
        OutlinedButton(
            onClick = { navigateToRegister() },
            modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "REGISTER")
        }
    }
}
