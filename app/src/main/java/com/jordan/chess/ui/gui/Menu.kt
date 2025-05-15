package com.jordan.chess.ui.gui

import PieceColor
import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jordan.chess.game.GameManager
import com.jordan.chess.ui.theme.uiPrimaryColor
import com.jordan.chess.ui.theme.uiSecondaryColor
import com.jordan.chess.utils.Settings
import com.jordan.chess.utils.StringKey
import com.jordan.chess.utils.StringResources
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu(
    onGameStart: (selectedColor: PieceColor?, gameDuration: Int, playWithBot: Boolean, botDifficulty: Int) -> Unit,
    onToggleTheme: () -> Unit,
    gameManager: GameManager,
) {
    var selectedColor by remember { mutableStateOf<PieceColor?>(null) }

    var showSettings by remember { mutableStateOf(false) }
    var gameDuration by remember { mutableIntStateOf(-1) }
    var selectedBotDifficulty by remember { mutableIntStateOf(0) }
    var isBotMode by remember { mutableStateOf(false) }
    val languageKey = remember(Settings.selectedLanguage) { Settings.selectedLanguage }

    val board = remember { mutableStateOf(gameManager.getBoard()) }
    gameManager.initializeBoard()

    if (showSettings) {
        BackHandler {
            showSettings = !showSettings
        }
    }

    Scaffold(
        topBar = {
            // Horní navigační menu, s tlačítkem zpět a nastavní
            key (languageKey){
                TopAppBar(
                    title = {
                        Text(
                            text = if (showSettings) StringResources.getString(StringKey.SETTINGS)
                            else StringResources.getString(StringKey.CHESS),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        if (showSettings) {
                            IconButton(onClick = {
                                showSettings = false
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = StringResources.getString(StringKey.BACK)
                                )
                            }
                        }
                    },
                    actions = {
                        if (!showSettings) {
                            IconButton(onClick = {
                                showSettings = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = StringResources.getString(StringKey.SETTINGS)
                                )
                            }
                        }
                    }
                )
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!showSettings) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        val maxBoardHeight = maxHeight * 0.4f

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (!showSettings) {
                                val rotationAngle = if (selectedColor == PieceColor.BLACK) 180f else 0f
                                board.value = gameManager.getBoard()

                                val boardModifier = Modifier
                                    .heightIn(max = maxBoardHeight)
                                    .aspectRatio(1f)
                                    .rotate(rotationAngle)

                                key(isBotMode) {
                                    boardModifier.DrawChessBoard(
                                        8, selectedColor, board.value, true, isBotMode = isBotMode
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp)
                                    ) {
                                        ColorSwitch(selectedColor = selectedColor) {
                                            selectedColor = it
                                        }
                                    }

                                    GameDurationSlider { gameDuration = it }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    ) {
                                        Text(
                                            text = StringResources.getString(StringKey.PLAY_WITH_BOT),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Switch(
                                            checked = isBotMode,
                                            onCheckedChange = { isBotMode = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                uncheckedThumbColor = uiSecondaryColor,
                                                checkedTrackColor = uiPrimaryColor,
                                                uncheckedTrackColor = Color.LightGray,
                                                uncheckedBorderColor = uiSecondaryColor,
                                            )
                                        )
                                    }

                                    if (isBotMode) {
                                        BotDifficultySlider { selectedBotDifficulty = it }
                                    }

                                    Button(
                                        onClick = {
                                            if (selectedColor == null) {
                                                selectedColor =
                                                    if (Random.nextBoolean()) PieceColor.WHITE else PieceColor.BLACK
                                            }
                                            onGameStart(
                                                selectedColor,
                                                gameDuration,
                                                isBotMode,
                                                selectedBotDifficulty
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .height(60.dp)
                                            .shadow(4.dp, RoundedCornerShape(25.dp))
                                            .clip(RoundedCornerShape(25.dp))
                                    ) {
                                        Text(
                                            text = StringResources.getString(StringKey.START),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    key(languageKey) {
                        Modifier.weight(1f).SettingsUI(gameManager, onToggleTheme)
                    }
                }
            }
        }
    )
}