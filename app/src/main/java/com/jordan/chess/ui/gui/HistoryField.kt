package com.jordan.chess.ui.gui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Button
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
import com.jordan.chess.ui.theme.uiSecondaryColor
import com.jordan.chess.utils.StringKey
import com.jordan.chess.utils.StringResources
import com.jordan.chess.utils.responsiveSp
import kotlinx.coroutines.delay

@Composable
fun Modifier.HistoryField(moveHistory: String) {
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }
    val historyIsNotEmpty = moveHistory.isNotEmpty()

    Box(
        modifier = this
            .fillMaxWidth()
            .background(uiSecondaryColor)
            .height(100.dp)
            .padding(vertical = 10.dp)
    ) {
        Text(
            StringResources.getString(StringKey.MOVE_HISTORY),
            color = Color.White,
            fontSize = responsiveSp(15.sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState(), reverseScrolling = true)
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = moveHistory,
                        color = Color.White,
                        fontSize = responsiveSp(12.sp),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (historyIsNotEmpty) {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(moveHistory))
                        isCopied = true
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .width(80.dp)
                ) {
                    val iconTintColor by animateColorAsState(
                        targetValue = if (isCopied) Color.Green else Color.White,
                        animationSpec = tween(durationMillis = 300),
                        label = "Copy animation"
                    )

                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy Icon",
                        modifier = Modifier.size(24.dp),
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
    }
}