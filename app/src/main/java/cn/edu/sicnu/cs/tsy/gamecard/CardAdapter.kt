package cn.edu.sicnu.cs.tsy.gamecard

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(
    private val cards: () -> List<Card>,
    private val isFinished: () -> Boolean,
    private val onCardClick: (Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(val cardView: TextView) : RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false) as TextView
        return CardViewHolder(view)
    }

    override fun getItemCount(): Int = cards().size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards()[position]
        val view = holder.cardView
        val context = view.context
        val visible = card.isChosen || card.isMatched
        val suits = context.resources.getStringArray(R.array.card_suits)
        val ranks = context.resources.getStringArray(R.array.card_ranks)
        val face = context.getString(R.string.card_face, suits[card.suit], ranks[card.rank - 1])
        view.text = if (visible) face else context.getString(R.string.card_back_symbol)
        view.contentDescription = if (visible) {
            context.getString(R.string.card_revealed_description, position + 1, face)
        } else {
            context.getString(R.string.card_hidden_description, position + 1)
        }
        view.setBackgroundResource(if (card.isMatched) R.drawable.card_matched else if (visible) R.drawable.card_front else R.drawable.card_back)
        view.setTextColor(ContextCompat.getColor(context, if (visible) R.color.card_ink else R.color.card_back_text))
        view.isEnabled = !card.isMatched && !isFinished()
        view.setOnClickListener {
            val index = holder.bindingAdapterPosition
            if (index != RecyclerView.NO_POSITION) onCardClick(index)
        }
    }
}
