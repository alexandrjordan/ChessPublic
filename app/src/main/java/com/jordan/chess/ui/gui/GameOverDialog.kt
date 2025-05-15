package com.jordan.chess.ui.gui

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jordan.chess.utils.StringKey
import com.jordan.chess.utils.StringResources
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@Composable
fun GameOverDialog(
    gameOverMessage: String,
    gameLength: Float,
    onBackClick: () -> Unit,
    onDismiss: () -> Unit,
    pgnFile: String
) {
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }
    val minutes = (gameLength / 60).toInt()
    val seconds = (gameLength % 60).toInt()
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = StringResources.getString(StringKey.GAME_OVER),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        },
        text = {
            Text(
                text = "$gameOverMessage\n${StringResources.getString(StringKey.GAME_LENGTH_LABEL).format(formattedTime)}",
                fontSize = 16.sp,
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Column {
                        val iconTintColor by animateColorAsState(
                            targetValue = if (isCopied) Color.Green else Color.White,
                            animationSpec = tween(durationMillis = 300),
                            label = "Copy animation"
                        )

                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(pgnFile))
                                isCopied = true
                            },
                            colors = ButtonDefaults.buttonColors(),
                            modifier = Modifier.width(165.dp)
                        ) {
                            Text(StringResources.getString(StringKey.EXPORT_GAME), color = iconTintColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Copy Moves",
                                modifier = Modifier.size(20.dp),
                                tint = iconTintColor
                            )
                        }

                        LaunchedEffect(isCopied) {
                            if (isCopied) {
                                delay(2000)
                                isCopied = false
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(),
                    ) {
                        Text("OK", color = Color.White)
                    }
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(),
                        modifier = Modifier.width(165.dp)
                    ) {
                        Text(StringResources.getString(StringKey.BACK_TO_MENU), color = Color.White)
                    }
                }
            }
        }
    )
}