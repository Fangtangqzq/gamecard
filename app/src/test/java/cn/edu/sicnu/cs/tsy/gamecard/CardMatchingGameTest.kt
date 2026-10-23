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
    fun matchingRankGivesSixteenPointsMinusTwoFlips() {
        val game = CardMatchingGame.restore(listOf(Card(0, 1), Card(1, 1)), 0)

        game.chooseCardAtIndex(0)
        assertEquals(-1, game.score)
        game.chooseCardAtIndex(1)

        assertEquals(14, game.score)
        assertTrue(game.cards.all { it.isMatched })
        assertTrue(game.isFinished)
        game.chooseCardAtIndex(0)
        assertEquals(14, game.score)
    }

    @Test
    fun matchingSuitGivesFourPointsMinusTwoFlips() {
        val game = CardMatchingGame.restore(listOf(Card(0, 1), Card(0, 2)), 0)

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)

        assertEquals(2, game.score)
        assertTrue(game.cards.all { it.isMatched })
    }

    @Test
    fun mismatchHidesPreviousCardAndCostsTwoMorePoints() {
        val game = CardMatchingGame.restore(listOf(Card(0, 1), Card(1, 2), Card(0, 3)), 0)

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)

        assertEquals(-4, game.score)
        assertFalse(game.cards[0].isChosen)
        assertTrue(game.cards[1].isChosen)
        assertFalse(game.isFinished)
        game.chooseCardAtIndex(1)
        assertFalse(game.cards[1].isChosen)
        assertEquals(-4, game.score)
    }

    @Test
    fun gameEndsWhenNoRemainingCardsCanMatch() {
        val game = CardMatchingGame.restore(listOf(Card(0, 1), Card(1, 2)), 0)

        assertTrue(game.isFinished)
        game.chooseCardAtIndex(0)
        assertEquals(0, game.score)
    }
}
