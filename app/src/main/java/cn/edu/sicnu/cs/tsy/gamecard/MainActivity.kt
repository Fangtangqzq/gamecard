package cn.edu.sicnu.cs.tsy.gamecard

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PlayerSession.get(this)

        findViewById<android.view.View>(R.id.editProfileButton).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        findViewById<android.view.View>(R.id.playButton).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val profile = PlayerSession.get(this)
        findViewById<TextView>(R.id.profileName).text = profile.name
        findViewById<TextView>(R.id.profileGender).text = profile.gender
        findViewById<TextView>(R.id.profilePhone).text = profile.phone
        findViewById<TextView>(R.id.bestScore).text = getString(R.string.score_value, profile.highScore)
    }
}
