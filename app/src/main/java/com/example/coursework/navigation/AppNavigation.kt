package com.example.coursework.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.coursework.components.FoodCard
import com.example.coursework.data.FoodData
import com.example.coursework.screens.FoodScreen
import com.example.coursework.screens.Screen
import com.example.coursework.screens.HomeScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(
            route = Screen.Home.route
        ) {
            HomeScreen(navController)
        }
        composable(
            route = Screen.FoodList.route
        ) {
            FoodScreen(FoodData.foodsList, navController)
        }
    }
}