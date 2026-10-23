package cn.edu.sicnu.cs.tsy.gamecard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class GameActivity : AppCompatActivity() {
    private lateinit var game: CardMatchingGame
    private lateinit var cardViews: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        game = restoreGame(savedInstanceState) ?: CardMatchingGame(CARD_COUNT)

        val grid = findViewById<GridLayout>(R.id.cardGrid)
        val margin = resources.getDimensionPixelSize(R.dimen.card_margin)
        cardViews = game.cards.mapIndexed { index, _ ->
            val view = LayoutInflater.from(this).inflate(R.layout.item_card, grid, false) as TextView
            val layout = GridLayout.LayoutParams().apply {
                width = 0
                height = resources.getDimensionPixelSize(R.dimen.card_height)
                columnSpec = GridLayout.spec(index % COLUMNS, 1f)
                rowSpec = GridLayout.spec(index / COLUMNS)
                setMargins(margin, margin, margin, margin)
            }
            grid.addView(view, layout)
            view.setOnClickListener {
                game.chooseCardAtIndex(index)
                val profile = PlayerSession.get(this)
                profile.highScore = maxOf(profile.highScore, game.score)
                render()
            }
            view
        }

        findViewById<View>(R.id.restartButton).setOnClickListener {
            game = CardMatchingGame(CARD_COUNT)
            render()
        }
        render()
    }

    private fun render() {
        val ranks = resources.getStringArray(R.array.card_ranks)
        val suits = resources.getStringArray(R.array.card_suits)
        game.cards.forEachIndexed { index, card ->
            val view = cardViews[index]
            val visible = card.isChosen || card.isMatched
            val face = getString(R.string.card_face, suits[card.suit], ranks[card.rank - 1])
            view.text = if (visible) face else getString(R.string.card_back_symbol)
            view.contentDescription = if (visible) {
                getString(R.string.card_revealed_description, index + 1, face)
            } else {
                getString(R.string.card_hidden_description, index + 1)
            }
            view.setBackgroundResource(if (card.isMatched) R.drawable.card_matched else if (visible) R.drawable.card_front else R.drawable.card_back)
            view.setTextColor(ContextCompat.getColor(this, if (visible) R.color.card_ink else R.color.card_back_text))
            view.isEnabled = !card.isMatched && !game.isFinished
        }
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

    private fun restoreGame(state: Bundle?): CardMatchingGame? {
        if (state == null) return null
        val suits = state.getIntArray(getString(R.string.state_suits)) ?: return null
        val ranks = state.getIntArray(getString(R.string.state_ranks)) ?: return null
        val chosen = state.getBooleanArray(getString(R.string.state_chosen)) ?: return null
        val matched = state.getBooleanArray(getString(R.string.state_matched)) ?: return null
        if (suits.size != CARD_COUNT || ranks.size != CARD_COUNT || chosen.size != CARD_COUNT || matched.size != CARD_COUNT) return null
        val cards = suits.indices.map { Card(suits[it], ranks[it], chosen[it], matched[it]) }
        return CardMatchingGame.restore(cards, state.getInt(getString(R.string.state_score)))
    }

    companion object {
        private const val CARD_COUNT = 12
        private const val COLUMNS = 3
    }
}
