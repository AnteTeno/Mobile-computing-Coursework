package com.example.coursework.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.coursework.R
import com.example.coursework.database.DatabaseProvider
import com.example.coursework.database.entities.FoodEntry
import java.io.File
import java.time.LocalDate

@Composable
fun DailyLogScreen(navController: NavController) {
    val context = LocalContext.current
    val db = DatabaseProvider.getDatabase(context)
    val foodEntryDao = db.foodEntryDao()
    val today = LocalDate.now().toString()

    var entries by remember { mutableStateOf<List<FoodEntry>>(emptyList()) }

    LaunchedEffect(Unit) {
        entries = foodEntryDao.getEntriesForDate(today)
    }

    val totalCalories = entries.sumOf { it.calories * it.grams / 100.0 }
    val totalProtein = entries.sumOf { it.protein * it.grams / 100.0 }
    val totalFat = entries.sumOf { it.fat * it.grams / 100.0 }
    val totalCarbs = entries.sumOf { it.carbs * it.grams / 100.0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(R.drawable.backicon),
                    contentDescription = "Back",
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = "Daily Log",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Today's Totals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NutrientText("Calories", "${totalCalories.toInt()} kcal")
                    NutrientText("Protein", "${String.format("%.1f", totalProtein)}g")
                    NutrientText("Fat", "${String.format("%.1f", totalFat)}g")
                    NutrientText("Carbs", "${String.format("%.1f", totalCarbs)}g")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (entries.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No foods logged today",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Use Search Foods to add entries",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries) { entry ->
                    FoodEntryCard(
                        entry = entry,
                        onDelete = {
                            foodEntryDao.delete(entry)
                            entries = foodEntryDao.getEntriesForDate(today)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FoodEntryCard(entry: FoodEntry, onDelete: () -> Unit) {
    val scaledCalories = entry.calories * entry.grams / 100.0
    val scaledProtein = entry.protein * entry.grams / 100.0
    val scaledFat = entry.fat * entry.grams / 100.0
    val scaledCarbs = entry.carbs * entry.grams / 100.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Show meal photo thumbnail if available
                if (!entry.imagePath.isNullOrBlank()) {
                    AsyncImage(
                        model = File(entry.imagePath),
                        contentDescription = "Meal photo",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${entry.grams.toInt()}g",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = onDelete) {
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutrientText("Cal", "${scaledCalories.toInt()}")
                NutrientText("Protein", "${String.format("%.1f", scaledProtein)}g")
                NutrientText("Fat", "${String.format("%.1f", scaledFat)}g")
                NutrientText("Carbs", "${String.format("%.1f", scaledCarbs)}g")
            }
        }
    }
}
