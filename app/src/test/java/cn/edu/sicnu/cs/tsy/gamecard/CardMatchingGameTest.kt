package cn.edu.sicnu.cs.tsy.gamecard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
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
    fun matchingTwoRanksAwardsSixteenMinusTwoFlips() {
        val game = CardMatchingGame.restore(listOf(Card(0, 1), Card(1, 1)), 0, 2)

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)

        assertEquals(14, game.score)
        assertTrue(game.cards.all { it.isMatched })
        assertTrue(game.isFinished)
    }

    @Test
    fun matchingTwoSuitsAwardsFourMinusTwoFlips() {
        val game = CardMatchingGame.restore(listOf(Card(0, 1), Card(0, 2)), 0, 2)

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)

        assertEquals(2, game.score)
        assertTrue(game.cards.all { it.isMatched })
    }

    @Test
    fun twoCardMismatchKeepsSecondCardChosen() {
        val game = CardMatchingGame.restore(
            listOf(Card(0, 1), Card(1, 2), Card(2, 2)), 0, 2
        )

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)

        assertEquals(-4, game.score)
        assertFalse(game.cards[0].isChosen)
        assertTrue(game.cards[1].isChosen)
    }

    @Test
    fun matchingThreeRanksAwardsSixteenMinusThreeFlips() {
        val game = CardMatchingGame.restore(listOf(Card(0, 1), Card(1, 1), Card(2, 1)), 0, 3)

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
        val game = CardMatchingGame.restore(listOf(Card(0, 1), Card(0, 2), Card(0, 3)), 0, 3)

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)

        assertEquals(1, game.score)
        assertTrue(game.cards.all { it.isMatched })
    }

    @Test
    fun threeCardMismatchHidesFirstTwoAndKeepsThirdFaceUp() {
        val game = CardMatchingGame.restore(
            listOf(Card(0, 1), Card(1, 2), Card(2, 3), Card(0, 4), Card(0, 5)), 0, 3
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
    fun gameEndStateDependsOnMatchSize() {
        val noPair = CardMatchingGame.restore(listOf(Card(0, 1), Card(1, 2)), 0, 2)
        assertTrue(noPair.isFinished)

        val pair = CardMatchingGame.restore(listOf(Card(0, 1), Card(0, 2)), 0, 2)
        assertFalse(pair.isFinished)

        val noTriple = CardMatchingGame.restore(listOf(Card(0, 1), Card(0, 2), Card(1, 3)), 0, 3)
        assertTrue(noTriple.isFinished)

        val triple = CardMatchingGame.restore(listOf(Card(0, 1), Card(0, 2), Card(0, 3)), 0, 3)
        assertFalse(triple.isFinished)
    }

    @Test
    fun resetDealsNewCardsAndPreservesMatchSize() {
        val game = CardMatchingGame.restore(listOf(Card(0, 1), Card(0, 2)), 9, 2)
        game.cards[0].isChosen = true
        val oldCards = game.cards

        game.reset(Deck(Random(42)))

        assertEquals(0, game.score)
        assertEquals(2, game.matchSize)
        assertEquals(2, game.cards.size)
        assertTrue(game.cards.none { it.isChosen || it.isMatched })
        assertFalse(game.cards === oldCards)
    }

    @Test
    fun normalDecksSupportBothGamesAndScreenSizes() {
        assertEquals(16, CardMatchingGame(16, 2).cards.size)
        assertEquals(20, CardMatchingGame(20, 2).cards.size)
        assertEquals(24, CardMatchingGame(24, 3).cards.size)
        assertEquals(18, CardMatchingGame(18, 3).cards.size)
    }

    @Test
    fun savedStateSurvivesSerializationRoundTrip() {
        val original = SavedAppState(
            profile = PlayerProfile("测试玩家", "男", "13800138000"),
            twoCardGame = GameProgress(
                cards = listOf(Card(0, 1, isChosen = true), Card(1, 1)),
                score = 14,
                bestScore = 20,
                matchSize = 2
            ),
            threeCardGame = GameProgress(
                cards = listOf(Card(2, 5, isMatched = true), Card(3, 5, isMatched = true), Card(0, 5, isMatched = true)),
                score = 13,
                bestScore = 30,
                matchSize = 3
            )
        )

        val bytes = ByteArrayOutputStream().use { buffer ->
            ObjectOutputStream(buffer).use { it.writeObject(original) }
            buffer.toByteArray()
        }
        val restored = ObjectInputStream(ByteArrayInputStream(bytes)).use {
            it.readObject() as SavedAppState
        }

        assertEquals(original.profile, restored.profile)
        assertEquals(14, restored.twoCardGame.score)
        assertEquals(20, restored.twoCardGame.bestScore)
        assertTrue(restored.twoCardGame.cards[0].isChosen)
        assertEquals(3, restored.threeCardGame.cards.size)
        assertTrue(restored.threeCardGame.cards.all { it.isMatched })
    }
}
