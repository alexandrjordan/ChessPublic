enum class PieceColor {
    WHITE, BLACK
}


/*
*   "Šablona" pro jednotlivé postavičky.
*
*   Postavička má vlastnosti:
*       - barva
*       - její typ
*       - "přezdívku"
*       - aktualní pozici
*       - hodnotu
*
*   Jelikož u některých postaviček je třeba zjistit, zda se je již pohla, nebo ne, všechny
* postavičky obsahují proměnnou hasMoved
*
*   Její obrázek je Unicode charakter, který se získá funkcí getUnicode()
*
*   Pro definici možných tahů postaviček slouží funkce getPossibleMoves()
*
*/

abstract class Piece(
    val color: PieceColor,
    val shortName: String?,
    var position: String? = null,
    val value: Int
) {
    var hasMoved: Boolean = false
    abstract fun getUnicode(): String

    abstract fun getPossibleMoves(row: Int, col: Int, board: Array<Array<Piece?>>): List<Pair<Int, Int>>
}