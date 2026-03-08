package com.example.coursework.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.coursework.screens.DailyLogScreen
import com.example.coursework.screens.Screen
import com.example.coursework.screens.HomeScreen
import com.example.coursework.screens.ProfileScreen
import com.example.coursework.screens.FoodSearchScreen
import com.example.coursework.screens.SettingsScreen
import com.example.coursework.SettingsManager

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    settingsManager: SettingsManager
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
            DailyLogScreen(navController)
        }
        composable(
            route = Screen.Profile.route
        ) {
            ProfileScreen(navController)
        }
        composable(
            route = Screen.FoodSearch.route
        ) {
            FoodSearchScreen(navController)
        }
        composable(
            route = Screen.Settings.route
        ) {
            SettingsScreen(navController, settingsManager)
        }
    }
}