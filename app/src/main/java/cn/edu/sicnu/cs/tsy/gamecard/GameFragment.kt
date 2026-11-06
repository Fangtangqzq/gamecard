package cn.edu.sicnu.cs.tsy.gamecard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class GameFragment : Fragment(R.layout.fragment_game) {
    protected abstract val matchSize: Int
    protected abstract val cardCountResource: Int
    protected abstract val columnCountResource: Int
    protected abstract val eyebrowResource: Int
    protected abstract val titleResource: Int
    protected abstract val instructionsResource: Int
    protected abstract val finishedResource: Int

    private lateinit var game: CardMatchingGame
    private lateinit var cardAdapter: CardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.gameEyebrow).setText(eyebrowResource)
        view.findViewById<TextView>(R.id.gameTitle).setText(titleResource)

        game = loadOrCreateGame()
        cardAdapter = CardAdapter({ game.cards }, { game.isFinished }) { index ->
            game.chooseCardAtIndex(index)
            persistGame()
            render()
        }
        view.findViewById<RecyclerView>(R.id.cardGrid).apply {
            layoutManager = GridLayoutManager(requireContext(), resources.getInteger(columnCountResource))
            adapter = cardAdapter
        }
        view.findViewById<View>(R.id.restartButton).setOnClickListener {
            game.reset()
            persistGame()
            render()
        }
        persistGame()
        render()
    }

    private fun loadOrCreateGame(): CardMatchingGame {
        val expectedCount = resources.getInteger(cardCountResource)
        val progress = PlayerSession.game(requireContext(), matchSize)
        val canRestore = progress.cards.size == expectedCount && progress.matchSize == matchSize
        return if (canRestore) {
            CardMatchingGame.restore(progress.cards, progress.score, matchSize)
        } else {
            CardMatchingGame(expectedCount, matchSize)
        }
    }

    private fun persistGame() {
        PlayerSession.updateGame(requireContext(), matchSize, game.cards, game.score)
    }

    private fun render() {
        cardAdapter.notifyDataSetChanged()
        requireView().findViewById<TextView>(R.id.currentScore).text =
            getString(R.string.score_value, game.score)
        requireView().findViewById<TextView>(R.id.gameBestScore).text =
            getString(R.string.score_value, PlayerSession.game(requireContext(), matchSize).bestScore)
        requireView().findViewById<TextView>(R.id.gameStatus).setText(
            if (game.isFinished) finishedResource else instructionsResource
        )
    }
}
