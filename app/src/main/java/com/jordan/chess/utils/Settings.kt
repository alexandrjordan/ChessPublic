package com.jordan.chess.utils

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object Settings {
    private lateinit var preferences: android.content.SharedPreferences

    var showPossibleMoves by mutableStateOf(true)
        private set

    var enableSound by mutableStateOf(true)
        private set

    var enableDarkTheme by mutableStateOf(false)
        private set

    var selectedLanguage by mutableStateOf(Language.ENGLISH)
        private set

    var pieceRotation by mutableStateOf(Pair(0,180))
        private set

    var useRotationForBot by mutableStateOf(false)
        private set


    fun initialize(context: Context) {
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        loadSettings()
    }

    private fun loadSettings() {
        showPossibleMoves = preferences.getBoolean("showPossibleMoves", true)
        enableSound = preferences.getBoolean("sound", true)
        enableDarkTheme = preferences.getBoolean("darkTheme", false)
        selectedLanguage = preferences.getString("language", Language.ENGLISH.name)?.let { languageName ->
            Language.entries.find { it.name == languageName }
        } ?: Language.ENGLISH

        StringResources.setLanguage(selectedLanguage)

        val pieceRotationString = preferences.getString("pieceRotation", "0,180") ?: "0,180"
        val rotations = pieceRotationString.split(",")
        if (rotations.size == 2) {
            try {
                val firstRotation = rotations[0].toInt()
                val secondRotation = rotations[1].toInt()
                pieceRotation = Pair(firstRotation, secondRotation)
            } catch (e: NumberFormatException) {
                pieceRotation = Pair(0, 180)
            }
        } else {
            pieceRotation = Pair(0, 180)
        }

        useRotationForBot = preferences.getBoolean("useRotationForBot", false)
    }

    private fun saveSettings() {
        preferences.edit().apply {
            putBoolean("showPossibleMoves", showPossibleMoves)
            putBoolean("sound", enableSound)
            putBoolean("darkTheme", enableDarkTheme)
            putString("language", selectedLanguage.name)
            putString("pieceRotation", "${pieceRotation.first},${pieceRotation.second}")
            putBoolean("useRotationForBot", useRotationForBot)
            apply()
        }
    }

    fun updateShowPossibleMoves(value: Boolean) {
        showPossibleMoves = value
        saveSettings()
    }

    fun updateEnableSound(value: Boolean) {
        enableSound = value
        saveSettings()
    }

    fun updateEnableDarkTheme(value: Boolean) {
        enableDarkTheme = value
        saveSettings()
    }

    fun updateSelectedLanguage(language: Language) {
        selectedLanguage = language
        saveSettings()
    }

    fun updatePieceRotation(rotations: Pair<Int,Int>) {
        pieceRotation = rotations
        saveSettings()
    }

    fun updateRotationForBot(value: Boolean) {
        useRotationForBot = value
        saveSettings()
    }
}
