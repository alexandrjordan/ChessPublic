package com.jordan.chess.ui.gui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jordan.chess.ui.theme.uiPrimaryColor
import com.jordan.chess.ui.theme.uiSecondaryColor
import com.jordan.chess.utils.Settings
import com.jordan.chess.utils.StringKey
import com.jordan.chess.utils.StringResources

@Composable
fun PieceRotation() {
    var isExpanded by remember { mutableStateOf(false) }

    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) -180f else -90f,
        label = "dropdown-arrow-rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth().shadow(4.dp, shape = RoundedCornerShape(25.dp)),
        colors = CardDefaults.cardColors(
            containerColor = uiSecondaryColor,
        ),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = StringResources.getString(StringKey.PIECE_ROTATION),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Expand/Collapse",
                    modifier = Modifier.rotate(rotationState),
                    tint = Color.White
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(Color.White)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                ) {
                    PieceRotationSetting(
                        label = StringResources.getString(StringKey.TOP_PIECE_ROTATION),
                        currentRotation = Settings.pieceRotation.second,
                        onRotationChange = { newRotation ->
                            Settings.updatePieceRotation(
                                Pair(Settings.pieceRotation.first, newRotation)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PieceRotationSetting(
                        label = StringResources.getString(StringKey.BOTTOM_PIECE_ROTATION),
                        currentRotation = Settings.pieceRotation.first,
                        onRotationChange = { newRotation ->
                            Settings.updatePieceRotation(
                                Pair(newRotation, Settings.pieceRotation.second)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    RotatePiecesForBot()
                }
            }
        }
    }
}

@Composable
private fun PieceRotationSetting(
    label: String,
    currentRotation: Int,
    onRotationChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Switch(
            checked = currentRotation == 180,
            onCheckedChange = { newState ->
                onRotationChange(if (newState) 180 else 0)
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
private fun RotatePiecesForBot(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = StringResources.getString(StringKey.USE_FOR_BOT),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Switch(
            checked = Settings.useRotationForBot,
            onCheckedChange = { newState ->
                Settings.updateRotationForBot(newState)
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
