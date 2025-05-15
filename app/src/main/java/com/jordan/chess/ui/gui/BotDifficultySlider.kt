package com.jordan.chess.ui.gui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jordan.chess.ui.theme.uiPrimaryColor
import com.jordan.chess.ui.theme.uiSecondaryColor
import com.jordan.chess.utils.StringKey
import com.jordan.chess.utils.StringResources
import kotlin.math.roundToInt

@Composable
fun BotDifficultySlider(onDifficultySelected: (Int) -> Unit) {
    var selectedDifficulty by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${StringResources.getString(StringKey.BOT_DIFFICULTY)} $selectedDifficulty",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Slider(
            value = selectedDifficulty.toFloat(),
            onValueChange = { value ->
                val roundedValue = value.roundToInt().coerceIn(1, 7)
                selectedDifficulty = roundedValue
                onDifficultySelected(roundedValue)
            },
            valueRange = 1f..7f,
            steps = 5,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 2.dp),
            colors = SliderDefaults.colors(
                activeTrackColor = uiPrimaryColor,
                inactiveTrackColor = Color.LightGray,
                activeTickColor = Color.LightGray,
                inactiveTickColor = uiSecondaryColor
            )
        )
    }
}