package cn.edu.sicnu.cs.tsy.gamecard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.editProfileButton).setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (isAdded) refreshProfile()
    }

    private fun refreshProfile() {
        val appState = PlayerSession.get(requireContext())
        val profile = appState.profile
        requireView().findViewById<TextView>(R.id.profileName).text = profile.name
        requireView().findViewById<TextView>(R.id.profileGender).text = profile.gender
        requireView().findViewById<TextView>(R.id.profilePhone).text = profile.phone
        requireView().findViewById<TextView>(R.id.bestScoreTwo).text =
            getString(R.string.score_value, appState.twoCardGame.bestScore)
        requireView().findViewById<TextView>(R.id.bestScoreThree).text =
            getString(R.string.score_value, appState.threeCardGame.bestScore)
    }
}
