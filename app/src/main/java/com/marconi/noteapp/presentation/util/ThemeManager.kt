package com.marconi.noteapp.presentation.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "note_app_preferences"
        private const val DARK_THEME_ENABLED = "dark_theme_enabled"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getSavedTheme(): Boolean? {
        return if (sharedPreferences.contains(DARK_THEME_ENABLED)) {
            sharedPreferences.getBoolean(DARK_THEME_ENABLED, false)
        } else null
    }

    fun setDarkThemeEnabled(darkThemeEnabled: Boolean?) {
        sharedPreferences.edit().apply {
            if (darkThemeEnabled == null) {
                remove(DARK_THEME_ENABLED)
            } else {
                putBoolean(DARK_THEME_ENABLED, darkThemeEnabled)
            }
        }.apply()
    }
}
