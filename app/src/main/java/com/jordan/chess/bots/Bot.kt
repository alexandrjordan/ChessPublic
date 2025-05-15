package com.jordan.chess.bots

import Piece
import android.util.Log
import com.jordan.chess.game.GameManager
import kotlin.random.Random

class Bot(gameManager: GameManager) {
    private val random: Random = Random
    var board: Array<Array<Piece?>> = gameManager.getBoard()

    private val botMoves = mutableListOf<Move>()
    private val opponentMoves = mutableListOf<Move>()
    private val gm = gameManager

    private val goodMoves = mutableListOf<Move>()
    private var normalMoves = mutableListOf<Move>()
    private var badMoves = mutableListOf<Move>()

    private fun analyzeMoves() {
        board = gm.getBoard()
        goodMoves.clear()
        normalMoves.clear()
        badMoves.clear()

        setPossibleMoves(board)
        categorizeMoves()
    }

    private fun categorizeMoves() {
        val botMovesSnapshot = ArrayList(botMoves)

        for (move in botMovesSnapshot) {
            val score = evaluateMove(move)
            when {
                score > 0 -> goodMoves.add(move)
                score == 0 -> normalMoves.add(move)
                else -> badMoves.add(move)
            }
        }
    }

    fun getBestMove(): Move? {
        analyzeMoves()
        return when {
            goodMoves.isNotEmpty() -> goodMoves.maxByOrNull { evaluateMove(it) }
            normalMoves.isNotEmpty() -> normalMoves.random()
            else -> badMoves.randomOrNull()
        }
    }

    private fun evaluateMove(move: Move): Int {
        val piece = board[move.from.first][move.from.second] ?: return Int.MIN_VALUE
        val targetPiece = board[move.to.first][move.to.second]
        var score = 0

        if (targetPiece != null) {
            score += targetPiece.value
        }

        if (!isMoveSafe(move)) {
            if (targetPiece == null || targetPiece.value <= piece.value) {
                score -= piece.value
            }
        }

        return score
    }

    private fun isMoveSafe(move: Move): Boolean {
        makeFakeMove(move)
        val isCaptured = opponentMoves.any { it.to == move.to }
        unmakeFakeMove()
        return !isCaptured
    }

    private fun setPossibleMoves(boardToAnalyze: Array<Array<Piece?>>) {
        botMoves.clear()
        opponentMoves.clear()
        if (!gm.isPlayer2Turn()) return
        val currentPlayerColor = gm.getPlayerColor(true)

        for (row in boardToAnalyze.indices) {
            for (col in boardToAnalyze[row].indices) {
                val piece = boardToAnalyze[row][col]
                if (piece != null) {
                    val moves = gm.getPossibleMoves(Pair(row, col), true, boardToAnalyze)
                    for (moveDestination in moves) {
                        val move = Move(from = Pair(row, col), to = moveDestination)
                        if (piece.color == currentPlayerColor) botMoves.add(move)
                        else opponentMoves.add(move)
                    }
                }
            }
        }
    }

    private var fakeBoard: Array<Array<Piece?>> = Array(8) { Array(8) { null } }

    private fun makeFakeMove(move: Move) {
        for (i in board.indices) {
            fakeBoard[i] = board[i].copyOf()
        }
        val piece = fakeBoard[move.from.first][move.from.second]
        if (piece != null) {
            fakeBoard[move.to.first][move.to.second] = piece
            fakeBoard[move.from.first][move.from.second] = null
        }
        setPossibleMoves(fakeBoard)
    }

    private fun unmakeFakeMove() {
        board = gm.getBoard()
        setPossibleMoves(board)
    }

    data class Move(
        val from: Pair<Int, Int>,
        val to: Pair<Int, Int>
    )
}