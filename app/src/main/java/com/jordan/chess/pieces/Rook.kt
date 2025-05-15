package com.jordan.chess.pieces

import Piece
import PieceColor

class Rook(color: PieceColor) : Piece(color, shortName = "R", value = 5) {
    override fun getUnicode(): String = "♜"

    override fun getPossibleMoves(
        row: Int,
        col: Int,
        board: Array<Array<Piece?>>
    ): List<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()

        val directions = listOf(
            Pair(-1, 0), Pair(1, 0),
            Pair(0, -1), Pair(0, 1)
        )

        for (direction in directions) {
            var currentRow = row
            var currentCol = col

            while (true) {
                currentRow += direction.first
                currentCol += direction.second

                if (currentRow !in 0..7 || currentCol !in 0..7) break

                val targetPiece = board[currentRow][currentCol]
                if (targetPiece == null) {
                    possibleMoves.add(Pair(currentRow, currentCol))
                } else {
                    if (targetPiece.color != color) {
                        possibleMoves.add(Pair(currentRow, currentCol))
                    }
                    break
                }
            }
        }

        return possibleMoves
    }
}
