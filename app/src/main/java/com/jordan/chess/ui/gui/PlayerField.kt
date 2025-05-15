package com.jordan.chess.ui.gui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jordan.chess.ui.theme.uiPrimaryColor
import com.jordan.chess.utils.responsiveSp

@SuppressLint("DefaultLocale")
@Composable
fun Modifier.PlayerField(playerName: String, gameLength: Float, capturedPiecesText: String? = null) {
    val formattedTime = if (gameLength > 10) {
        val minutes = (gameLength / 60).toInt()
        val seconds = (gameLength % 60).toInt()
        String.format("%02d:%02d", minutes, seconds)
    } else {
        val seconds = gameLength.toInt()
        val tenths = ((gameLength - seconds) * 10).toInt()
        String.format("%2d.%d", seconds, tenths)
    }

    Box(
        modifier = this
            .padding(
                horizontal = 16.dp,
            )
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(uiPrimaryColor, RoundedCornerShape(12.dp))
            .fillMaxWidth(0.8f)
            .clip(RoundedCornerShape(15.dp))
            .height((LocalConfiguration.current.screenHeightDp / 14).dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 16.dp)
                .height(50.dp)
        ){
            Text(
                text = playerName,
                modifier = Modifier
                    .align(if (capturedPiecesText == "") Alignment.CenterStart else Alignment.TopStart),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = responsiveSp(16.sp),
            )
            Text(
                text = capturedPiecesText ?: "",
                modifier = Modifier
                    .align(Alignment.BottomStart),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = responsiveSp(16.sp),
            )
        }


        if (gameLength.toInt() != -1 || gameLength != -1.0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = formattedTime,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = responsiveSp(16.sp),
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .width(50.dp)
                        .align(Alignment.Center),
                    thickness = 1.dp,
                    color = Color.White
                )
                Text(
                    text = formattedTime,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .rotate(180f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = responsiveSp(16.sp),
                )
            }
        }
    }
}
