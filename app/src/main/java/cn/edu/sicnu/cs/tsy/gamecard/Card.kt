package cn.edu.sicnu.cs.tsy.gamecard

import java.io.Serializable

data class Card(
    val suit: Int,
    val rank: Int,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) : Serializable {
    fun match(others: List<Card>): Int = when {
        others.isEmpty() -> 0
        others.all { it.rank == rank } -> 4
        others.all { it.suit == suit } -> 1
        else -> 0
    }
}
