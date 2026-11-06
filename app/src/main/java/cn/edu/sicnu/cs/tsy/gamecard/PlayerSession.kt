package cn.edu.sicnu.cs.tsy.gamecard

import android.content.Context
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

data class PlayerProfile(
    var name: String,
    var gender: String,
    var phone: String
) : Serializable

data class GameProgress(
    var cards: List<Card> = emptyList(),
    var score: Int = 0,
    var bestScore: Int = 0,
    var matchSize: Int = 3
) : Serializable

data class SavedAppState(
    var profile: PlayerProfile,
    var twoCardGame: GameProgress = GameProgress(matchSize = 2),
    var threeCardGame: GameProgress = GameProgress(matchSize = 3)
) : Serializable

object PlayerSession {
    private const val FILE_NAME = "player_state.dat"

    @Volatile
    private var state: SavedAppState? = null

    fun get(context: Context): SavedAppState = state ?: load(context)

    fun profile(context: Context): PlayerProfile = get(context).profile

    fun game(context: Context, matchSize: Int): GameProgress =
        if (matchSize == 2) get(context).twoCardGame else get(context).threeCardGame

    fun updateProfile(context: Context, name: String, gender: String, phone: String) {
        get(context).profile.apply {
            this.name = name
            this.gender = gender
            this.phone = phone
        }
        save(context)
    }

    fun updateGame(context: Context, matchSize: Int, cards: List<Card>, score: Int) {
        game(context, matchSize).apply {
            this.cards = cards.map { it.copy() }
            this.score = score
            this.bestScore = maxOf(bestScore, score)
            this.matchSize = matchSize
        }
        save(context)
    }

    @Synchronized
    fun save(context: Context) {
        val current = state ?: return
        context.applicationContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use { file ->
            ObjectOutputStream(file).use { output ->
                output.writeObject(current)
            }
        }
    }

    @Synchronized
    fun clearMemoryForTests() {
        state = null
    }

    @Synchronized
    fun clearSavedStateForTests(context: Context) {
        state = null
        context.applicationContext.deleteFile(FILE_NAME)
    }

    private fun load(context: Context): SavedAppState {
        val appContext = context.applicationContext
        val loaded = runCatching {
            appContext.openFileInput(FILE_NAME).use { file ->
                ObjectInputStream(file).use { input ->
                    input.readObject() as? SavedAppState
                }
            }
        }.getOrNull()

        return (loaded ?: SavedAppState(
            profile = PlayerProfile(
            context.getString(R.string.default_name),
            context.getString(R.string.default_gender),
            context.getString(R.string.default_phone)
            )
        )).also { state = it }
    }
}
