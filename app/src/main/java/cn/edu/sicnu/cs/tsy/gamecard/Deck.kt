package cn.edu.sicnu.cs.tsy.gamecard

import java.util.Random

class Deck(private val random: Random = Random()) {
    private val cards = (0 until 4).flatMap { suit ->
        (1..13).map { rank -> Card(suit, rank) }
    }.toMutableList()

    fun drawRandomCard(): Card? = if (cards.isEmpty()) {
        null
    } else {
        cards.removeAt(random.nextInt(cards.size))
    }
}
