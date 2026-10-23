package cn.edu.sicnu.cs.tsy.gamecard

import android.content.Context

data class PlayerProfile(
    var name: String,
    var gender: String,
    var phone: String,
    var highScore: Int = 0
)

object PlayerSession {
    private var profile: PlayerProfile? = null

    fun get(context: Context): PlayerProfile {
        return profile ?: PlayerProfile(
            context.getString(R.string.default_name),
            context.getString(R.string.default_gender),
            context.getString(R.string.default_phone)
        ).also { profile = it }
    }
}
