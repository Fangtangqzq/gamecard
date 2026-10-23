package cn.edu.sicnu.cs.tsy.gamecard

import android.content.pm.ActivityInfo
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
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals

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
            onView(withId(R.id.cardGrid)).check { view, _ ->
                assertEquals(24, (view as RecyclerView).adapter?.itemCount)
            }
            onView(withContentDescription(context.getString(R.string.card_hidden_description, 1))).perform(click())
            onView(withId(R.id.currentScore)).check(matches(withText(context.getString(R.string.score_value, -1))))
            onView(withId(R.id.restartButton)).perform(click())
            onView(withId(R.id.currentScore)).check(matches(withText(context.getString(R.string.score_value, 0))))
            onView(withContentDescription(context.getString(R.string.card_hidden_description, 1)))
                .check(matches(withText(R.string.card_back_symbol)))
        }
    }

    @Test
    fun aboutMenuDisplaysDialog() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.menuButton)).perform(click())
            onView(withText(R.string.about_title)).perform(click())
            onView(withText(R.string.about_message)).check(matches(isDisplayed()))
            onView(withText(R.string.confirm)).perform(click())
        }
    }

    @Test
    fun orientationChangesTheCardCount() {
        ActivityScenario.launch(GameActivity::class.java).use { scenario ->
            onView(withId(R.id.cardGrid)).check { view, _ ->
                assertEquals(24, (view as RecyclerView).adapter?.itemCount)
            }
            scenario.onActivity { activity ->
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            onView(withId(R.id.cardGrid)).check { view, _ ->
                assertEquals(18, (view as RecyclerView).adapter?.itemCount)
            }
            scenario.onActivity { activity ->
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

    @Test
    fun recreatingInSameOrientationPreservesScore() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        ActivityScenario.launch(GameActivity::class.java).use { scenario ->
            onView(withContentDescription(context.getString(R.string.card_hidden_description, 1))).perform(click())
            scenario.recreate()
            onView(withId(R.id.currentScore)).check(matches(withText(context.getString(R.string.score_value, -1))))
            onView(withId(R.id.cardGrid)).check { view, _ ->
                assertEquals(24, (view as RecyclerView).adapter?.itemCount)
            }
        }
    }
}
