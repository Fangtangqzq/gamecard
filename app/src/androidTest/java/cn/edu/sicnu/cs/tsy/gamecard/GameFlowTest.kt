package cn.edu.sicnu.cs.tsy.gamecard

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.equalTo

@RunWith(AndroidJUnit4::class)
class GameFlowTest {
    @Test
    fun editProfileUpdatesHomeScreen() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val gender = context.resources.getStringArray(R.array.gender_options)[1]
        val name = context.getString(R.string.test_player_name)
        val phone = context.getString(R.string.test_player_phone)
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.editProfileButton)).perform(scrollTo(), click())
            onView(withId(R.id.nameInput)).perform(replaceText(name), closeSoftKeyboard())
            onView(withId(R.id.phoneInput)).perform(replaceText(phone), closeSoftKeyboard())
            onView(withId(R.id.genderInput)).perform(click())
            onData(equalTo(gender)).perform(click())
            onView(withId(R.id.saveButton)).perform(click())
            onView(withId(R.id.profileName)).check(matches(withText(name)))
            onView(withId(R.id.profilePhone)).check(matches(withText(phone)))
            onView(withId(R.id.profileGender)).check(matches(withText(gender)))
        }
    }

    @Test
    fun flippingAndRestartingResetTheScoreAndCards() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.playButton)).perform(click())
            onView(withId(R.id.currentScore)).check(matches(withText(context.getString(R.string.score_value, 0))))
            onView(withId(R.id.cardGrid)).check(matches(hasChildCount(12)))
            onView(withContentDescription(context.getString(R.string.card_hidden_description, 1))).perform(click())
            onView(withId(R.id.currentScore)).check(matches(withText(context.getString(R.string.score_value, -1))))
            onView(withId(R.id.restartButton)).perform(scrollTo(), click())
            onView(withId(R.id.currentScore)).check(matches(withText(context.getString(R.string.score_value, 0))))
            onView(withContentDescription(context.getString(R.string.card_hidden_description, 1)))
                .check(matches(withText(R.string.card_back_symbol)))
        }
    }
}
