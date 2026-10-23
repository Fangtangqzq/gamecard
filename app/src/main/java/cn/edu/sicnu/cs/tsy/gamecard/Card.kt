package cn.edu.sicnu.cs.tsy.gamecard

data class Card(
    val suit: Int,
    val rank: Int,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) {
    fun match(other: Card): Int = when {
        rank == other.rank -> 4
        suit == other.suit -> 1
        else -> 0
    }
}
