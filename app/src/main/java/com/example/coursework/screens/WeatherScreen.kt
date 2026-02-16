package com.example.coursework.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.coursework.WeatherWorker
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.TimeUnit


@Composable
fun WeatherScreen(navController: NavController) {
    val context = LocalContext.current
    var weatherText by remember { mutableStateOf("Fetching...") }

    LaunchedEffect(Unit) {
        weatherText = try {
            val url = "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=65.01&longitude=25.47" +
                    "&current=temperature_2m,wind_speed_10m"
            val response = withContext(Dispatchers.IO) { URL(url).readText() }
            val current = JSONObject(response).getJSONObject("current")
            val temp = current.getDouble("temperature_2m")
            val wind = current.getDouble("wind_speed_10m")
            "Temperature: ${temp}°C\nWind: ${wind} km/h"
        } catch (e: Exception) {
            "Failed to fetch weather"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = { navController.popBackStack() }) { Text("Back") }

        Text("Oulu Weather")
        Text(weatherText)

        Button( onClick = {
            val request = OneTimeWorkRequestBuilder<WeatherWorker>()
                .setInitialDelay(5, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context)
                .enqueue(request)
        }) {
            Text("Notification")
        }
    }
}
