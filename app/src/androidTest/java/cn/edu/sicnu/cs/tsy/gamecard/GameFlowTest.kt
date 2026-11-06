package cn.edu.sicnu.cs.tsy.gamecard

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameFlowTest {
    private lateinit var context: Context

    @Before
    fun clearSavedState() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        PlayerSession.clearSavedStateForTests(context)
    }

    @Test
    fun editProfileUpdatesProfileFragmentAndFile() {
        val gender = context.resources.getStringArray(R.array.gender_options)[1]
        val name = context.getString(R.string.test_player_name)
        val phone = context.getString(R.string.test_player_phone)

        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.navProfile)).perform(click())
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

        PlayerSession.clearMemoryForTests()
        val restored = PlayerSession.profile(context)
        assertEquals(name, restored.name)
        assertEquals(phone, restored.phone)
        assertEquals(gender, restored.gender)
    }

    @Test
    fun twoCardFragmentFlipsAndRestartsTheGame() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.navTwoCard)).perform(click())
            onView(withId(R.id.gameTitle)).check(matches(withText(R.string.two_card_game_title)))
            onView(withId(R.id.currentScore)).check(matches(withText(context.getString(R.string.score_value, 0))))
            onView(withId(R.id.cardGrid)).check { view, _ ->
                assertEquals(16, (view as RecyclerView).adapter?.itemCount)
            }
            onView(withContentDescription(context.getString(R.string.card_hidden_description, 1))).perform(click())
            onView(withId(R.id.currentScore)).check(matches(withText(context.getString(R.string.score_value, -1))))
            onView(withId(R.id.restartButton)).perform(click())
            onView(withId(R.id.currentScore)).check(matches(withText(context.getString(R.string.score_value, 0))))
        }
    }

    @Test
    fun threeCardFragmentUsesTwentyFourCards() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.navThreeCard)).perform(click())
            onView(withId(R.id.gameTitle)).check(matches(withText(R.string.three_card_game_title)))
            onView(withId(R.id.cardGrid)).check { view, _ ->
                assertEquals(24, (view as RecyclerView).adapter?.itemCount)
            }
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
    fun orientationChangesTheCurrentGameCardCount() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.navThreeCard)).perform(click())
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
    fun gameProgressSurvivesMemoryReset() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.navTwoCard)).perform(click())
            onView(withContentDescription(context.getString(R.string.card_hidden_description, 1))).perform(click())
            onView(withId(R.id.currentScore)).check(matches(withText(context.getString(R.string.score_value, -1))))
        }

        PlayerSession.clearMemoryForTests()
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.navTwoCard)).perform(click())
            onView(withId(R.id.currentScore)).check(matches(withText(context.getString(R.string.score_value, -1))))
            onView(withId(R.id.cardGrid)).check { view, _ ->
                assertEquals(16, (view as RecyclerView).adapter?.itemCount)
            }
        }
    }
}
