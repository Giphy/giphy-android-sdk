package com.giphy.sdk.uidemo

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.RenditionType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.GiphyCoreUI
import com.giphy.sdk.ui.themes.GridType
import com.giphy.sdk.ui.themes.LightTheme
import com.giphy.sdk.ui.views.GPHBrandButton
import com.giphy.sdk.ui.views.GPHGenericButton
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.giphy.sdk.uidemo.feed.FeedDataItem
import com.giphy.sdk.uidemo.feed.GifItem
import com.giphy.sdk.uidemo.feed.MessageFeedAdapter
import com.giphy.sdk.uidemo.feed.MessageItem
import kotlinx.android.synthetic.main.activity_demo.*

/**
 * Created by Cristian Holdunu on 27/02/2019.
 */
class DemoActivity : AppCompatActivity() {

    var settings = GPHSettings(gridType = GridType.waterfall, theme = LightTheme, dimBackground = true, showAttributeScreenOnSelection = true)

    var feedAdapter: MessageFeedAdapter? = null
    var messageItems = ArrayList<FeedDataItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GiphyCoreUI.configure(this, "oUThALwXNzrOG4b1jRyoPDtmZJmmW5HU")

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
        override fun onGifSelected(media: Media) {
            messageItems.add(GifItem(media))
            feedAdapter?.notifyItemInserted(messageItems.size - 1)        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.demo_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> showSettingsDialog()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar2)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupFeed() {
        messageItems.add(MessageItem("Hey, what are you up tonight? Wanna meet Rich and I for a dinner and a movie?"))
        feedAdapter = MessageFeedAdapter(messageItems)
        feedAdapter?.theme = settings.theme
        messageFeed.layoutManager = LinearLayoutManager(this)
        messageFeed.adapter = feedAdapter
    }

    private fun showSettingsDialog(): Boolean {
        val dialog = SettingsDialogFragment.newInstance(settings)
        dialog.dismissListener = ::applyNewSettings
        dialog.show(supportFragmentManager, "settings_dialog")
        return true
    }

    private fun applyNewSettings(settings: GPHSettings, buttonConfig: GPHButtonConfig?) {
        this.settings = settings
        feedAdapter?.theme = settings.theme
        applyTheme()

        buttonConfig?.let {
            launchGiphyBtn.removeAllViews()
            val newBtn = it.type.getConstructor(Context::class.java).newInstance(this)
            launchGiphyBtn.addView(newBtn)
            (newBtn as? GPHGenericButton)?.let { button ->
                button.style = it.gphGenericStyle!!
                button.gradient = it.gphGenericGradient!!
                button.rounded = it.rounded!!
            }
            (newBtn as? GPHBrandButton)?.let { button ->
                button.fill = it.gphBrandFill!!
                button.rounded = it.rounded!!
            }
        }
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
