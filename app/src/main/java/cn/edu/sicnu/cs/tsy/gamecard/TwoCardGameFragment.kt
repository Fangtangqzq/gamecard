package cn.edu.sicnu.cs.tsy.gamecard

class TwoCardGameFragment : GameFragment() {
    override val matchSize = 2
    override val cardCountResource = R.integer.two_card_count
    override val columnCountResource = R.integer.two_card_column_count
    override val eyebrowResource = R.string.two_card_game_eyebrow
    override val titleResource = R.string.two_card_game_title
    override val instructionsResource = R.string.two_card_game_instructions
    override val finishedResource = R.string.two_card_game_finished
}
