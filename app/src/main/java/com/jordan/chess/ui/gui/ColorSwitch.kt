package com.jordan.chess.ui.gui

import PieceColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jordan.chess.ui.theme.uiPrimaryColor
import kotlin.math.min

@Composable
fun ColorSwitch(selectedColor: PieceColor?, onColorSelected: (PieceColor?) -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val scaleFactor = min(screenWidth.value, screenHeight.value) / 800f

    val buttonWidth = (70 * scaleFactor).coerceIn(70f, 120f).dp
    val buttonHeight = (40 * scaleFactor).coerceIn(40f, 70f).dp
    val iconSize = (24 * scaleFactor).coerceIn(24f, 40f).dp
    val fontSize = (24 * scaleFactor).coerceIn(24f, 40f).sp
    val outlineOffset = (1 * scaleFactor).coerceIn(1f, 2f).dp
    val cornerRadius = buttonHeight / 2

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth(0.8f)
    ) {
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(cornerRadius)
                )
                .clip(RoundedCornerShape(cornerRadius))
        ) {
            val selectedBackgroundWhite = if (selectedColor == PieceColor.WHITE) uiPrimaryColor else Color.White
            val selectedBackgroundBlack = if (selectedColor == PieceColor.BLACK) uiPrimaryColor else Color.White
            val selectedBackgroundRandom = if (selectedColor == null) uiPrimaryColor else Color.White

            Box(
                Modifier
                    .width(buttonWidth)
                    .height(buttonHeight)
                    .clip(RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius))
                    .background(selectedBackgroundWhite)
                    .clickable { onColorSelected(PieceColor.WHITE) },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier.align(Alignment.Center)
                ) {
                    listOf(
                        outlineOffset to 0.dp,
                        (-outlineOffset) to 0.dp,
                        0.dp to outlineOffset,
                        0.dp to (-outlineOffset)
                    ).forEach { (dx, dy) ->
                        BasicText(
                            text = "♚",
                            modifier = Modifier.offset(x = dx, y = dy),
                            style = TextStyle(fontSize = fontSize, color = Color.Black)
                        )
                    }
                    BasicText(
                        text = "♚",
                        style = TextStyle(fontSize = fontSize, color = Color.White)
                    )
                }
            }

            Box(
                Modifier
                    .width(buttonWidth)
                    .height(buttonHeight)
                    .background(selectedBackgroundRandom)
                    .clickable { onColorSelected(null) },
                contentAlignment = Alignment.Center
            ) {
                listOf(
                    outlineOffset to 0.dp,
                    (-outlineOffset) to 0.dp,
                    0.dp to outlineOffset,
                    0.dp to (-outlineOffset)
                ).forEach { (dx, dy) ->
                    Icon(
                        imageVector = Icons.Outlined.QuestionMark,
                        contentDescription = "Shuffle Icon",
                        modifier = Modifier
                            .size(iconSize)
                            .offset(x = dx, y = dy),
                        tint = Color.Black,
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.QuestionMark,
                    contentDescription = "Shuffle Icon",
                    modifier = Modifier.size(iconSize),
                    tint = Color.White
                )
            }

            Box(
                Modifier
                    .width(buttonWidth)
                    .height(buttonHeight)
                    .clip(RoundedCornerShape(topEnd = cornerRadius, bottomEnd = cornerRadius))
                    .background(selectedBackgroundBlack)
                    .clickable { onColorSelected(PieceColor.BLACK) },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier.align(Alignment.Center)
                ) {
                    listOf(
                        outlineOffset to 0.dp,
                        (-outlineOffset) to 0.dp,
                        0.dp to outlineOffset,
                        0.dp to (-outlineOffset)
                    ).forEach { (dx, dy) ->
                        BasicText(
                            text = "♚",
                            modifier = Modifier.offset(x = dx, y = dy),
                            style = TextStyle(fontSize = fontSize, color = Color.White)
                        )
                    }
                    BasicText(
                        text = "♚",
                        style = TextStyle(fontSize = fontSize, color = Color.Black)
                    )
                }
            }
        }
    }
}