package cn.edu.sicnu.cs.tsy.gamecard

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val profile = PlayerSession.get(this)
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val genderInput = findViewById<Spinner>(R.id.genderInput)
        val genders = resources.getStringArray(R.array.gender_options)
        genderInput.adapter = ArrayAdapter(this, R.layout.item_gender, genders).apply {
            setDropDownViewResource(R.layout.item_gender)
        }

        if (savedInstanceState == null) {
            nameInput.setText(profile.name)
            phoneInput.setText(profile.phone)
            genderInput.setSelection(genders.indexOf(profile.gender).coerceAtLeast(0))
        }

        findViewById<android.view.View>(R.id.saveButton).setOnClickListener {
            val name = nameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            when {
                name.isEmpty() -> nameInput.error = getString(R.string.name_required)
                phone.isEmpty() -> phoneInput.error = getString(R.string.phone_required)
                else -> {
                    profile.name = name
                    profile.phone = phone
                    profile.gender = genderInput.selectedItem.toString()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }
}
