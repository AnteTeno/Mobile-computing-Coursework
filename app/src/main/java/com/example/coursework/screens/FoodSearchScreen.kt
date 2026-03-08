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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.coursework.R
import com.example.coursework.database.DatabaseProvider
import com.example.coursework.database.entities.FoodEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

data class SearchedFood(
    val name: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double
)

@Composable
fun FoodSearchScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<SearchedFood>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedFood by remember { mutableStateOf<SearchedFood?>(null) }
    var gramsInput by remember { mutableStateOf("100") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = DatabaseProvider.getDatabase(context)
    val foodEntryDao = db.foodEntryDao()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(R.drawable.backicon),
                    contentDescription = "Back",
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = "Search Foods",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Food name") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Button(
                onClick = {
                    if (query.isNotBlank()) {
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            try {
                                results = searchFoods(query)
                                if (results.isEmpty()) {
                                    errorMessage = "No results found"
                                }
                            } catch (e: Exception) {
                                errorMessage = "Search failed: ${e.message}"
                                results = emptyList()
                            }
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading
            ) {
                Text("Search")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Searching...")
                }
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(results) { food ->
                        FoodResultCard(food, onAdd = {
                            selectedFood = food
                            gramsInput = "100"
                        })
                    }
                }
            }
        }
    }

    selectedFood?.let { food ->
        AlertDialog(
            onDismissRequest = { selectedFood = null },
            title = { Text("Add ${food.name}") },
            text = {
                Column {
                    Text("Nutrition per 100g: ${food.calories.toInt()} kcal")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = gramsInput,
                        onValueChange = { gramsInput = it },
                        label = { Text("Grams") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val grams = gramsInput.toDoubleOrNull() ?: 100.0
                    foodEntryDao.insert(
                        FoodEntry(
                            name = food.name,
                            calories = food.calories,
                            protein = food.protein,
                            fat = food.fat,
                            carbs = food.carbs,
                            grams = grams,
                            date = java.time.LocalDate.now().toString()
                        )
                    )
                    selectedFood = null
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedFood = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FoodResultCard(food: SearchedFood, onAdd: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onAdd) {
                    Text("+ Add")
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Per 100g:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutrientText("Calories", "${food.calories.toInt()} kcal")
                NutrientText("Protein", "${food.protein}g")
                NutrientText("Fat", "${food.fat}g")
                NutrientText("Carbs", "${food.carbs}g")
            }
        }
    }
}

@Composable
fun NutrientText(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

suspend fun searchFoods(query: String): List<SearchedFood> = withContext(Dispatchers.IO) {
    val encoded = URLEncoder.encode(query, "UTF-8")
    val url = "https://api.nal.usda.gov/fdc/v1/foods/search" +
            "?api_key=DEMO_KEY&query=$encoded&pageSize=10" +
            "&dataType=Foundation,SR%20Legacy"
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.connectTimeout = 10000
    connection.readTimeout = 10000
    val response = connection.inputStream.bufferedReader().readText()
    connection.disconnect()
    val json = JSONObject(response)
    val foodsArray = json.getJSONArray("foods")

    val foods = mutableListOf<SearchedFood>()
    for (i in 0 until foodsArray.length()) {
        val food = foodsArray.getJSONObject(i)
        val name = food.optString("description", "").trim()
        if (name.isEmpty()) continue

        val nutrients = food.getJSONArray("foodNutrients")
        var calories = 0.0
        var protein = 0.0
        var fat = 0.0
        var carbs = 0.0

        for (j in 0 until nutrients.length()) {
            val nutrient = nutrients.getJSONObject(j)
            val nutrientName = nutrient.optString("nutrientName", "")
            val value = nutrient.optDouble("value", 0.0)
            when (nutrientName) {
                "Energy" -> {
                    if (nutrient.optString("unitName", "") == "KCAL") {
                        calories = value
                    }
                }
                "Protein" -> protein = value
                "Total lipid (fat)" -> fat = value
                "Carbohydrate, by difference" -> carbs = value
            }
        }

        foods.add(
            SearchedFood(
                name = name,
                calories = calories,
                protein = Math.round(protein * 10.0) / 10.0,
                fat = Math.round(fat * 10.0) / 10.0,
                carbs = Math.round(carbs * 10.0) / 10.0
            )
        )
    }
    foods
}
