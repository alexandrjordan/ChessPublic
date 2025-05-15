package com.jordan.chess.ui.gui

import com.jordan.chess.bots.BotWrapper
import Piece
import PieceColor
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jordan.chess.game.GameManager
import com.jordan.chess.ui.theme.boardDarkSquareColor
import com.jordan.chess.ui.theme.boardLightSquareColor
import com.jordan.chess.ui.theme.kingInDangerColor
import com.jordan.chess.ui.theme.moveHighlightColor
import com.jordan.chess.ui.theme.possibleMovesColor
import com.jordan.chess.utils.Settings
import com.jordan.chess.utils.StringKey
import com.jordan.chess.utils.StringResources
import com.jordan.chess.utils.responsiveSp
import kotlinx.coroutines.delay

/**
*   Composable funkce, která vykresluje hlavní herní obrazovku.
*
*   Zajišťuje správné vykreslení hrací desky a její aktualizaci.
*
*   Správje časovače obou hráčů a detekci ukončení v GUI.
*
*   Vykresluje "sekce" grafických prvků.
*/
@Composable
fun ChessBoardScreen(
    dimensions: Int = 8,
    selectedColor: PieceColor,
    onBackClick: () -> Unit,
    gameManager: GameManager,
    botMode: Boolean,
    botDifficulty: Int = 0
) {
    val isInitialized = remember { mutableStateOf(false) }
    val showReturnDialog = remember { mutableStateOf(false) }
    val player1Time = remember { mutableFloatStateOf(gameManager.getTimeLeft(GameManager.Player.PLAYER1)) }
    val player2Time = remember { mutableFloatStateOf(gameManager.getTimeLeft(GameManager.Player.PLAYER2)) }
    val gameTime = remember { mutableFloatStateOf(gameManager.getGameTime()) }
    val moveHistory = remember { mutableStateOf(gameManager.getMoveHistory()) }

    val promotionPiece = remember { mutableStateOf<Piece?>(null) }
    val pawnFromPositionToPromotion = remember { mutableStateOf(Pair(0, 0)) }
    val board = remember { mutableStateOf(gameManager.getBoard()) }
    var botWrapper by remember { mutableStateOf<BotWrapper?>(null) }

    var showGameOverDialog by remember { mutableStateOf(false) }
    var gameOverMessage by remember { mutableStateOf("") }
    var isGameRunning by remember { mutableStateOf(true) }

    BackHandler {
        if (!showGameOverDialog && !showReturnDialog.value){
            showReturnDialog.value = true
        } else {
            showGameOverDialog = false
            showReturnDialog.value = false
        }
    }

    if (!isInitialized.value) {
        gameManager.initializeBoard()
        isInitialized.value = true
    }

    if (botMode && botWrapper == null) {
        botWrapper = BotWrapper(gameManager, botDifficulty)
    }

    // Nastavení callback funkce pro práci s promocí
    LaunchedEffect(Unit) {
        gameManager.setPromotionCallback { pawn, _, position ->
            promotionPiece.value = pawn
            pawnFromPositionToPromotion.value = position
        }
    }

    // Nastavení času hráčů, kontrola statu hry
    LaunchedEffect(Unit) {
        while (isGameRunning) {
            try {
                delay(100L)
                gameTime.floatValue = gameManager.getGameTime()
                player1Time.floatValue = gameManager.getTimeLeft(GameManager.Player.PLAYER1)
                player2Time.floatValue = gameManager.getTimeLeft(GameManager.Player.PLAYER2)

                if (gameManager.getGameState() == GameManager.GameState.GAME_OVER || gameManager.checkGameOver()) {
                    isGameRunning = false
                    showGameOverDialog = true
                    gameOverMessage = gameManager.getGameOverMessage()
                    break
                }
            } catch (e: Exception) {
                isGameRunning = false
                break
            }
        }
    }

    if (showGameOverDialog || (showReturnDialog.value && gameManager.getGameState() == GameManager.GameState.GAME_OVER)) {
        GameOverDialog(
            gameOverMessage = gameOverMessage,
            gameLength = gameManager.getGameTime(),
            onBackClick = { onBackClick() },
            onDismiss = {
                showGameOverDialog = false
                showReturnDialog.value = false
            },
            pgnFile = gameManager.generatePGN()
        )
    }

    if (showReturnDialog.value && gameManager.getGameState() != GameManager.GameState.GAME_OVER) {
        AlertDialog(
            onDismissRequest = {
                showReturnDialog.value = false
            },
            title = {
                Text(
                    text = StringResources.getString(StringKey.CONFIRM_EXIT_MESSAGE),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            },
            confirmButton = {
                Button(onClick = {
                    showReturnDialog.value = false
                    onBackClick()
                }) {
                    Text(StringResources.getString(StringKey.YES), color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showReturnDialog.value = false }) {
                    Text((StringResources.getString(StringKey.NO)), color = Color.White)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val otherElementsHeight = 200.dp
            val availableHeight = maxHeight - otherElementsHeight
            val availableWidth = maxWidth * 0.98f
            val boardSize = minOf(availableWidth, availableHeight * 0.8f)

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Modifier.BoardTopBar(
                    currentPlayer = gameManager.getPlayerName(gameManager.getCurrentPlayer()),
                    onBackClick = { showReturnDialog.value = true },
                    gameLength = gameTime.floatValue
                )

                // horní hráč + případná promoční volba
                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                    Modifier
                        .graphicsLayer(rotationZ = if (!botMode) 180f else 0f)
                        .PlayerField(
                            gameManager.getPlayerName(GameManager.Player.PLAYER2),
                            player2Time.floatValue,
                            gameManager.getCapturedPieces(GameManager.Player.PLAYER2)
                        )

                    if (promotionPiece.value != null && !gameManager.isPlayer2Turn()) {
                        Modifier
                            .matchParentSize()
                            .PawnPromotion(
                                selectedColor = selectedColor,
                                isPlayerTwo = false,
                                onPromotionSelected = {
                                    gameManager.promotePawn(it, pawnFromPositionToPromotion.value)
                                    moveHistory.value = gameManager.getMoveHistory()
                                    promotionPiece.value = null
                                }
                            )
                    }
                }

                val shouldRedraw = remember { mutableStateOf(false) }

                // Sekce hrací desky
                Box(
                    modifier = Modifier.size(boardSize),
                    contentAlignment = Alignment.Center
                ) {
                    Modifier
                        .fillMaxSize()
                        .DrawChessBoard(
                            dimensions,
                            selectedColor,
                            board.value,
                            decorative = false,
                            gameManager,
                            onMove = {
                                shouldRedraw.value = true
                                board.value = gameManager.getBoard()
                                moveHistory.value = gameManager.getMoveHistory()
                            },
                            isBotMode = botMode,
                            botWrapper = botWrapper
                        )
                }

                if (shouldRedraw.value) {
                    shouldRedraw.value = false
                }

                // spodní hráč + případná promoční volba
                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                    Modifier
                        .PlayerField(
                            gameManager.getPlayerName(GameManager.Player.PLAYER1),
                            player1Time.floatValue,
                            gameManager.getCapturedPieces(GameManager.Player.PLAYER1)
                        )

                    if (promotionPiece.value != null && gameManager.isPlayer2Turn() && !botMode) {
                        Modifier
                            .matchParentSize()
                            .graphicsLayer(rotationZ = 180f)
                            .PawnPromotion(
                                selectedColor = selectedColor,
                                isPlayerTwo = true,
                                onPromotionSelected = {
                                    gameManager.promotePawn(it, pawnFromPositionToPromotion.value)
                                    moveHistory.value = gameManager.getMoveHistory()
                                    promotionPiece.value = null
                                }
                            )
                    }
                }

                Modifier
                    .HistoryField(moveHistory.value)
            }
        }
    }
}


/**
*   Composable funkce, která vykresluje hrací desku.
*
*   Zajišťuje správnou orientaci desky a rotaci šachových figurek
*   podle barvy hráče. Stará se o zvýraznění tahů a
*   posledního provedeného tahu.
*
*   Komunikuje s gameManagerem, umožňuje správné provádění možých tahů
*   jak hráčem, tak i botem.
*
*   V případě hry proti botovi zajišťuje jeho tahy na základě nejlepšího
*   možného tahu získaného z instance třídy BotWrapper, jenž rozhodne, zda bude hrát Kabrallo bot nebo můj bot.
*
*   Funkce podporuje interakci hráče s figurkami a umožňuje výběr tahů
*   na základě toho, zda jsou legální. Po tahu hráče, nebo bota se zavolá funkce onMove,
*   která aktualizuje hrací desku a historii tahů.
 *
 *   Deska podporuje 3 typy vstupu:
 *      - Kliknutí: vybere figurku, zobrazí její možné tahy, dalším kliknutím provede tah
 *      - Držení: vybere figurku a spustí tažení figurky. po puštění provede tah
 *      - Táhnutí: rovnou provede tah na místo, kam uživatel přetáhne prstem
*
*   Je možné vykreslit desku pouze jako dekorativní. Deska se vykreslí
*   bez možnosti interakce s ní, pro její použití v menu.
*/
@Composable
fun Modifier.DrawChessBoard(
    dimensions: Int,
    selectedColor: PieceColor?,
    board: Array<Array<Piece?>>,
    decorative: Boolean = false,
    gameManager: GameManager? = null,
    onMove: () -> Unit = {},
    isBotMode: Boolean = false,
    botWrapper: BotWrapper? = null
) {
    val baseFontSize = responsiveSp((LocalConfiguration.current.screenHeightDp / 2.3 / dimensions * 0.75).sp)
    val piecesSize = if (decorative) baseFontSize * 0.6f else baseFontSize
    val selectedField = remember { mutableStateOf<Pair<Int, Int>?>(null) }
    val possibleMoveFields = remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }
    val lastMove = gameManager?.getLastMove()

    val draggedPiece = remember { mutableStateOf<DraggedPieceState?>(null) }
    val boardSize = remember { mutableStateOf(IntSize(0, 0)) }

    // Pokud je hra proti botovi a je dostupný bot a gameManager a zrovna hraje bot
    if (isBotMode
        && botWrapper != null // instance třídy BotWrapper nesmí být null
        && gameManager?.getGameState() == GameManager.GameState.PLAYING // hra ještě běží
        && gameManager.getCurrentPlayer() == GameManager.Player.PLAYER2 // hraje hráč číslo 2
    ) {
        LaunchedEffect(gameManager.getMoveHistory().length) {
            // získá nejlepší tah z instance třídy BotWrapper, když nějaký existuje
            val best = botWrapper.getBestMove() ?: return@LaunchedEffect

            delay(150) // menší odezva, aby bot nehrál moc rychle
            gameManager.makeMove(best.from, best.to) // provede tah
            gameManager.setLastMove(best.from, best.to) // nastaví tah jako poslední provodený
            onMove() // aktualizuje desku
        }
    }

    Box(
        modifier = this
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .aspectRatio(1f)
            .rotate(if (selectedColor == PieceColor.BLACK && !decorative) 180f else 0f)
            .onSizeChanged { boardSize.value = it }
    ) {
        Column {
            for (i in 0 until dimensions) {
                Row(modifier = Modifier.weight(1f)) {
                    for (j in 0 until dimensions) {
                        val piece = board[i][j]
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(if ((i + j) % 2 == 0) boardLightSquareColor else boardDarkSquareColor)
                                .then(
                                    when {
                                        selectedField.value == Pair(i, j) -> Modifier.background(
                                            moveHighlightColor.copy(alpha = 0.8f)
                                        )

                                        lastMove?.let {
                                            it.first == Pair(i, j) || it.second == Pair(i, j)
                                        } == true -> Modifier.background(
                                            moveHighlightColor.copy(alpha = 0.8f)
                                        )

                                        else -> Modifier
                                    }
                                )
                                .run {
                                    if (!decorative && gameManager !== null && gameManager.getGameState() != GameManager.GameState.GAME_OVER) {
                                        this
                                            .pointerInput("combined") {
                                                detectTapGestures {
                                                    if (draggedPiece.value == null && !gameManager.getIsPromoting()) {
                                                        val (hasPiece, pieceColor) = gameManager.getCurrentField(
                                                            i,
                                                            j
                                                        )
                                                        val targetField = Pair(i, j)

                                                        if (selectedField.value == targetField) {
                                                            selectedField.value = null
                                                            possibleMoveFields.value = emptyList()
                                                            return@detectTapGestures
                                                        }

                                                        // pokud je na zrovna kliknutém poli figurka a je správného hráče
                                                        if (hasPiece && gameManager.getPlayerColor(true) == pieceColor) {
                                                            selectedField.value = targetField // Vybere pole
                                                            possibleMoveFields.value = gameManager.getPossibleMoves(targetField) // Získá dostupné tahy
                                                        }

                                                        // Pokud je vybrané pole a bylo kliknuto na pole shodující se s možnými tahy figurky
                                                        if (selectedField.value != null && possibleMoveFields.value.contains(targetField)
                                                        ) {
                                                            // GameManager provede tah z vybraného pole na kliknuté pole
                                                            gameManager.makeMove(selectedField.value!!, targetField)
                                                            gameManager.setLastMove(selectedField.value!!, targetField) // nastaví poslední tah

                                                            // Resetuje Proměnné
                                                            possibleMoveFields.value = emptyList()
                                                            selectedField.value = null
                                                            // Znovu vykreslí hrací desku
                                                            onMove()
                                                        }
                                                    }
                                                }
                                            }
                                            .pointerInput("holdAndDrag") {
                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = { offset ->
                                                        if (gameManager.getIsPromoting()) return@detectDragGesturesAfterLongPress

                                                        val (hasPiece, pieceColor) = gameManager.getCurrentField(
                                                            i,
                                                            j
                                                        )

                                                        if (hasPiece && gameManager.getPlayerColor(
                                                                true
                                                            ) == pieceColor
                                                        ) {
                                                            val sourcePair = Pair(i, j)
                                                            selectedField.value = sourcePair
                                                            possibleMoveFields.value =
                                                                gameManager.getPossibleMoves(
                                                                    sourcePair
                                                                )

                                                            val squareSize =
                                                                boardSize.value.width / dimensions

                                                            val centerX =
                                                                j * squareSize + squareSize / 2 - piecesSize.value / 2
                                                            val centerY =
                                                                i * squareSize + squareSize / 2 - piecesSize.value / 2

                                                            draggedPiece.value = DraggedPieceState(
                                                                piece = board[i][j],
                                                                sourceField = sourcePair,
                                                                position = Offset(centerX, centerY),
                                                                dragOffset = Offset.Zero,
                                                                initialOffset = offset
                                                            )
                                                        }
                                                    },
                                                    onDrag = { change, dragAmount ->
                                                        draggedPiece.value?.let { dragState ->
                                                            draggedPiece.value = dragState.copy(
                                                                dragOffset = Offset(
                                                                    dragState.dragOffset.x + dragAmount.x,
                                                                    dragState.dragOffset.y + dragAmount.y
                                                                )
                                                            )
                                                        }
                                                        change.consume()
                                                    },
                                                    onDragEnd = {
                                                        draggedPiece.value?.let { dragState ->
                                                            val squareSize =
                                                                boardSize.value.width / dimensions

                                                            val currentPosition = Offset(
                                                                dragState.position.x + dragState.dragOffset.x,
                                                                dragState.position.y + dragState.dragOffset.y
                                                            )

                                                            val targetJ =
                                                                ((currentPosition.x + piecesSize.value / 2) / squareSize)
                                                                    .toInt()
                                                                    .coerceIn(0, dimensions - 1)
                                                            val targetI =
                                                                ((currentPosition.y + piecesSize.value / 2) / squareSize)
                                                                    .toInt()
                                                                    .coerceIn(0, dimensions - 1)
                                                            val targetField = Pair(targetI, targetJ)

                                                            if (possibleMoveFields.value.contains(
                                                                    targetField
                                                                )
                                                            ) {
                                                                gameManager.makeMove(
                                                                    dragState.sourceField,
                                                                    targetField
                                                                )
                                                                gameManager.setLastMove(
                                                                    dragState.sourceField,
                                                                    targetField
                                                                )
                                                                onMove()
                                                            }

                                                            possibleMoveFields.value = emptyList()
                                                            selectedField.value = null
                                                            draggedPiece.value = null
                                                        }
                                                    },
                                                    onDragCancel = {
                                                        possibleMoveFields.value = emptyList()
                                                        selectedField.value = null
                                                        draggedPiece.value = null
                                                    }
                                                )
                                            }.pointerInput("drag") {
                                                detectDragGestures(
                                                    onDragStart = { offset ->
                                                        if (gameManager.getIsPromoting()) return@detectDragGestures

                                                        val (hasPiece, pieceColor) = gameManager.getCurrentField(
                                                            i,
                                                            j
                                                        )

                                                        if (hasPiece && gameManager.getPlayerColor(
                                                                true
                                                            ) == pieceColor
                                                        ) {
                                                            val sourcePair = Pair(i, j)
                                                            selectedField.value = sourcePair
                                                            possibleMoveFields.value =
                                                                gameManager.getPossibleMoves(
                                                                    sourcePair
                                                                )

                                                            val squareSize =
                                                                boardSize.value.width / dimensions

                                                            val centerX =
                                                                j * squareSize + squareSize / 2 - piecesSize.value / 2
                                                            val centerY =
                                                                i * squareSize + squareSize / 2 - piecesSize.value / 2

                                                            draggedPiece.value = DraggedPieceState(
                                                                piece = board[i][j],
                                                                sourceField = sourcePair,
                                                                position = Offset(centerX, centerY),
                                                                dragOffset = Offset.Zero,
                                                                initialOffset = offset
                                                            )
                                                        }
                                                    },
                                                    onDrag = { change, dragAmount ->
                                                        draggedPiece.value?.let { dragState ->
                                                            draggedPiece.value = dragState.copy(
                                                                dragOffset = Offset(
                                                                    dragState.dragOffset.x + dragAmount.x,
                                                                    dragState.dragOffset.y + dragAmount.y
                                                                )
                                                            )
                                                        }
                                                        change.consume()
                                                    },
                                                    onDragEnd = {
                                                        draggedPiece.value?.let { dragState ->
                                                            val squareSize =
                                                                boardSize.value.width / dimensions

                                                            val currentPosition = Offset(
                                                                dragState.position.x + dragState.dragOffset.x,
                                                                dragState.position.y + dragState.dragOffset.y
                                                            )

                                                            val targetJ =
                                                                ((currentPosition.x + piecesSize.value / 2) / squareSize)
                                                                    .toInt()
                                                                    .coerceIn(0, dimensions - 1)
                                                            val targetI =
                                                                ((currentPosition.y + piecesSize.value / 2) / squareSize)
                                                                    .toInt()
                                                                    .coerceIn(0, dimensions - 1)
                                                            val targetField = Pair(targetI, targetJ)

                                                            if (possibleMoveFields.value.contains(
                                                                    targetField
                                                                )
                                                            ) {
                                                                gameManager.makeMove(
                                                                    dragState.sourceField,
                                                                    targetField
                                                                )
                                                                gameManager.setLastMove(
                                                                    dragState.sourceField,
                                                                    targetField
                                                                )
                                                                onMove()
                                                            }

                                                            possibleMoveFields.value = emptyList()
                                                            selectedField.value = null
                                                            draggedPiece.value = null
                                                        }
                                                    },
                                                    onDragCancel = {
                                                        possibleMoveFields.value = emptyList()
                                                        selectedField.value = null
                                                        draggedPiece.value = null
                                                    }
                                                )
                                            }
                                    } else this
                                }
                        ) {
                            // Pokud je dostupný gameManager
                            if (gameManager != null) {
                                // Najde pozici krále aktuálního hráče
                                val kingPosition = gameManager.findKingPosition(gameManager.getPlayerColor(true))

                                // Pokud je král v šachu
                                if (gameManager.isKingInCheck(gameManager.getPlayerColor(true))) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .align(Alignment.Center)
                                            .then(
                                                // Pokud je aktuální pole pozicí krále a zároveň není pole právě vybrané, zvýrazní jej
                                                if (kingPosition == Pair(
                                                        i,
                                                        j
                                                    ) && selectedField.value != Pair(i, j)
                                                ) {
                                                    Modifier.background(kingInDangerColor) // Zvýraznění pozice krále v šachu
                                                } else {
                                                    Modifier // Jinak ponechá beze změny
                                                }
                                            )
                                    )
                                }
                            }

                            if (piece != null && draggedPiece.value?.sourceField != Pair(i, j)) {
                                val outlineColor =
                                    if (selectedColor == null) {
                                        Color.Transparent
                                    } else if (piece.color == PieceColor.WHITE) {
                                        Color.Black
                                    } else {
                                        Color.White
                                    }

                                val textColor =
                                    if (selectedColor == null) {
                                        Color.Transparent
                                    } else if (piece.color == PieceColor.WHITE) {
                                        Color.White
                                    } else {
                                        Color.Black
                                    }

                                listOf(
                                    1.dp to 0.dp,
                                    (-1).dp to 0.dp,
                                    0.dp to 1.dp,
                                    0.dp to (-1).dp
                                ).forEach { (dx, dy) ->
                                    Text(
                                        text = piece.getUnicode(),
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .offset(x = dx, y = dy)
                                            .run {
                                                val rotation = calculatePieceRotation(
                                                    isBotMode,
                                                    selectedColor ?: PieceColor.WHITE,
                                                    piece.color
                                                )
                                                this.rotate(rotation)
                                            },
                                        fontSize = piecesSize,
                                        color = outlineColor
                                    )
                                }

                                Text(
                                    text = piece.getUnicode(),
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .run {
                                            val rotation = calculatePieceRotation(
                                                isBotMode,
                                                selectedColor ?: PieceColor.WHITE,
                                                piece.color
                                            )
                                            this.rotate(rotation)
                                        },
                                    fontSize = piecesSize,
                                    color = textColor
                                )
                            }

                            if (possibleMoveFields.value.contains(Pair(i, j))) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(possibleMovesColor)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }

        draggedPiece.value?.let { dragState ->
            dragState.piece?.let { piece ->
                val currentPosition = Offset(
                    dragState.position.x + dragState.dragOffset.x,
                    dragState.position.y + dragState.dragOffset.y
                )

                Box(
                    modifier = Modifier
                        .offset { IntOffset(currentPosition.x.toInt(), currentPosition.y.toInt()) }
                        .wrapContentSize()
                ) {
                    val outlineColor = if (piece.color == PieceColor.WHITE) Color.Black else Color.White
                    val textColor = if (piece.color == PieceColor.WHITE) Color.White else Color.Black

                    listOf(
                        1.dp to 0.dp,
                        (-1).dp to 0.dp,
                        0.dp to 1.dp,
                        0.dp to (-1).dp
                    ).forEach { (dx, dy) ->
                        Text(
                            text = piece.getUnicode(),
                            modifier = Modifier
                                .offset(x = dx, y = dy)
                                .run {
                                    val rotation = calculatePieceRotation(
                                        isBotMode,
                                        selectedColor ?: PieceColor.WHITE,
                                        piece.color
                                    )
                                    this.rotate(rotation)
                                },
                            fontSize = piecesSize,
                            color = outlineColor
                        )
                    }

                    Text(
                        text = piece.getUnicode(),
                        modifier = Modifier.run {
                            val rotation = calculatePieceRotation(
                                isBotMode,
                                selectedColor ?: PieceColor.WHITE,
                                piece.color
                            )
                            this.rotate(rotation)
                        },
                        fontSize = piecesSize,
                        color = textColor
                    )
                }
            }
        }

        if (selectedColor == null) {
            Box(
                modifier = Modifier.align(Alignment.Center)
            ) {
                listOf(
                    1.dp to 0.dp,
                    (-1).dp to 0.dp,
                    0.dp to 1.dp,
                    0.dp to (-1).dp
                ).forEach { (dx, dy) ->
                    Icon(
                        imageVector = Icons.Outlined.QuestionMark,
                        contentDescription = "Outlined Shuffle",
                        tint = Color.Black,
                        modifier = Modifier
                            .size((LocalConfiguration.current.screenHeightDp / 3.5).dp)
                            .offset(x = dx, y = dy)
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.QuestionMark,
                    contentDescription = "Outlined Shuffle",
                    tint = Color.White,
                    modifier = Modifier
                        .size((LocalConfiguration.current.screenHeightDp / 3.5).dp)
                )
            }
        }
    }
}

data class DraggedPieceState(
    val piece: Piece?,
    val sourceField: Pair<Int, Int>,
    val position: Offset,
    val dragOffset: Offset,
    val initialOffset: Offset
)


/**
 * Vypočítá rotaci figurky podle nastavení rotace.
 */
fun calculatePieceRotation(
    isBotMode: Boolean,      // Určuje, zda je zapnutý režim pro bota
    selectedColor: PieceColor,  // Barva spodního hráče (rotace desky)
    pieceColor: PieceColor    // Barva dané figurky
): Float {
    return when {
        // Pokud je hra proti botovi a není zapnuté nastavení použití rotace pro bota
        isBotMode && !Settings.useRotationForBot -> when {
            // Pokud spodní hráč zvolil bílou barvu a figurka je bílá vrací se 0°, v opačném případě 180°
            selectedColor == PieceColor.WHITE -> 0f
            selectedColor == PieceColor.BLACK -> 180f
            else -> 0f  // Defaultní rotace 0°
        }

        else -> when {
            // Pokud spodní hráč zvolil bílou barvu, rotace závisí na barvě figurky a vrací se hodnota uložená v nastavení
            selectedColor == PieceColor.WHITE ->
                if (pieceColor == selectedColor) { Settings.pieceRotation.first.toFloat() }
                else { Settings.pieceRotation.second.toFloat() }
            // Pokud spodní hráč zvolil černou barvu, rotace závisí na barvě figurky a vrací se hodnota uložená v nastavení otočená o 180°
            selectedColor == PieceColor.BLACK ->
                if (pieceColor == selectedColor) { Settings.pieceRotation.first - 180f }
                else { Settings.pieceRotation.second - 180f }

            else -> 0f  // Defaultní rotace 0°
        }
    }
}
