package cn.edu.sicnu.cs.tsy.gamecard

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GameActivity : AppCompatActivity() {
    private lateinit var game: CardMatchingGame
    private lateinit var cardAdapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val cardCount = resources.getInteger(R.integer.game_card_count)
        game = restoreGame(savedInstanceState, cardCount) ?: CardMatchingGame(cardCount)

        cardAdapter = CardAdapter({ game.cards }, { game.isFinished }) { index ->
            game.chooseCardAtIndex(index)
            val profile = PlayerSession.get(this)
            profile.highScore = maxOf(profile.highScore, game.score)
            render()
        }
        findViewById<RecyclerView>(R.id.cardGrid).apply {
            layoutManager = GridLayoutManager(this@GameActivity, resources.getInteger(R.integer.game_column_count))
            adapter = cardAdapter
        }

        findViewById<android.view.View>(R.id.restartButton).setOnClickListener {
            game.reset()
            render()
        }
        render()
    }

    private fun render() {
        cardAdapter.notifyDataSetChanged()
        findViewById<TextView>(R.id.currentScore).text = getString(R.string.score_value, game.score)
        findViewById<TextView>(R.id.gameStatus).setText(if (game.isFinished) R.string.game_finished else R.string.game_instructions)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntArray(getString(R.string.state_suits), game.cards.map { it.suit }.toIntArray())
        outState.putIntArray(getString(R.string.state_ranks), game.cards.map { it.rank }.toIntArray())
        outState.putBooleanArray(getString(R.string.state_chosen), game.cards.map { it.isChosen }.toBooleanArray())
        outState.putBooleanArray(getString(R.string.state_matched), game.cards.map { it.isMatched }.toBooleanArray())
        outState.putInt(getString(R.string.state_score), game.score)
        super.onSaveInstanceState(outState)
    }

    private fun restoreGame(state: Bundle?, expectedCount: Int): CardMatchingGame? {
        if (state == null) return null
        val suits = state.getIntArray(getString(R.string.state_suits)) ?: return null
        val ranks = state.getIntArray(getString(R.string.state_ranks)) ?: return null
        val chosen = state.getBooleanArray(getString(R.string.state_chosen)) ?: return null
        val matched = state.getBooleanArray(getString(R.string.state_matched)) ?: return null
        if (suits.size != expectedCount || ranks.size != expectedCount || chosen.size != expectedCount || matched.size != expectedCount) return null
        val cards = suits.indices.map { Card(suits[it], ranks[it], chosen[it], matched[it]) }
        return CardMatchingGame.restore(cards, state.getInt(getString(R.string.state_score)))
    }
}
