package com.example.coursework

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ThemeMode { SYSTEM, LIGHT, DARK }
enum class FontSizeOption(val label: String, val scale: Float) {
    SMALL("Small", 0.85f),
    MEDIUM("Medium", 1.0f),
    LARGE("Large", 1.15f),
    EXTRA_LARGE("Extra Large", 1.3f)
}

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode

    private val _fontSizeOption = MutableStateFlow(loadFontSizeOption())
    val fontSizeOption: StateFlow<FontSizeOption> = _fontSizeOption

    private fun loadThemeMode(): ThemeMode {
        val name = prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return try { ThemeMode.valueOf(name) } catch (_: Exception) { ThemeMode.SYSTEM }
    }

    private fun loadFontSizeOption(): FontSizeOption {
        val name = prefs.getString("font_size", FontSizeOption.MEDIUM.name) ?: FontSizeOption.MEDIUM.name
        return try { FontSizeOption.valueOf(name) } catch (_: Exception) { FontSizeOption.MEDIUM }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeMode.value = mode
    }

    fun setFontSizeOption(option: FontSizeOption) {
        prefs.edit().putString("font_size", option.name).apply()
        _fontSizeOption.value = option
    }
}
