package com.example.loansandsavings

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigator(){
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home"){
        composable("home") { HomeScreen(navController) }
        composable("issue_loans") { LoansIssueScreen() }
        composable("view_loans") { DisplayLoansScreen() }
        composable("update_savings") { EnterSavingsScreen() }
        composable("view_savings") { DisplaySavingsScreen() }
    }
}