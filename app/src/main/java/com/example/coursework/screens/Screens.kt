package com.example.coursework.screens

sealed class Screen(val route: String) {
    object Home: Screen(route = "home_screen")
    object FoodList: Screen(route = "foodList_screen")

    object Profile: Screen(route = "profile_screen")

    object FoodSearch: Screen(route = "food_search_screen")

    object Settings: Screen(route = "settings_screen")
}