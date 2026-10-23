package cn.edu.sicnu.cs.tsy.gamecard

class CardMatchingGame private constructor(
    val cards: List<Card>,
    var score: Int
) {
    constructor(count: Int, deck: Deck = Deck()) : this(
        List(count.coerceIn(0, 52)) { deck.drawRandomCard()!! }, 0
    )

    companion object {
        fun restore(cards: List<Card>, score: Int): CardMatchingGame =
            CardMatchingGame(cards, score)
    }

    val isFinished: Boolean
        get() {
            val remaining = cards.filterNot { it.isMatched }
            return remaining.indices.none { first ->
                (first + 1 until remaining.size).any { second ->
                    remaining[first].match(remaining[second]) > 0
                }
            }
        }

    fun chooseCardAtIndex(index: Int) {
        val card = cards[index]
        if (card.isMatched || isFinished) return
        if (card.isChosen) {
            card.isChosen = false
            return
        }

        val other = cards.firstOrNull { it.isChosen && !it.isMatched }
        if (other != null) {
            val matchScore = card.match(other)
            if (matchScore > 0) {
                score += matchScore * 4
                other.isMatched = true
                card.isMatched = true
            } else {
                score -= 2
                other.isChosen = false
            }
        }
        score -= 1
        card.isChosen = true
    }
}
