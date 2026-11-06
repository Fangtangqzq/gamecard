package cn.edu.sicnu.cs.tsy.gamecard

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PlayerSession.get(this)

        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            showFragment(item.itemId)
            true
        }

        val selectedItem = savedInstanceState?.getInt(STATE_SELECTED_ITEM)
            ?: R.id.navProfile
        if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) == null) {
            showFragment(selectedItem)
        }
        bottomNavigation.selectedItemId = selectedItem

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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_SELECTED_ITEM, bottomNavigation.selectedItemId)
        super.onSaveInstanceState(outState)
    }

    private fun showFragment(itemId: Int) {
        val fragment: Fragment = when (itemId) {
            R.id.navTwoCard -> TwoCardGameFragment()
            R.id.navThreeCard -> ThreeCardGameFragment()
            else -> ProfileFragment()
        }
        val current = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (current != null && current::class.java == fragment::class.java) return
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commitNow()
    }

    private companion object {
        const val STATE_SELECTED_ITEM = "selected_navigation_item"
    }
}
