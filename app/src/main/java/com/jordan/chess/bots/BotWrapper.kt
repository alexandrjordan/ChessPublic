package com.jordan.chess.bots

import com.jordan.chess.game.GameManager
import karballo.Config
import karballo.search.SearchEngine
import karballo.uci.Uci
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Spojuje oba boty dohromady.
 *
 * Obtížnost 1 používá mého bota.
 * Obtížnost 2 a více používá bota Karballo, kterému je dle obtížnosti nastaveno ELO.
 *
 * */
class BotWrapper(
    private val gameManager: GameManager,
    private val difficulty: Int
) {
    private val uci by lazy {
        Uci(Config()) { cfg -> SearchEngine(cfg) }.apply {
            processLine("uci")
            processLine("isready")
        }
    }

    fun getBestMove(): Bot.Move? =
        if (difficulty == 0) {
            Bot(gameManager).getBestMove()
        } else {
            getKarballoMove()
        }

    private fun mapDifficultyToElo(difficulty: Int): Int {
        return when (difficulty) {
            1  -> 600
            2  -> 800
            3  -> 1000
            4  -> 1200
            5  -> 1600
            6  -> 2000
            else -> 2100
        }
    }

    private fun movetimeForDifficulty(difficulty: Int): Int {
        val d = difficulty.coerceIn(1, 7)
        return 200 + (d - 1) * 100
    }

    private fun getKarballoMove(): Bot.Move? {
        val fen = gameManager.generateFEN()
        val baos = ByteArrayOutputStream()
        val ps = PrintStream(baos, true, "UTF-8")
        val origOut = System.out
        System.setOut(ps)

        uci.processLine("ucinewgame")

        if (difficulty == 7) {
            uci.processLine("setoption name UCI_LimitStrength value false")
        } else {
            uci.processLine("setoption name UCI_LimitStrength value true")
            uci.processLine("setoption name UCI_Elo value ${mapDifficultyToElo(difficulty)}")
        }

        uci.processLine("position fen $fen")
        uci.processLine("go movetime ${movetimeForDifficulty(difficulty)}")

        System.out.flush()
        System.setOut(origOut)

        val bestLine = baos.toString("UTF-8")
            .lineSequence()
            .firstOrNull { it.startsWith("bestmove ") }
            ?: return null

        val mv = bestLine.split(" ")[1]
        if (mv.length < 4 || mv == "(none)") return null

        val fromFile = mv[0] - 'a'
        val fromRank = 8 - (mv[1] - '0')
        val toFile   = mv[2] - 'a'
        val toRank   = 8 - (mv[3] - '0')

        return Bot.Move(
            from = Pair(fromRank, fromFile),
            to   = Pair(toRank,   toFile)
        )
    }
}