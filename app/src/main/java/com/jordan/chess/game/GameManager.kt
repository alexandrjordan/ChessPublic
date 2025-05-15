package com.jordan.chess.game

import Piece
import PieceColor
import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.jordan.chess.R
import com.jordan.chess.pieces.Bishop
import com.jordan.chess.pieces.King
import com.jordan.chess.pieces.Knight
import com.jordan.chess.pieces.Pawn
import com.jordan.chess.pieces.Queen
import com.jordan.chess.pieces.Rook
import com.jordan.chess.utils.Quadruple
import com.jordan.chess.utils.Settings
import com.jordan.chess.utils.StringKey
import com.jordan.chess.utils.StringResources
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class GameManager(
    selectedGameTime: Int,
    botMode: Boolean,
    private val selectedColor: PieceColor = PieceColor.WHITE,
    private val context: Context
) {
    private var currentPlayer: Player = if (selectedColor == PieceColor.WHITE) Player.PLAYER1 else Player.PLAYER2
    private var playerOneTimeLeft: Float = selectedGameTime.toFloat()
    private var playerTwoTimeLeft: Float = selectedGameTime.toFloat()
    private var gameState: GameState = GameState.PLAYING
    private var moveHistory: MutableList<String> = mutableListOf()
    private var dimensions: Int = 8
    private val isUnlimitedGame: Boolean = selectedGameTime == -1
    private val playerOneName = if (selectedColor == PieceColor.WHITE) StringResources.getString(StringKey.WHITE) else StringResources.getString(StringKey.BLACK)
    private val playerTwoName = if (!botMode && selectedColor == PieceColor.WHITE) StringResources.getString(StringKey.BLACK) else if (botMode) "Bot" else StringResources.getString(StringKey.WHITE)
    private var board: Array<Array<Piece?>> = Array(dimensions) { Array(dimensions) { null } }
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private var gameTime: Float = 0f
    private val timeControl: Int = selectedGameTime
    private var playerOneCapturedPieces: MutableList<Piece> = mutableListOf()
    private var playerTwoCapturedPieces: MutableList<Piece> = mutableListOf()
    private var playerOnePromotedPieces: MutableList<Piece> = mutableListOf()
    private var playerTwoPromotedPieces: MutableList<Piece> = mutableListOf()
    private var lastMove: Pair<Pair<Int, Int>, Pair<Int, Int>>? = null
    private var enPassantTarget: Pair<Int, Int>? = null
    private var pawnToPromote: Piece? = null
    private var promotionCallback: ((Piece, PieceColor, Pair<Int, Int>) -> Unit)? = null
    private var isPromoting = false
    private var moveCountWithoutCaptureOrPawnMove = 0
    private val boardStatesHistory: MutableList<String> = mutableListOf()
    private var gameOverMessage: String = ""
    private var moveCount = 0
    private var timerRunning = false
    private var lastCapturedPiece: Piece? = null
    private val againstBot = botMode
    private var botHasPromoted = false

    private var isSoundEnabled: Boolean
        get() = Settings.enableSound
        set(value) {
            Settings.updateEnableSound(value)
        }

    fun setIsSoundEnabled(boolean: Boolean) {
        isSoundEnabled = boolean
    }

    enum class ChessSound(val soundResId: Int) {
        MOVE(R.raw.move),
        CAPTURE(R.raw.capture),
        CASTLE(R.raw.castle),
        PROMOTE(R.raw.promote),
        CHECK(R.raw.check),
        END(R.raw.end)
    }

    private fun playSound(sound: ChessSound) {
        if (isSoundEnabled) {
            val mediaPlayer = MediaPlayer.create(context, sound.soundResId)

            mediaPlayer.setOnCompletionListener {
                mediaPlayer.release()
            }

            mediaPlayer.start()
        }
    }

    enum class Player {
        PLAYER1, PLAYER2
    }

    enum class GameState {
        PLAYING, GAME_OVER
    }

    private fun getTimeControl(): Int {
        return timeControl / 60
    }

    fun getBoard(): Array<Array<Piece?>> {
        return board
    }

    private fun getBoardState(): String {
        return board.joinToString("\n") { row ->
            row.joinToString(",") { it?.let { piece -> piece::class.java.simpleName + piece.color.name } ?: "Empty" }
        }
    }

    fun initializeBoard() {
        board[0][0] = Rook(PieceColor.BLACK).apply { position = "a8" }
        board[0][1] = Knight(PieceColor.BLACK).apply { position = "b8" }
        board[0][2] = Bishop(PieceColor.BLACK).apply { position = "c8" }
        board[0][3] = Queen(PieceColor.BLACK).apply { position = "d8" }
        board[0][4] = King(PieceColor.BLACK).apply { position = "e8" }
        board[0][5] = Bishop(PieceColor.BLACK).apply { position = "f8" }
        board[0][6] = Knight(PieceColor.BLACK).apply { position = "g8" }
        board[0][7] = Rook(PieceColor.BLACK).apply { position = "h8" }

        for (i in 0 until 8) {
            board[1][i] = Pawn(PieceColor.BLACK).apply { position = "${('a' + i)}7" }
        }

        board[7][0] = Rook(PieceColor.WHITE).apply { position = "a1" }
        board[7][1] = Knight(PieceColor.WHITE).apply { position = "b1" }
        board[7][2] = Bishop(PieceColor.WHITE).apply { position = "c1" }
        board[7][3] = Queen(PieceColor.WHITE).apply { position = "d1" }
        board[7][4] = King(PieceColor.WHITE).apply { position = "e1" }
        board[7][5] = Bishop(PieceColor.WHITE).apply { position = "f1" }
        board[7][6] = Knight(PieceColor.WHITE).apply { position = "g1" }
        board[7][7] = Rook(PieceColor.WHITE).apply { position = "h1" }

        for (i in 0 until 8) {
            board[6][i] = Pawn(PieceColor.WHITE).apply { position = "${('a' + i)}2" }
        }
    }

    fun makeMove(fromPosition: Pair<Int, Int>, toPosition: Pair<Int, Int>) {
        botHasPromoted = false
        if (isPromoting) return

        if (!timerRunning && gameState == GameState.PLAYING) {
            startTimer()
        }

        val (fromRow, fromCol) = fromPosition
        val (toRow, toCol) = toPosition

        var isEnPassant = false
        var isCastling = false

        val piece = getPieceAtPosition(fromPosition) ?: return

        if (piece is King && !piece.hasMoved && abs(fromCol - toCol) == 2) {
            isCastling = handleCastle(fromPosition, toPosition, piece)
        }

        if (piece is Pawn) {
            isEnPassant = handleEnPassant(fromPosition, toPosition, piece)
        }

        val capturedPiece = board[toRow][toCol]

        if (capturedPiece != null && capturedPiece.color != piece.color) {
            makeCapture(toRow, toCol)
        }

        piece.hasMoved = true
        board[toRow][toCol] = piece
        board[fromRow][fromCol] = null
        piece.position = convertToChessNotation(toRow, toCol)

        val currentBoardState = getBoardState()
        boardStatesHistory.add(currentBoardState)

        if (capturedPiece != null || piece is Pawn) {
            moveCountWithoutCaptureOrPawnMove = 0
        } else {
            moveCountWithoutCaptureOrPawnMove++
        }

        if (moveCountWithoutCaptureOrPawnMove >= 75) {
            endGame("Remíza 75-ti tahy!")
            return
        }

        val opponentColor = if (piece.color == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE

        when {
            isKingInCheck(opponentColor) -> {
                handler.postDelayed({
                    playSound(ChessSound.CHECK)
                }, 25)
            }
            !isCastling && capturedPiece == null && !isEnPassant -> {
                playSound(ChessSound.MOVE)
            }
        }

        if (piece is Pawn) {
            handlePromotion(toRow, piece, fromPosition)
        }

        if (!isPromoting) {
            if (!botHasPromoted){
                recordMove(fromPosition, toPosition, piece, capturedPiece, isEnPassant, isCastling)
                if (checkGameOver()) return
                moveCount++
                switchTurn()
            }
        }
    }

    private fun recordMove(
        fromPosition: Pair<Int, Int>,
        toPosition: Pair<Int, Int>,
        piece: Piece,
        capturedPiece: Piece? = null,
        isEnPassant: Boolean = false,
        isCastling: Boolean = false,
        isPromotion: Boolean = false,
        promotedTo: Piece? = null
    ) {
        var moveNotation = buildString {
            if (isCastling) {
                if (toPosition.second == 6) append("O-O")
                else if (toPosition.second == 2) append("O-O-O")
            } else {
                if (piece !is Pawn) append(piece.shortName)

                if (piece is Pawn && (capturedPiece != null || isEnPassant)) {
                    append(convertToChessNotation(fromPosition.first, fromPosition.second).first())
                }

                if (capturedPiece != null || isEnPassant) append("x")

                append(convertToChessNotation(toPosition.first, toPosition.second))

                if (isPromotion && promotedTo != null) {
                    append("=").append(promotedTo.shortName)
                }
            }
        }

        val opponentColor = if (piece.color == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        val isCheck = isKingInCheck(opponentColor)

        if (isCheck) {
            moveNotation += "+"
        }

        if (isCheckmate(opponentColor)) {
            moveNotation += "#"
        }

        moveHistory.add(moveNotation)
    }

    fun getIsPromoting(): Boolean{
        return isPromoting
    }

    private fun makeCapture(row: Int, col: Int) {
        val capturedPiece = board[row][col]

        if (capturedPiece != null) {
            if (capturedPiece.color == PieceColor.WHITE) {
                playerTwoCapturedPieces.add(capturedPiece)
            } else {
                playerOneCapturedPieces.add(capturedPiece)
            }

            playSound(ChessSound.CAPTURE)

            playerOneCapturedPieces.sortByDescending { it.value }
            playerTwoCapturedPieces.sortByDescending { it.value }

            lastCapturedPiece = capturedPiece

            board[row][col] = null
        }
    }

    private fun handleCastle(fromPosition: Pair<Int, Int>, toPosition: Pair<Int, Int>, piece: King): Boolean {
        val (fromRow, fromCol) = fromPosition
        val (toRow, toCol) = toPosition

        val direction = if (toCol > fromCol) 1 else -1
        val rookCol = if (direction == 1) 7 else 0
        val rook = board[fromRow][rookCol] as? Rook

        if (rook != null && !rook.hasMoved) {
            board[toRow][toCol] = piece
            board[fromRow][fromCol] = null
            rook.hasMoved = true

            val rookNewCol = fromCol + direction
            board[fromRow][rookNewCol] = rook
            board[fromRow][rookCol] = null

            piece.position = convertToChessNotation(toRow, toCol)
            rook.position = convertToChessNotation(fromRow, rookNewCol)

            playSound(ChessSound.CASTLE)

            return true
        }

        return false
    }

    private fun handleEnPassant(fromPosition: Pair<Int, Int>, toPosition: Pair<Int, Int>, piece: Pawn): Boolean {
        val (fromRow, fromCol) = fromPosition
        val (toRow, toCol) = toPosition

        val enPassantMoves = piece.getEnPassantMoves(fromRow, fromCol, board, lastMove)

        if (enPassantMoves.contains(Pair(toRow, toCol))) {
            board[toRow][toCol] = piece
            piece.position = convertToChessNotation(toRow, toCol)

            val opponentRow = if (piece.color == PieceColor.WHITE) toRow + 1 else toRow - 1

            makeCapture(opponentRow, toCol)

            enPassantTarget = null

            return true
        }

        return false
    }

    fun setPromotionCallback(callback: (Piece, PieceColor, position: Pair<Int, Int>) -> Unit) {
        promotionCallback = callback
    }

    private fun handlePromotion(toRow: Int, piece: Pawn, fromPosition: Pair<Int, Int>) {
        if ((toRow == 0 || toRow == 7)) {
            pawnToPromote = piece
            isPromoting = true

            if (againstBot && isPlayer2Turn()) {
                botHasPromoted = true
                promotePawn(Queen(this.getPlayerColor(true)),fromPosition)
                return
            }

            promotionCallback?.invoke(piece, piece.color, fromPosition)
        }
    }

    fun promotePawn(newPiece: Piece, fromPosition: Pair<Int, Int>): Boolean {
        pawnToPromote?.let { pawn ->
            val index = board.indexOfFirst { row -> row.contains(pawn) }
            val colIndex = board[index].indexOfFirst { it == pawn }
            board[index][colIndex] = newPiece

            val toPosition = Pair(index, colIndex)

            pawnToPromote = null
            isPromoting = false

            if (newPiece.color == PieceColor.BLACK) {
                playerTwoPromotedPieces.add(newPiece)
            } else {
                playerOnePromotedPieces.add(newPiece)
            }
            playSound(ChessSound.PROMOTE)
            recordMove(fromPosition, toPosition, pawn, isPromotion = true, promotedTo = newPiece, capturedPiece = lastCapturedPiece)
            switchTurn()
            return true
        }

        return false
    }


    fun isKingInCheck(color: PieceColor): Boolean {
        // Najde pozici krále dané barvy. Pokud není nalezen, vrátí false.
        val kingPosition = findKingPosition(color) ?: return false
        // Určí barvu soupeře
        val opponentColor = if (color == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE

        // Projde všechny figurky soupeře a zkontroluje, zda některá z nich útočí na královu pozici.
        return board.indices.any { row ->
            board[row].indices.any { col ->
                val piece = board[row][col] // Získání figurky na daném poli

                // Pokud existuje soupeřova figura, která může táhnout na pozici krále, král je v šachu
                piece != null && piece.color == opponentColor &&
                    piece.getPossibleMoves(row, col, board).contains(kingPosition)
            }
        }
    }

    private fun isCheckmate(color: PieceColor): Boolean {
        // Pokud král není v šachu, nemůže být šachmat
        if (!isKingInCheck(color)) return false

        // Projde všechny figurky dané barvy a ověří, zda některá z nich může provést tah,
        // který by krále dostal ze šachu.
        return !board.indices.flatMap { row ->
            board[row].indices.map { col -> Pair(row, col) }  // Vytvoření seznamu souřadnic všech polí na desce
        }.any { (row, col) ->
            val piece = board[row][col]  // Získání figurky na daném poli

            // Pokud existuje alespoň jeden tah, který by krále dostal ze šachu, nejedná se o šachmat
            piece != null && piece.color == color &&
                piece.getPossibleMoves(row, col, board).any { move ->
                    !wouldMoveLeaveKingInCheck(Pair(row, col), move, color)
                }
        }
    }

    private fun isStalemate(color: PieceColor): Boolean {
        if (isKingInCheck(color)) return false
        return !board.indices.flatMap { row ->
            board[row].indices.map { col -> Pair(row, col) }
        }.any { (row, col) ->
            val piece = board[row][col]
            piece != null && piece.color == color &&
                piece.getPossibleMoves(row, col, board).any { move ->
                    !wouldMoveLeaveKingInCheck(Pair(row, col), move, color)
                }
        }
    }

    fun findKingPosition(color: PieceColor): Pair<Int, Int>? {
        return board.indices.flatMap { row ->
            board[row].indices.map { col -> Pair(row, col) }
        }.find { (row, col) ->
            val piece = board[row][col]
            piece is King && piece.color == color
        }
    }

    private fun wouldMoveLeaveKingInCheck(
        from: Pair<Int, Int>,
        to: Pair<Int, Int>,
        color: PieceColor
    ): Boolean {
        val originalPiece = board[to.first][to.second]
        val movingPiece = board[from.first][from.second]

        board[to.first][to.second] = movingPiece
        board[from.first][from.second] = null

        val inCheck = isKingInCheck(color)

        board[from.first][from.second] = movingPiece
        board[to.first][to.second] = originalPiece

        return inCheck
    }

    fun getCapturedPieces(player: Player): String {
        val (capturedPieces, opponentCapturedPieces, promotedPieces, opponentPromotedPieces) =
            if (selectedColor == PieceColor.WHITE) {
                if (player == Player.PLAYER1) {
                    Quadruple(playerOneCapturedPieces, playerTwoCapturedPieces, playerOnePromotedPieces, playerTwoPromotedPieces)
                } else {
                    Quadruple(playerTwoCapturedPieces, playerOneCapturedPieces, playerTwoPromotedPieces, playerOnePromotedPieces)
                }
            } else {
                if (player == Player.PLAYER1) {
                    Quadruple(playerTwoCapturedPieces, playerOneCapturedPieces, playerTwoPromotedPieces, playerOnePromotedPieces)
                } else {
                    Quadruple(playerOneCapturedPieces, playerTwoCapturedPieces, playerOnePromotedPieces, playerTwoPromotedPieces)
                }
            }

        val playerValue = capturedPieces.sumOf { it.value } + promotedPieces.sumOf { it.value - 1 }
        val opponentValue = opponentCapturedPieces.sumOf { it.value } + opponentPromotedPieces.sumOf { it.value - 1 }

        val valueDifference = playerValue - opponentValue
        val valueDifferenceString = if (valueDifference > 0) {
            " +$valueDifference"
        } else {
            ""
        }

        val trimmedCapturedPieces = if (capturedPieces.size > 8) {
            capturedPieces.sortedByDescending { it.value }.take(8)
        } else {
            capturedPieces
        }

        return trimmedCapturedPieces
            .sortedByDescending { it.value }
            .joinToString("") { it.getUnicode() } + valueDifferenceString
    }

    fun setLastMove(from: Pair<Int, Int>, to: Pair<Int, Int>) {
        lastMove = Pair(from, to)
    }

    fun getLastMove(): Pair<Pair<Int, Int>, Pair<Int, Int>>? {
        return lastMove
    }

    private fun getPieceAtPosition(position: Pair<Int, Int>): Piece? {
        return board[position.first][position.second]
    }

    fun getPossibleMoves(position: Pair<Int, Int>, ignoreColor: Boolean = false, fakeBoard: Array<Array<Piece?>>? = null): List<Pair<Int, Int>> {
        val (row, col) = position
        val piece = getPieceAtPosition(position) ?: return emptyList()
        if (!ignoreColor && piece.color != this.getPlayerColor(true)) return emptyList()
        val board = fakeBoard ?: getBoard()
        val possibleMoves = piece.getPossibleMoves(row, col, board).toMutableList()

        if (piece is Pawn) {
            possibleMoves.addAll(piece.getEnPassantMoves(row, col, board, lastMove))
        }

        if (piece is King && !isKingInCheck(piece.color)) {
            if (!piece.hasMoved) {
                possibleMoves.addAll(getCastlingMoves(row, col, piece.color))
            }
        }

        return possibleMoves.filter { move ->
            !wouldMoveLeaveKingInCheck(position, move, piece.color)
        }
    }


    private fun getCastlingMoves(row: Int, col: Int, color: PieceColor): List<Pair<Int, Int>> {
        val castlingMoves = mutableListOf<Pair<Int, Int>>()

        if (canCastleKingSide(row, col, color)) {
            castlingMoves.add(Pair(row, col + 2))
        }

        if (canCastleQueenSide(row, col, color)) {
            castlingMoves.add(Pair(row, col - 2))
        }

        return castlingMoves
    }

    private fun canCastleKingSide(row: Int, col: Int, color: PieceColor): Boolean {
        val rook = board[row][7] as? Rook
        if (rook == null || rook.hasMoved || isKingInCheck(color)) return false

        return board[row][col + 1] == null &&
            board[row][col + 2] == null &&
            !isSquareUnderAttack(row, col + 1, color) &&
            !isSquareUnderAttack(row, col + 2, color)
    }

    private fun canCastleQueenSide(row: Int, col: Int, color: PieceColor): Boolean {
        val rook = board[row][0] as? Rook
        if (rook == null || rook.hasMoved || isKingInCheck(color)) return false

        return board[row][col - 1] == null &&
            board[row][col - 2] == null &&
            board[row][col - 3] == null &&
            !isSquareUnderAttack(row, col - 1, color) &&
            !isSquareUnderAttack(row, col - 2, color)
    }

    private fun isSquareUnderAttack(row: Int, col: Int, color: PieceColor): Boolean {
        val opponentColor = if (color == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE

        for (r in board.indices) {
            for (c in board[r].indices) {
                val piece = board[r][c]
                if (piece != null && piece.color == opponentColor) {
                    val possibleMoves = piece.getPossibleMoves(r, c, board)
                    if (possibleMoves.contains(Pair(row, col))) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun startTimer() {
        if (timerRunning || gameState == GameState.GAME_OVER) {
            return
        }

        timerRunning = true
        val newRunnable = object : Runnable {
            override fun run() {
                if (gameState == GameState.GAME_OVER || !timerRunning) {
                    stopTimer()
                    return
                }

                manageTime()
                if (gameState == GameState.PLAYING && timerRunning) {
                    handler.postDelayed(this, 100)
                }
            }
        }

        timerRunnable = newRunnable
        handler.postDelayed(newRunnable, 100)
    }

    private fun stopTimer() {
        if (!timerRunning) return

        timerRunning = false
        timerRunnable?.let {
            handler.removeCallbacks(it)
        }
        timerRunnable = null
    }

    private fun manageTime() {
        if (gameState != GameState.PLAYING) {
            stopTimer()
            return
        }

        val timeUpdateInterval = 0.1f
        gameTime += timeUpdateInterval

        if (!isUnlimitedGame && moveCount >= 2) {
            if (currentPlayer == Player.PLAYER1) {
                playerOneTimeLeft -= timeUpdateInterval
            } else {
                playerTwoTimeLeft -= timeUpdateInterval
            }

            if (playerOneTimeLeft <= 0 || playerTwoTimeLeft <= 0) {
                handleTimeOut()
            }
        }
    }

    private fun switchTurn() {
        currentPlayer = if (currentPlayer == Player.PLAYER1) Player.PLAYER2 else Player.PLAYER1
    }

    private fun endGame(message: String) {
        stopTimer()
        moveCount = 0
        gameOverMessage = message
        gameState = GameState.GAME_OVER
        playSound(ChessSound.END)
    }

    fun checkGameOver(): Boolean {
        if (gameState == GameState.GAME_OVER) return true

        val currentColor = this.getPlayerColor(true)
        val opponentColor = this.getPlayerColor(false)

        if (isInsufficientMaterial()) {
            endGame(StringResources.getString(StringKey.GAME_OVER_INS_MATERIAL))
            return true
        }

        if (isCheckmate(currentColor)) {
            endGame(StringResources.getString(StringKey.GAME_OVER_CHECKMATE).format(getPlayerName(currentPlayer)))
            return true
        }

        if (isStalemate(opponentColor)) {
            endGame(StringResources.getString(StringKey.GAME_OVER_STALEMATE))
            return true
        }

        if (boardStatesHistory.filter { it == getBoardState() }.size >= 3) {
            endGame(StringResources.getString(StringKey.GAME_OVER_REPEATED))
            return true
        }

        return false
    }


    private fun isInsufficientMaterial(): Boolean {
        val kings = board.flatten().filterIsInstance<King>()
        return kings.size == 2 && board.flatten().all { it == null || it is King }
    }

    fun getGameOverMessage(): String {
        return gameOverMessage
    }


    fun getTimeLeft(player: Player): Float {
        val timeLeft = if (player == Player.PLAYER1) playerOneTimeLeft else playerTwoTimeLeft
        return timeLeft
    }

    fun getGameTime(): Float {
        return gameTime
    }

    fun getPlayerName(player: Player): String {
        return if (player == Player.PLAYER1) playerOneName else playerTwoName
    }

    fun getCurrentPlayer(): Player {
        return currentPlayer
    }

    private fun getSelectedColor(): PieceColor {
        return selectedColor
    }

    fun getPlayerColor(forCurrentPlayer: Boolean): PieceColor {
        return if (selectedColor == PieceColor.WHITE) {
            if ((forCurrentPlayer && currentPlayer == Player.PLAYER1) || (!forCurrentPlayer && currentPlayer == Player.PLAYER2)) {
                PieceColor.WHITE
            } else {
                PieceColor.BLACK
            }
        } else {
            if (currentPlayer == Player.PLAYER1) {
                PieceColor.BLACK
            } else {
                PieceColor.WHITE
            }
        }
    }

    fun getMoveHistory(): String {
        return formatMovesToPGN()
    }

    fun getCurrentField(row: Int, col: Int): Pair<Boolean, PieceColor?> {
        val pieceColor: PieceColor? = if (board[row][col] != null) {
            board[row][col]!!.color
        } else {
            null
        }

        val hasPiece = board[row][col] != null

        return Pair(hasPiece, pieceColor)
    }

    fun isPlayer2Turn(): Boolean {
        return currentPlayer == Player.PLAYER2
    }

    fun getGameState(): GameState {
        return gameState
    }

    private fun handleTimeOut() {
        endGame(StringResources.getString(StringKey.TIMEOUT).format(getPlayerName(currentPlayer)))
    }

    private fun convertToChessNotation(row: Int, col: Int): String {
        val file = ('a' + col)
        val rank = 8 - row
        return "$file$rank"
    }

    private fun formatMovesToPGN(): String {
        val builder = StringBuilder()
        for ((index, move) in moveHistory.withIndex()) {
            if (index % 2 == 0) {
                builder.append("${index / 2 + 1}. $move ")
            } else {
                builder.append("$move ")
            }
        }
        return builder.toString().trim()
    }

    @SuppressLint("DefaultLocale")
    fun generatePGN(): String {
        val event = "Casual Game"
        val site = "Jordan chess"
        val gameLength = getGameTime()
        val minutes = (gameLength / 60)
        val seconds = (gameLength % 60)
        val formattedLength = String.format("%02d:%02d", minutes.toInt(), seconds.toInt())
        val timeControl = getTimeControl()
        val date = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date())

        val whitePlayer = if (getSelectedColor() == PieceColor.WHITE) {
            getPlayerName(Player.PLAYER1)
        } else {
            getPlayerName(Player.PLAYER2)
        }

        val blackPlayer = if (getSelectedColor() == PieceColor.BLACK) {
            getPlayerName(Player.PLAYER1)
        } else {
            getPlayerName(Player.PLAYER2)
        }

        val result = if (checkGameOver()) {
            if (getGameOverMessage().contains("Šachmat") || getGameOverMessage().contains("Checkmate")) "0-1" else "1-0"
        } else {
            "1/2-1/2"
        }

        val moves = formatMovesToPGN()

        return """
        [Event "$event"]
        [Site "$site"]
        [Date "$date"]
        [Duration "$formattedLength"]
        [White "$whitePlayer"]
        [Black "$blackPlayer"]
        [Result "$result"]
        [TimeControl "$timeControl"]

        $moves
    """.trimIndent()
    }

    fun generateFEN(): String {
        val sb = StringBuilder()

        for (rank in 0 until 8) {
            var empty = 0
            for (file in 0 until 8) {
                val p = board[rank][file]
                if (p == null) {
                    empty++
                } else {
                    if (empty > 0) { sb.append(empty); empty = 0 }
                    val sym = when (p) {
                        is Pawn   -> 'p'
                        is Knight -> 'n'
                        is Bishop -> 'b'
                        is Rook   -> 'r'
                        is Queen  -> 'q'
                        is King   -> 'k'
                        else      -> '?'
                    }
                    sb.append(if (p.color == PieceColor.WHITE) sym.uppercaseChar() else sym)
                }
            }
            if (empty > 0) sb.append(empty)
            if (rank < 7) sb.append('/')
        }

        sb.append(' ')
        sb.append(if (getPlayerColor(true) == PieceColor.WHITE) 'w' else 'b')

        sb.append(' ')
        val castling = buildString {
            if (canCastle(PieceColor.WHITE, true)) append('K')
            if (canCastle(PieceColor.WHITE, false)) append('Q')
            if (canCastle(PieceColor.BLACK, true)) append('k')
            if (canCastle(PieceColor.BLACK, false)) append('q')
        }
        sb.append(if (castling.isEmpty()) '-' else castling)

        sb.append(' ')
        sb.append(enPassantTarget?.let { convertToChessNotation(it.first, it.second) } ?: "-")

        sb.append(' ')
        sb.append(moveCountWithoutCaptureOrPawnMove)

        sb.append(' ')
        sb.append((moveCount / 2) + 1)

        return sb.toString()
    }

    private fun canCastle(color: PieceColor, kingSide: Boolean): Boolean {
        val kingRow = if (color == PieceColor.WHITE) 7 else 0
        val king = board[kingRow][4] as? King

        if (king == null || king.hasMoved) return false

        val rookCol = if (kingSide) 7 else 0
        val rook = board[kingRow][rookCol] as? Rook

        return rook != null && !rook.hasMoved
    }

}