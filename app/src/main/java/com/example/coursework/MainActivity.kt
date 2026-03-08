package com.example.coursework

import android.Manifest
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.coursework.navigation.SetupNavGraph
import com.example.coursework.screens.Screen
import com.example.coursework.ui.theme.CourseworkTheme

class MainActivity : ComponentActivity() {

    lateinit var navController: NavHostController

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        // Animated exit for the splash screen
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            // Scale up animation
            val scaleX = ObjectAnimator.ofFloat(
                splashScreenView.iconView, View.SCALE_X, 1f, 1.5f, 0f
            )
            val scaleY = ObjectAnimator.ofFloat(
                splashScreenView.iconView, View.SCALE_Y, 1f, 1.5f, 0f
            )
            // Fade out the background
            val fadeOut = ObjectAnimator.ofFloat(
                splashScreenView.view, View.ALPHA, 1f, 0f
            )

            scaleX.interpolator = OvershootInterpolator()
            scaleY.interpolator = OvershootInterpolator()
            scaleX.duration = 800L
            scaleY.duration = 800L
            fadeOut.duration = 500L
            fadeOut.startDelay = 400L

            fadeOut.doOnEnd { splashScreenView.remove() }

            scaleX.start()
            scaleY.start()
            fadeOut.start()
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.createNotificationChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val settingsManager = SettingsManager(this)

        setContent {
            val themeMode by settingsManager.themeMode.collectAsState()
            val fontSizeOption by settingsManager.fontSizeOption.collectAsState()

            val isDark = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            CourseworkTheme(
                darkTheme = isDark,
                fontScale = fontSizeOption.scale
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    navController = rememberNavController()
                    SetupNavGraph(navController, settingsManager)
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        val destination = intent.getStringExtra("navigate_to")
        if (destination != null) {
            navController.navigate(destination)
        }
    }
}
