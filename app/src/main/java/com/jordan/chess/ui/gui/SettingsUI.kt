package com.jordan.chess.ui.gui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jordan.chess.game.GameManager
import com.jordan.chess.ui.theme.possibleMovesColor
import com.jordan.chess.ui.theme.uiPrimaryColor
import com.jordan.chess.ui.theme.uiSecondaryColor
import com.jordan.chess.utils.Language
import com.jordan.chess.utils.Settings
import com.jordan.chess.utils.StringKey
import com.jordan.chess.utils.StringResources

@Composable
fun Modifier.SettingsUI(gameManager: GameManager, onToggleTheme: () -> Unit) {
    Column(
        modifier = this
            .fillMaxWidth(0.9f),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ){
            Card (
                modifier = Modifier
                    .fillMaxWidth().shadow(4.dp, shape = RoundedCornerShape(25.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = uiSecondaryColor,
                ),
            ){
                Column (Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)){
                    ShowPossibleMovesSetting()
                    Spacer(modifier = Modifier.height(16.dp))

                    SoundSetting(gameManager)
                    Spacer(modifier = Modifier.height(16.dp))

                    DarkThemeSetting(onToggleTheme)
                    Spacer(modifier = Modifier.height(16.dp))

                    LanguageSetting()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PieceRotation()
        }

        Spacer(modifier = Modifier.height(24.dp))

        AboutSection()
    }
}

@Composable
fun ShowPossibleMovesSetting() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = StringResources.getString(StringKey.SHOW_POSSIBLE_MOVES),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Switch(
            checked = Settings.showPossibleMoves,
            onCheckedChange = { newState ->
                Settings.updateShowPossibleMoves(newState)
                setPossibleMoves(Settings.showPossibleMoves)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = uiSecondaryColor,
                checkedTrackColor = uiPrimaryColor,
                uncheckedTrackColor = Color.LightGray,
                uncheckedBorderColor = uiSecondaryColor,
            )
        )
    }
}

@Composable
fun SoundSetting(gameManager: GameManager) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = StringResources.getString(StringKey.SOUNDS),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Switch(
            checked = Settings.enableSound,
            onCheckedChange = { newState ->
                Settings.updateEnableSound(newState)
                setSounds(Settings.enableSound, gameManager)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = uiSecondaryColor,
                checkedTrackColor = uiPrimaryColor,
                uncheckedTrackColor = Color.LightGray,
                uncheckedBorderColor = uiSecondaryColor,
            )
        )
    }
}

@Composable
fun DarkThemeSetting(onToggleTheme: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = StringResources.getString(StringKey.DARK_THEME),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Switch(
            checked = Settings.enableDarkTheme,
            onCheckedChange = { newState ->
                Settings.updateEnableDarkTheme(newState)
                onToggleTheme()
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = uiSecondaryColor,
                checkedTrackColor = uiPrimaryColor,
                uncheckedTrackColor = Color.LightGray,
                uncheckedBorderColor = uiSecondaryColor,
            )
        )
    }
}

@Composable
fun LanguageSetting() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = StringResources.getString(StringKey.LANGUAGE),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        var expanded by remember { mutableStateOf(false) }

        Box {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.offset(x = 6.dp)
            ) {
                Text(Settings.selectedLanguage.toString().take(2), color = Color.White)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(65.dp),
            ) {
                Language.entries.forEach { language ->
                    DropdownMenuItem(onClick = {
                        Settings.updateSelectedLanguage(language)
                        setLanguage(Settings.selectedLanguage)
                        expanded = false
                    },
                        text = {
                            Text(
                                language.name.take(2),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    )
                }
            }
        }
    }
}

private fun setPossibleMoves(showPossibleMoves: Boolean = true) {
    possibleMovesColor = if (!showPossibleMoves) {
        possibleMovesColor.copy(
            alpha = 0f
        )
    } else {
        Color(0xFF6B8E23).copy(alpha = 0.8f)
    }
}

private fun setSounds(enableSounds: Boolean = true, gameManager: GameManager) {
    gameManager.setIsSoundEnabled(enableSounds)
}

private fun setLanguage(language: Language) {
    StringResources.setLanguage(language)
}