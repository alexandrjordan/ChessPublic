package com.jordan.chess.pieces

import Piece
import PieceColor

class King(color: PieceColor) : Piece(color, shortName = "K", value = 100) {
    override fun getUnicode(): String = "♚"

    override fun getPossibleMoves(
        row: Int,
        col: Int,
        board: Array<Array<Piece?>>
    ): List<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()

        val moveOffsets = listOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1),            Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1)
        )

        for (offset in moveOffsets) {
            val newRow = row + offset.first
            val newCol = col + offset.second

            if (newRow in 0..7 && newCol in 0..7) {
                val targetPiece = board[newRow][newCol]
                if (targetPiece == null || targetPiece.color != color) {
                    possibleMoves.add(Pair(newRow, newCol))
                }
            }
        }

        return possibleMoves
    }
}
