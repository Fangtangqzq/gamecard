package cn.edu.sicnu.cs.tsy.gamecard

class ThreeCardGameFragment : GameFragment() {
    override val matchSize = 3
    override val cardCountResource = R.integer.three_card_count
    override val columnCountResource = R.integer.three_card_column_count
    override val eyebrowResource = R.string.three_card_game_eyebrow
    override val titleResource = R.string.three_card_game_title
    override val instructionsResource = R.string.three_card_game_instructions
    override val finishedResource = R.string.three_card_game_finished
}
