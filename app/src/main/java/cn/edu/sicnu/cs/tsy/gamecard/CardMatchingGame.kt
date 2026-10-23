package cn.edu.sicnu.cs.tsy.gamecard

class CardMatchingGame private constructor(
    initialCards: List<Card>,
    var score: Int
) {
    var cards: List<Card> = initialCards
        private set

    constructor(count: Int, deck: Deck = Deck()) : this(
        List(count.coerceIn(0, 52)) { deck.drawRandomCard()!! }, 0
    )

    companion object {
        fun restore(cards: List<Card>, score: Int): CardMatchingGame =
            CardMatchingGame(cards, score)
    }

    fun reset(deck: Deck = Deck()) {
        cards = List(cards.size) { deck.drawRandomCard()!! }
        score = 0
    }

    val isFinished: Boolean
        get() {
            val remaining = cards.filterNot { it.isMatched }
            return remaining.indices.none { first ->
                (first + 1 until remaining.size).any { second ->
                    (second + 1 until remaining.size).any { third ->
                        remaining[first].match(listOf(remaining[second], remaining[third])) > 0
                    }
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

        val chosen = cards.filter { it.isChosen && !it.isMatched }
        if (chosen.size == 2) {
            val matchScore = card.match(chosen)
            if (matchScore > 0) {
                score += matchScore * 4
                chosen.forEach { it.isMatched = true }
                card.isMatched = true
            } else {
                score -= 2
                chosen.forEach { it.isChosen = false }
            }
        }
        score -= 1
        card.isChosen = true
    }
}
