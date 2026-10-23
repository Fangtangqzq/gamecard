package cn.edu.sicnu.cs.tsy.gamecard

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PlayerSession.get(this)

        val menuButton = findViewById<android.view.View>(R.id.menuButton)
        menuButton.setOnClickListener {
            PopupMenu(this, menuButton).apply {
                menuInflater.inflate(R.menu.main_menu, menu)
                setOnMenuItemClickListener { item ->
                    if (item.itemId == R.id.menuAbout) {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle(R.string.about_title)
                            .setMessage(R.string.about_message)
                            .setPositiveButton(R.string.confirm, null)
                            .show()
                        true
                    } else {
                        false
                    }
                }
                show()
            }
        }

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
