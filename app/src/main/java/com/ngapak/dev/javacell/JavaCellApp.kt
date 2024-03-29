package com.ngapak.dev.javacell

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ngapak.dev.javacell.ui.navigation.JavaCellNavGraph

@Composable
fun JavaCellApp(navHostController: NavHostController = rememberNavController()) {
    JavaCellNavGraph(navHostController)
}