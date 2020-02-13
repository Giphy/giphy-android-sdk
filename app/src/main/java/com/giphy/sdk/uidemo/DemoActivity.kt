package com.giphy.sdk.uidemo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.themes.GridType
import com.giphy.sdk.ui.themes.LightTheme
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.giphy.sdk.uidemo.feed.Author
import com.giphy.sdk.uidemo.feed.FeedDataItem
import com.giphy.sdk.uidemo.feed.GifItem
import com.giphy.sdk.uidemo.feed.InvalidKeyItem
import com.giphy.sdk.uidemo.feed.MessageFeedAdapter
import com.giphy.sdk.uidemo.feed.MessageItem
import kotlinx.android.synthetic.main.activity_demo.*

/**
 * Created by Cristian Holdunu on 27/02/2019.
 */
class DemoActivity : AppCompatActivity() {

    companion object {
        val TAG = DemoActivity::class.java.simpleName
        val INVALID_KEY = "NOT_A_VALID_KEY"
    }

    var settings = GPHSettings(gridType = GridType.waterfall, theme = LightTheme)
    var feedAdapter: MessageFeedAdapter? = null
    var messageItems = ArrayList<FeedDataItem>()

    //TODO: Set a valid API KEY
    val YOUR_API_KEY = INVALID_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Giphy.configure(this, YOUR_API_KEY, true)

        setContentView(R.layout.activity_demo)
        setupToolbar()
        setupFeed()
        applyTheme()

        launchGiphyBtn.setOnClickListener {
            val dialog = GiphyDialogFragment.newInstance(settings)
            dialog.gifSelectionListener = getGifSelectionListener()
            dialog.show(supportFragmentManager, "gifs_dialog")
        }

        emojiBtn.setOnClickListener {
            val dialog = GiphyDialogFragment.newInstance(settings.copy(gridType = GridType.emoji))
            dialog.gifSelectionListener = getGifSelectionListener()
            dialog.show(supportFragmentManager, "emoji_dialog")
        }
    }

    private fun getGifSelectionListener() = object : GiphyDialogFragment.GifSelectionListener {
        override fun onDismissed() {
            Log.d(TAG, "onDismissed")
        }

        override fun onGifSelected(media: Media) {
            Log.d(TAG, "onGifSelected")
            messageItems.add(GifItem(media, Author.Me))
            feedAdapter?.notifyItemInserted(messageItems.size - 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.demo_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> showSettingsDialog()
            R.id.action_grid -> openGridViewDemo()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar2)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }


    private fun openGridViewDemo(): Boolean {
        val intent = Intent(this, GridViewSetupActivity::class.java)
        startActivity(intent)
        return true
    }

    private fun setupFeed() {
        messageItems.add(
            MessageItem(
                "Hi there! The SDK is perfect for many contexts, including messaging, reactions, stories and other camera features. This is one example of how the GIPHY SDK can be used in a messaging app.",
                Author.GifBot
            )
        )
        messageItems.add(
            MessageItem(
                "Tap the GIPHY button in the bottom left to see the SDK in action. Tap the settings icon in the top right to try out all of the customization options.",
                Author.GifBot
            )
        )
        if (YOUR_API_KEY == INVALID_KEY) {
            messageItems.add(InvalidKeyItem(Author.GifBot))
        }
        feedAdapter = MessageFeedAdapter(messageItems)
        feedAdapter?.theme = settings.theme
        messageFeed.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        messageFeed.adapter = feedAdapter
    }

    private fun showSettingsDialog(): Boolean {
        val dialog = SettingsDialogFragment.newInstance(settings)
        dialog.dismissListener = ::applyNewSettings
        dialog.show(supportFragmentManager, "settings_dialog")
        return true
    }

    private fun applyNewSettings(settings: GPHSettings) {
        this.settings = settings
        feedAdapter?.theme = settings.theme
        applyTheme()
    }

    private fun applyTheme() {
        toolbar2.setTitleTextColor(toolbarTextColor)
        toolbar2.setBackgroundColor(toolbarBgColor)
        contentView.setBackgroundColor(feedBgColor)
        ViewCompat.setElevation(toolbar2, 10f)
        feedAdapter?.notifyDataSetChanged()
        composeContainer.setBackgroundResource(if (settings.theme == LightTheme) R.drawable.input_background_light else R.drawable.input_background_dark)
    }

    private val toolbarTextColor: Int
        get() {
            return if (settings.theme == LightTheme) Color.DKGRAY else Color.WHITE
        }

    private val toolbarBgColor: Int
        get() {
            return if (settings.theme == LightTheme) Color.WHITE else Color.DKGRAY
        }

    private val feedBgColor: Int
        get() {
            return if (settings.theme == LightTheme) 0xfff3f3f3.toInt() else Color.BLACK
        }
}
