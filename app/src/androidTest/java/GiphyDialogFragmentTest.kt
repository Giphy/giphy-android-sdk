package com.giphy.sdk.uidemo

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.giphy.sdk.analytics.GiphyPingbacks
import com.giphy.sdk.analytics.batching.PingbackCollector
import com.giphy.sdk.analytics.models.enums.ActionType
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.universallist.SmartGifViewHolder
import com.giphy.sdk.uidemo.PingbacksTestInfo.checkTestResponse
import com.giphy.sdk.uidemo.PingbacksTestInfo.feedLoadingDelay
import com.giphy.sdk.uidemo.PingbacksTestInfo.pingbacksProcessingDelay
import com.giphy.sdk.uidemo.PingbacksTestInfo.verificationTag
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class GiphyDialogFragmentTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<DemoActivity>()

    @Before
    fun init() {
        Giphy.configure(ApplicationProvider.getApplicationContext(), "XJmGmSLo34Jk8XXDKJLm6cPE2qL9T2Pq", true)
        PingbackCollector.addPingbackDelay = 100
    }

    @Test
    fun testGifs() {
        onView(withId(R.id.launchGiphyBtn)).perform(click())
        onView(withId(R.id.searchInput)).perform(
                typeText(verificationTag + "gif"),
                closeSoftKeyboard()
        )
        Thread.sleep(feedLoadingDelay)
        onView(withId(com.giphy.sdk.ui.R.id.gifRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition<SmartGifViewHolder>(
                        0,
                        click()
                )
        )
        Thread.sleep(pingbacksProcessingDelay)

        val searchActions = checkTestResponse()
        Assert.assertTrue(searchActions.count { it.actionType == ActionType.SEEN } > 1)
        Assert.assertTrue(searchActions.count { it.actionType == ActionType.SENT } > 1)
    }

    @Test
    fun testStickers() {
        onView(withId(R.id.launchGiphyBtn)).perform(click())
        onView(withText("Stickers")).perform(click())
        onView(withId(R.id.searchInput)).perform(
                typeText(verificationTag + "sticker"),
                closeSoftKeyboard()
        )
        Thread.sleep(feedLoadingDelay)
        onView(withId(com.giphy.sdk.ui.R.id.gifRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition<SmartGifViewHolder>(
                        0,
                        click()
                )
        )
        Thread.sleep(pingbacksProcessingDelay)

        val searchActions = checkTestResponse()
        Assert.assertTrue(searchActions.count { it.actionType == ActionType.SEEN } > 1)
        Assert.assertTrue(searchActions.count { it.actionType == ActionType.SENT } > 1) }

    @Test
    fun testText() {
        onView(withId(R.id.launchGiphyBtn)).perform(click())
        onView(withText("Text")).perform(click())
        onView(withId(R.id.searchInput)).perform(
                typeText(verificationTag + "text"),
                closeSoftKeyboard()
        )
        Thread.sleep(feedLoadingDelay)
        onView(withId(com.giphy.sdk.ui.R.id.gifRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition<SmartGifViewHolder>(
                        0,
                        click()
                )
        )
        GiphyPingbacks.flush()
        Thread.sleep(pingbacksProcessingDelay)

        val searchActions = checkTestResponse()
        Assert.assertTrue(searchActions.count { it.actionType == ActionType.SEEN } > 1)
        Assert.assertTrue(searchActions.count { it.actionType == ActionType.SENT } > 1) }
}
