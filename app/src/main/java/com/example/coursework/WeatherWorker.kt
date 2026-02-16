
package com.example.coursework

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.json.JSONObject
import java.net.URL

class WeatherWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val weather = fetchWeather()
            NotificationHelper.sendWeatherNotification(
                applicationContext,
                temp = weather.first,
                wind = weather.second
            )
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun fetchWeather(): Pair<Double, Double> {
        val url = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=65.01&longitude=25.47" +
                "&current=temperature_2m,wind_speed_10m"

        val response = URL(url).readText()
        val json = JSONObject(response)
        val current = json.getJSONObject("current")

        val temperature = current.getDouble("temperature_2m")
        val windSpeed = current.getDouble("wind_speed_10m")

        return Pair(temperature, windSpeed)
    }
}

