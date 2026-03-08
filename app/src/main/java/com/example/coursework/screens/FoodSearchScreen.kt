package com.example.coursework.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.coursework.R
import com.example.coursework.database.DatabaseProvider
import com.example.coursework.database.entities.FoodEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL
import java.util.Locale

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
    var capturedPhotoPath by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = DatabaseProvider.getDatabase(context)
    val foodEntryDao = db.foodEntryDao()

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    var tempPhotoFile by remember { mutableStateOf<File?>(null) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (!granted) {
            Toast.makeText(context, "Microphone permission is needed for voice search", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            Toast.makeText(context, "Camera permission is needed to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                query = spokenText
                // Auto-search after voice input
                isLoading = true
                errorMessage = null
                scope.launch {
                    try {
                        results = searchFoods(spokenText)
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
        }
    }


    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            capturedPhotoPath = tempPhotoFile?.absolutePath
        }
    }

    fun launchVoiceSearch() {
        if (!hasMicPermission) {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Toast.makeText(context, "Speech recognition not available on this device", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a food name...")
        }
        speechLauncher.launch(intent)
    }

    fun launchCamera() {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }

        val photoDir = File(context.filesDir, "meal_photos")
        if (!photoDir.exists()) photoDir.mkdirs()
        val photoFile = File(photoDir, "meal_${System.currentTimeMillis()}.jpg")
        tempPhotoFile = photoFile

        val photoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        cameraLauncher.launch(photoUri)
    }

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
            IconButton(onClick = { launchVoiceSearch() }) {
                Text(
                    text = if (hasMicPermission) "\uD83C\uDF99" else "\uD83C\uDF99",
                    style = MaterialTheme.typography.titleLarge
                )
            }
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

        if (!hasMicPermission) {
            Text(
                text = "Tap the mic icon to enable voice search",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
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
                            capturedPhotoPath = null
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
                    Spacer(modifier = Modifier.height(12.dp))


                    if (capturedPhotoPath != null) {
                        AsyncImage(
                            model = File(capturedPhotoPath!!),
                            contentDescription = "Meal photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { launchCamera() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Retake Photo")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { launchCamera() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (hasCameraPermission) "Take Meal Photo"
                                else "Take Photo"
                            )
                        }
                    }
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
                            date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date()),
                            imagePath = capturedPhotoPath
                        )
                    )
                    selectedFood = null
                    capturedPhotoPath = null
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedFood = null
                    capturedPhotoPath = null
                }) {
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
