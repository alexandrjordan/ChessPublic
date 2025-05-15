package com.jordan.chess

import PieceColor
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.jordan.chess.game.GameManager
import com.jordan.chess.ui.gui.ChessBoardScreen
import com.jordan.chess.ui.gui.Menu
import com.jordan.chess.ui.theme.ChessTheme
import com.jordan.chess.utils.Settings
import karballo.util.JvmPlatformUtils
import karballo.util.Utils

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Settings.initialize(this)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        enableEdgeToEdge()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            controller?.hide(WindowInsets.Type.systemBars())
            controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        setContent {
            val darkThemeEnabled = remember { mutableStateOf(Settings.enableDarkTheme) }

            ChessTheme(darkTheme = darkThemeEnabled.value, dynamicColor = false) {
                AppContent(
                    onToggleTheme = {
                        darkThemeEnabled.value = !darkThemeEnabled.value
                        Settings.updateEnableDarkTheme(darkThemeEnabled.value )
                    }
                )
            }
        }
    }
}

@Composable
fun AppContent(onToggleTheme: () -> Unit) {
    var playerColor by remember { mutableStateOf(PieceColor.WHITE) }
    var readyToPlay by remember { mutableStateOf(false) }
    var gameDuration by remember { mutableIntStateOf(-1) }
    var playWithBot by remember { mutableStateOf(false) }
    var botDifficulty by remember { mutableIntStateOf(0) }

    if (!readyToPlay) {
        Menu(
            onGameStart = { p1Color, duration, bot, difficulty ->
                playerColor = p1Color!!
                gameDuration = duration
                readyToPlay = true
                botDifficulty = difficulty
                playWithBot = bot

                if(bot && difficulty > 0){
                    Utils.instance = JvmPlatformUtils()
                }
            },
            onToggleTheme = onToggleTheme,
            gameManager = GameManager(gameDuration, playWithBot, playerColor, context = LocalContext.current)
        )
    } else {
        ChessBoardScreen(
            dimensions = 8,
            selectedColor = playerColor,
            onBackClick = { readyToPlay = false },
            gameManager = GameManager(gameDuration, playWithBot, playerColor, context = LocalContext.current),
            botMode = playWithBot,
            botDifficulty = botDifficulty
        )
    }
}

