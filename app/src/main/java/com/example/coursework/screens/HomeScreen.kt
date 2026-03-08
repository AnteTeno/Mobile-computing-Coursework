package com.example.coursework.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.coursework.R
import com.example.coursework.database.DatabaseProvider
import com.example.coursework.database.entities.FoodEntry
import com.example.coursework.database.entities.User
import java.io.File
import java.time.LocalDate

@Composable
fun HomeScreen(
    navController: NavController
) {
    var savedUser by remember { mutableStateOf<User?>(null) }
    var todayEntries by remember { mutableStateOf<List<FoodEntry>>(emptyList()) }
    val context = LocalContext.current
    val db = DatabaseProvider.getDatabase(context)

    LaunchedEffect(Unit) {
        savedUser = db.userDao().getUser()
        todayEntries = db.foodEntryDao().getEntriesForDate(LocalDate.now().toString())
    }

    val totalCalories = todayEntries.sumOf { it.calories * it.grams / 100.0 }
    val totalProtein = todayEntries.sumOf { it.protein * it.grams / 100.0 }
    val totalFat = todayEntries.sumOf { it.fat * it.grams / 100.0 }
    val totalCarbs = todayEntries.sumOf { it.carbs * it.grams / 100.0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header with greeting and profile picture
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hello,",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = savedUser?.username ?: "Guest",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            val imageModifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clickable { navController.navigate(Screen.Profile.route) }

            savedUser?.let { user ->
                AsyncImage(
                    model = Uri.fromFile(File(user.profilePicturePath)),
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            } ?: Image(
                painter = painterResource(id = R.drawable.profile_picture_placeholder),
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = imageModifier
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Today's summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Today's Nutrition",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MacroColumn("Calories", "${totalCalories.toInt()}", "kcal")
                    MacroColumn("Protein", String.format("%.1f", totalProtein), "g")
                    MacroColumn("Fat", String.format("%.1f", totalFat), "g")
                    MacroColumn("Carbs", String.format("%.1f", totalCarbs), "g")
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Navigation cards
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        NavCard(
            title = "Daily Log",
            subtitle = "${todayEntries.size} entries today",
            onClick = { navController.navigate(Screen.FoodList.route) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        NavCard(
            title = "Search Foods",
            subtitle = "Find nutrition info & add to log",
            onClick = { navController.navigate(Screen.FoodSearch.route) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        NavCard(
            title = "Profile",
            subtitle = "Edit your profile",
            onClick = { navController.navigate(Screen.Profile.route) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        NavCard(
            title = "Settings",
            subtitle = "Theme, font size",
            onClick = { navController.navigate(Screen.Settings.route) }
        )
    }
}

@Composable
fun MacroColumn(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun NavCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = ">",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen(
        navController = rememberNavController()
    )
}
