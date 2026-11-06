package cn.edu.sicnu.cs.tsy.gamecard

class CardMatchingGame private constructor(
    initialCards: List<Card>,
    var score: Int,
    val matchSize: Int
) {
    init {
        require(matchSize in 2..3) { "matchSize must be 2 or 3" }
    }

    var cards: List<Card> = initialCards
        private set

    constructor(count: Int, matchSize: Int = 3, deck: Deck = Deck()) : this(
        List(count.coerceIn(0, 52)) { deck.drawRandomCard()!! }, 0, matchSize
    )

    companion object {
        fun restore(cards: List<Card>, score: Int, matchSize: Int = 3): CardMatchingGame =
            CardMatchingGame(cards, score, matchSize)
    }

    fun reset(deck: Deck = Deck()) {
        cards = List(cards.size) { deck.drawRandomCard()!! }
        score = 0
    }

    val isFinished: Boolean
        get() {
            val remaining = cards.filterNot { it.isMatched }
            if (remaining.size < matchSize) return true
            return when (matchSize) {
                2 -> remaining.indices.none { first ->
                    (first + 1 until remaining.size).any { second ->
                        remaining[first].match(listOf(remaining[second])) > 0
                    }
                }
                else -> remaining.indices.none { first ->
                    (first + 1 until remaining.size).any { second ->
                        (second + 1 until remaining.size).any { third ->
                            remaining[first].match(listOf(remaining[second], remaining[third])) > 0
                        }
                    }
                }
            }
        }

    fun chooseCardAtIndex(index: Int) {
        if (index !in cards.indices) return
        val card = cards[index]
        if (card.isMatched || isFinished) return
        if (card.isChosen) {
            card.isChosen = false
            return
        }

        val chosen = cards.filter { it.isChosen && !it.isMatched }
        if (chosen.size == matchSize - 1) {
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
