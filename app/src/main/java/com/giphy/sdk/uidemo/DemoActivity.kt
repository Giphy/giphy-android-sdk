package com.giphy.sdk.uidemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.themes.GridType
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.giphy.sdk.uidemo.feed.Author
import com.giphy.sdk.uidemo.feed.FeedDataItem
import com.giphy.sdk.uidemo.feed.GifItem
import com.giphy.sdk.uidemo.feed.InvalidKeyItem
import com.giphy.sdk.uidemo.feed.MessageFeedAdapter
import com.giphy.sdk.uidemo.feed.MessageItem
import kotlinx.android.synthetic.main.activity_demo.*
import timber.log.Timber

/**
 * Created by Cristian Holdunu on 27/02/2019.
 */
class DemoActivity : AppCompatActivity() {

    companion object {
        val TAG = DemoActivity::class.java.simpleName
        val INVALID_KEY = "NOT_A_VALID_KEY"
    }

    var settings = GPHSettings(gridType = GridType.waterfall, useBlurredBackground = false, theme = GPHTheme.Light, stickerColumnCount = 3)
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

        launchGiphyBtn.setOnClickListener {
            val dialog = GiphyDialogFragment.newInstance(settings)
            dialog.gifSelectionListener = getGifSelectionListener()
            dialog.show(supportFragmentManager, "gifs_dialog")
        }
    }

    private fun getGifSelectionListener() = object : GiphyDialogFragment.GifSelectionListener {
        override fun onGifSelected(
            media: Media,
            searchTerm: String?,
            selectedContentType: GPHContentType
        ) {
            Log.d(TAG, "onGifSelected")
            messageItems.add(GifItem(media, Author.Me))
            feedAdapter?.notifyItemInserted(messageItems.size - 1) }

        override fun onDismissed(selectedContentType: GPHContentType) {
            Log.d(TAG, "onDismissed")
        }

        override fun didSearchTerm(term: String) {
            Log.d(TAG, "didSearchTerm $term")
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
            R.id.action_grid_view -> openGridViewExtensionsDemo()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun openGridViewDemo(): Boolean {
        val intent = Intent(this, GridViewSetupActivity::class.java)
        startActivity(intent)
        return true
    }

    private fun openGridViewExtensionsDemo(): Boolean {
        val intent = Intent(this, GridViewExtensionsActivity::class.java)
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)
    }
}
