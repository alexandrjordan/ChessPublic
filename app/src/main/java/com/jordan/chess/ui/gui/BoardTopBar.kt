package com.jordan.chess.ui.gui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jordan.chess.ui.theme.uiSecondaryColor
import com.jordan.chess.utils.StringKey
import com.jordan.chess.utils.StringResources
import com.jordan.chess.utils.responsiveSp

@SuppressLint("DefaultLocale")
@Composable
fun Modifier.BoardTopBar(currentPlayer: String, onBackClick: () -> Unit, gameLength: Float) {
    val minutes = (gameLength / 60).toInt()
    val seconds = (gameLength % 60).toInt()
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = this
            .fillMaxWidth()
            .background(uiSecondaryColor)
            .height(100.dp)
            .padding(vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(60.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable { onBackClick() },
            contentAlignment = Alignment.CenterStart
        )
        {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 16.dp),
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier.align(Alignment.Center).height(50.dp).fillMaxWidth().padding(horizontal = 30.dp)
        ){
            Text(
                text = (StringResources.getString(StringKey.CURRENT_TURN).format(currentPlayer)),
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = responsiveSp(15.sp),
            )
            Text(
                text = formattedTime,
                modifier = Modifier.align(Alignment.CenterEnd),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = responsiveSp(15.sp),
            )
        }
    }
}
