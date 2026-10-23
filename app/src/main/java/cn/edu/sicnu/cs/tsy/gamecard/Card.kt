package cn.edu.sicnu.cs.tsy.gamecard

data class Card(
    val suit: Int,
    val rank: Int,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) {
    fun match(others: List<Card>): Int = when {
        others.size != 2 -> 0
        others.all { it.rank == rank } -> 4
        others.all { it.suit == suit } -> 1
        else -> 0
    }
}
