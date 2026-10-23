package cn.edu.sicnu.cs.tsy.gamecard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Random

class CardMatchingGameTest {
    @Test
    fun deckDrawsEveryCardOnce() {
        val deck = Deck(Random(42))
        val drawn = List(52) { deck.drawRandomCard()!! }

        assertEquals(52, drawn.map { it.suit to it.rank }.toSet().size)
        assertEquals(null, deck.drawRandomCard())
    }

    @Test
    fun matchingThreeRanksAwardsSixteenMinusThreeFlips() {
        val game = CardMatchingGame.restore(listOf(Card(0, 1), Card(1, 1), Card(2, 1)), 0)

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        assertEquals(-2, game.score)
        assertFalse(game.cards.any { it.isMatched })
        game.chooseCardAtIndex(2)

        assertEquals(13, game.score)
        assertTrue(game.cards.all { it.isMatched })
        assertTrue(game.isFinished)
        game.chooseCardAtIndex(0)
        assertEquals(13, game.score)
    }

    @Test
    fun matchingThreeSuitsAwardsFourMinusThreeFlips() {
        val game = CardMatchingGame.restore(listOf(Card(0, 1), Card(0, 2), Card(0, 3)), 0)

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)

        assertEquals(1, game.score)
        assertTrue(game.cards.all { it.isMatched })
    }

    @Test
    fun mismatchHidesFirstTwoAndKeepsThirdFaceUp() {
        val game = CardMatchingGame.restore(
            listOf(Card(0, 1), Card(1, 2), Card(2, 3), Card(0, 4), Card(0, 5)), 0
        )

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)

        assertEquals(-5, game.score)
        assertFalse(game.cards[0].isChosen)
        assertFalse(game.cards[1].isChosen)
        assertTrue(game.cards[2].isChosen)
        game.chooseCardAtIndex(2)
        assertFalse(game.cards[2].isChosen)
        assertEquals(-5, game.score)
    }

    @Test
    fun gameEndsOnlyWhenNoTripleRemains() {
        val noMatch = CardMatchingGame.restore(listOf(Card(0, 1), Card(0, 2), Card(1, 3)), 0)
        assertTrue(noMatch.isFinished)
        noMatch.chooseCardAtIndex(0)
        assertEquals(0, noMatch.score)

        val match = CardMatchingGame.restore(listOf(Card(0, 1), Card(0, 2), Card(0, 3)), 0)
        assertFalse(match.isFinished)
        assertEquals(0, Card(0, 1).match(listOf(Card(0, 2))))
    }

    @Test
    fun resetDealsNewCardsAndClearsScoreAndFlags() {
        val game = CardMatchingGame.restore(listOf(Card(0, 1), Card(0, 2), Card(0, 3)), 9)
        game.cards[0].isChosen = true
        val oldCards = game.cards

        game.reset(Deck(Random(42)))

        assertEquals(0, game.score)
        assertEquals(3, game.cards.size)
        assertTrue(game.cards.none { it.isChosen || it.isMatched })
        assertFalse(game.cards === oldCards)
    }

    @Test
    fun normalDeckSupportsBothScreenSizes() {
        assertEquals(24, CardMatchingGame(24).cards.size)
        assertEquals(18, CardMatchingGame(18).cards.size)
    }
}
