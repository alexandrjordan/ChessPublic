package com.jordan.chess.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun responsiveSp(base: TextUnit): TextUnit {
    val w = LocalConfiguration.current.screenWidthDp
    val scale = when {
        w < 360   -> 0.8f
        w < 600   -> 1.0f
        w < 840   -> 1.3f
        else      -> 1.8f
    }
    return (base.value * scale).sp
}