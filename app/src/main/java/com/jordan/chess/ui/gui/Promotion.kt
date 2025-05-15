package com.jordan.chess.ui.gui

import Piece
import PieceColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
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
import com.jordan.chess.pieces.Bishop
import com.jordan.chess.pieces.Knight
import com.jordan.chess.pieces.Queen
import com.jordan.chess.pieces.Rook
import com.jordan.chess.ui.theme.uiPrimaryColor
import com.jordan.chess.utils.responsiveSp

@Composable
fun Modifier.PawnPromotion(selectedColor: PieceColor, isPlayerTwo: Boolean, onPromotionSelected: (Piece) -> Unit) {
    Box(
        modifier = this
            .padding(
                horizontal = 16.dp,
            )
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(uiPrimaryColor, RoundedCornerShape(12.dp))
            .fillMaxWidth(0.8f)
            .clip(RoundedCornerShape(15.dp))
            .height((LocalConfiguration.current.screenHeightDp / 14).dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.93f)
                .align(Alignment.Center)
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val iconColor = if (selectedColor == PieceColor.WHITE) {
                if (isPlayerTwo) Color.Black else Color.White
            } else {
                if (isPlayerTwo) Color.White else Color.Black
            }

            val pieceColor = if (iconColor == Color.White) {PieceColor.WHITE} else {PieceColor.BLACK}

            PromotionBox("♛", "Queen", iconColor, pieceColor, onPromotionSelected)
            PromotionBox("♞", "Knight", iconColor, pieceColor, onPromotionSelected)
            PromotionBox("♝", "Bishop", iconColor, pieceColor, onPromotionSelected)
            PromotionBox("♜", "Rook", iconColor, pieceColor, onPromotionSelected)
        }
    }
}

@Composable
fun PromotionBox(piece: String, label: String, textColor: Color, color: PieceColor, onPromotionSelected: (Piece) -> Unit) {
    Box(
        modifier = Modifier
            .width(50.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White)
            .clickable {
                onPromotionSelected(
                    when (label) {
                        "Queen" -> Queen(color)
                        "Knight" -> Knight(color)
                        "Bishop" -> Bishop(color)
                        "Rook" -> Rook(color)
                        else -> throw IllegalArgumentException("Invalid label: $label")
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (textColor == Color.White){
            listOf(
                1.dp to 0.dp,
                (-1).dp to 0.dp,
                0.dp to 1.dp,
                0.dp to (-1).dp
            ).forEach { (dx, dy) ->
                BasicText(
                    text = piece,
                    modifier = Modifier.offset(x = dx, y = dy),
                    style = TextStyle(fontSize = responsiveSp(25.sp), color = Color.Black)
                )
            }
        }

        BasicText(
            text = piece,
            style = TextStyle(fontSize = responsiveSp(25.sp), color = textColor)
        )
    }
}