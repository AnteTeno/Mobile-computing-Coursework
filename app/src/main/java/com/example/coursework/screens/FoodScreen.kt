package com.example.coursework.screens

import android.media.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coursework.data.Food
import com.example.coursework.components.FoodCard
import com.example.coursework.data.FoodData
import com.example.coursework.ui.theme.CourseworkTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.coursework.R
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon

@Composable
fun FoodScreen(foods: List<Food>, navController: NavController) {
    Column{
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton( onClick = { navController.popBackStack() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.backicon),
                    contentDescription = "Back",
                    modifier = Modifier.size(22.dp)
                )
            }

        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(foods.sortedBy { it.name }) { food ->
                FoodCard(food)
            }
        }
    }


}


@Preview
@Composable
fun PreviewFoodScreen() {
    CourseworkTheme() {
        FoodScreen(FoodData.foodsList, navController = rememberNavController())
    }
}

