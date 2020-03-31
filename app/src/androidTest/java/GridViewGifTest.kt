package com.giphy.sdk.uidemo

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.giphy.sdk.analytics.GiphyPingbacks
import com.giphy.sdk.analytics.models.enums.ActionType
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.universallist.SmartGifViewHolder
import com.giphy.sdk.uidemo.PingbacksTestInfo.verificationTag
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class GridViewGifTest {

    lateinit var scenario: ActivityScenario<GridViewDemoActivity>

    @Before
    fun init() {
        Giphy.configure(getApplicationContext(), "2lXlMi0Z2GRnYpVi8xZeNOLhT1nMMZzM", true)
        DemoConfig.spanCount = 2
        DemoConfig.cellPadding = 20
    }

    @Test
    fun testGifSearch() {
        DemoConfig.mediaType = MediaType.gif
        val intent = Intent(getApplicationContext(), GridViewDemoActivity::class.java)
        scenario = launchActivity(intent)
        onView(withId(R.id.searchInput)).perform(typeText(verificationTag + "gif"), closeSoftKeyboard())
        Thread.sleep(PingbacksTestInfo.feedLoadingDelay)
        onView(withId(R.id.gifsRecycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition<SmartGifViewHolder>(
                        0,
                        ViewActions.click()
                )
        )
        GiphyPingbacks.flush()
        Thread.sleep(PingbacksTestInfo.pingbacksProcessingDelay)

        val searchActions = PingbacksTestInfo.checkTestResponse()
        Assert.assertTrue(searchActions.count { it.actionType == ActionType.SEEN } > 1)
        Assert.assertTrue(searchActions.count { it.actionType == ActionType.SENT } > 1)
        scenario.close()
    }

    @Test
    fun testStickers() {
        DemoConfig.mediaType = MediaType.sticker
        val intent = Intent(getApplicationContext(), GridViewDemoActivity::class.java)
        scenario = launchActivity(intent)
        onView(withId(R.id.searchInput)).perform(typeText(verificationTag + "sticker"), closeSoftKeyboard())
        Thread.sleep(PingbacksTestInfo.feedLoadingDelay)
        onView(withId(R.id.gifsRecycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition<SmartGifViewHolder>(
                        0,
                        ViewActions.click()
                )
        )
        GiphyPingbacks.flush()
        Thread.sleep(PingbacksTestInfo.pingbacksProcessingDelay)

        val searchActions = PingbacksTestInfo.checkTestResponse()
        Assert.assertTrue(searchActions.count { it.actionType == ActionType.SEEN } > 1)
        Assert.assertTrue(searchActions.count { it.actionType == ActionType.SENT } > 1)
        scenario.close()
    }

    @Test
    fun testText() {
        DemoConfig.mediaType = MediaType.text
        val intent = Intent(getApplicationContext(), GridViewDemoActivity::class.java)
        scenario = launchActivity(intent)
        onView(withId(R.id.searchInput)).perform(typeText(verificationTag + "text"), closeSoftKeyboard())
        Thread.sleep(PingbacksTestInfo.feedLoadingDelay)
        onView(withId(R.id.gifsRecycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition<SmartGifViewHolder>(
                        0,
                        ViewActions.click()
                )
        )
        GiphyPingbacks.flush()
        Thread.sleep(PingbacksTestInfo.pingbacksProcessingDelay)

        val searchActions = PingbacksTestInfo.checkTestResponse()
        Assert.assertTrue(searchActions.count { it.actionType == ActionType.SEEN } > 1)
        Assert.assertTrue(searchActions.count { it.actionType == ActionType.SENT } > 1)
        scenario.close()
    }
}
