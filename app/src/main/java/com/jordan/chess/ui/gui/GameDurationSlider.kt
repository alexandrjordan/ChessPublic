package com.jordan.chess.ui.gui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.sp
import com.jordan.chess.ui.theme.uiPrimaryColor
import com.jordan.chess.ui.theme.uiSecondaryColor
import com.jordan.chess.utils.StringKey
import com.jordan.chess.utils.StringResources

@Composable
fun GameDurationSlider(onDurationSelected: (Int) -> Unit) {
    val timeOptions = listOf(-1, 60, 300, 600, 1800, 3600)
    var selectedDuration by remember { mutableIntStateOf(timeOptions[0]) }

    val durationText = when (selectedDuration) {
        -1 -> StringResources.getString(StringKey.NO_LIMIT)
        60 -> "1 ${StringResources.getString(StringKey.MINUTE)}"
        else -> "${selectedDuration/60} ${StringResources.getString(StringKey.MINUTES)}"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${StringResources.getString(StringKey.GAME_LENGTH)} $durationText",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Slider(
            value = timeOptions.indexOf(selectedDuration).toFloat(),
            onValueChange = { index ->
                selectedDuration = timeOptions[index.toInt()]
                onDurationSelected(selectedDuration)
            },
            valueRange = 0f..(timeOptions.size - 1).toFloat(),
            steps = timeOptions.size - 2,
            modifier = Modifier
                .fillMaxWidth(0.8f),
            colors = SliderDefaults.colors(
                activeTrackColor = uiPrimaryColor,
                inactiveTrackColor = Color.LightGray,
                activeTickColor = Color.LightGray,
                inactiveTickColor = uiSecondaryColor
            )
        )
    }
}