package com.jordan.chess.pieces

import Piece
import PieceColor
import kotlin.math.abs

class Pawn(color: PieceColor, position: String? = null) : Piece(
    color,
    shortName = null,
    position,
    value = 1
) {

    override fun getUnicode(): String = "♟"

    override fun getPossibleMoves(
        row: Int,
        col: Int,
        board: Array<Array<Piece?>>
    ): List<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()
        val direction = if (color == PieceColor.WHITE) -1 else 1

        if (row + direction in 0..7 && board[row + direction][col] == null) {
            possibleMoves.add(Pair(row + direction, col))
        }

        if ((color == PieceColor.WHITE && row == 6) || (color == PieceColor.BLACK && row == 1)) {
            if (board[row + direction][col] == null && board[row + 2 * direction][col] == null) {
                possibleMoves.add(Pair(row + 2 * direction, col))
            }
        }

        if (row + direction in 0..7) {
            if (col - 1 in 0..7 && board[row + direction][col - 1]?.color != color && board[row + direction][col - 1] != null) {
                possibleMoves.add(Pair(row + direction, col - 1))
            }
            if (col + 1 in 0..7 && board[row + direction][col + 1]?.color != color && board[row + direction][col + 1] != null) {
                possibleMoves.add(Pair(row + direction, col + 1))
            }
        }

        return possibleMoves
    }

    fun getEnPassantMoves(
        row: Int,
        col: Int,
        board: Array<Array<Piece?>>,
        lastMove: Pair<Pair<Int, Int>, Pair<Int, Int>>?
    ): List<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()
        if (lastMove != null) {
            val (from, to) = lastMove
            val (fromRow, _) = from
            val (toRow, toCol) = to

            if (abs(fromRow - toRow) == 2 &&
                abs(toCol - col) == 1 &&
                board[toRow][toCol] is Pawn &&
                board[toRow][toCol]?.color != color &&
                row == toRow
            ) {
                possibleMoves.add(Pair(toRow + (if (color == PieceColor.WHITE) -1 else 1), toCol))
            }
        }
        return possibleMoves
    }
}