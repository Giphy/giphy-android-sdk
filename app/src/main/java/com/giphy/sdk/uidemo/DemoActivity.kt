package com.giphy.sdk.uidemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.tracking.isVideo
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.themes.GridType
import com.giphy.sdk.ui.utils.videoUrl
import com.giphy.sdk.ui.views.GPHVideoPlayer
import com.giphy.sdk.ui.views.GPHVideoPlayerState
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.giphy.sdk.uidemo.VideoPlayer.VideoCache
import com.giphy.sdk.uidemo.VideoPlayer.VideoPlayer
import com.giphy.sdk.uidemo.VideoPlayer.VideoPlayerState
import com.giphy.sdk.uidemo.feed.*
import com.giphy.sdk.uidemo.databinding.ActivityDemoBinding
import timber.log.Timber

/**
 * Created by Cristian Holdunu on 27/02/2019.
 */
class DemoActivity : AppCompatActivity() {

    companion object {
        val TAG = DemoActivity::class.java.simpleName
        val INVALID_KEY = "NOT_A_VALID_KEY"
    }
    private lateinit var binding: ActivityDemoBinding
    var settings = GPHSettings(gridType = GridType.waterfall, theme = GPHTheme.Light, stickerColumnCount = 3)
    var feedAdapter: MessageFeedAdapter? = null
    var messageItems = ArrayList<FeedDataItem>()
    var contentType = GPHContentType.gif

    //TODO: Set a valid API KEY
    val YOUR_API_KEY = INVALID_KEY

    val player: VideoPlayer = createVideoPlayer()
    private var clipsPlaybackSetting = SettingsDialogFragment.ClipsPlaybackSetting.inline

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Giphy.configure(this, YOUR_API_KEY, true)
        VideoCache.initialize(this, 100 * 1024 * 1024)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupFeed()

        binding.launchGiphyBtn.setOnClickListener {
            player.onPause()
            val dialog = GiphyDialogFragment.newInstance(settings.copy(selectedContentType = contentType))
            dialog.gifSelectionListener = getGifSelectionListener()
            dialog.show(supportFragmentManager, "gifs_dialog")
        }
    }

    override fun onDestroy() {
        player.onDestroy()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        player.onPause()
    }

    override fun onResume() {
        super.onResume()
        player.onResume()
    }

    private fun getGifSelectionListener() = object : GiphyDialogFragment.GifSelectionListener {
        override fun onGifSelected(media: Media, searchTerm: String?, selectedContentType: GPHContentType) {
            Timber.d(TAG, "onGifSelected")
            if (selectedContentType == GPHContentType.clips && media.isVideo) {
                messageItems.add(ClipItem(media, Author.Me))
            } else {
                messageItems.add(GifItem(media, Author.Me))
            }
            feedAdapter?.notifyItemInserted(messageItems.size - 1)
            contentType = selectedContentType
        }

        override fun onDismissed(selectedContentType: GPHContentType) {
            Timber.d(TAG, "onDismissed")
            contentType = selectedContentType
        }

        override fun didSearchTerm(term: String) {
            Timber.d(TAG, "didSearchTerm $term")
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
        feedAdapter?.itemSelectedListener = ::onGifSelected
        feedAdapter?.adapterHelper?.player = player
        feedAdapter?.adapterHelper?.clipsPlaybackSetting = clipsPlaybackSetting

        binding.messageFeed.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.messageFeed.adapter = feedAdapter
    }

    private fun createVideoPlayer(): VideoPlayer {
        val player = VideoPlayer(null, true)
        player.addListener { playerState ->
            when (playerState) {
                is VideoPlayerState.MediaChanged -> {
                    val position = messageItems.map {
                        if (it is ClipItem) {
                            return@map it.media
                        }
                        return@map null
                    }.indexOfFirst {
                        it?.videoUrl == playerState.mediaUrl
                    }
                    if (position > -1) {
                        binding.messageFeed.smoothScrollToPosition(position)
                    }
                }
                else -> return@addListener
            }
        }
        return player
    }

    private fun onGifSelected(itemData: FeedDataItem) {
        if (itemData is MessageItem) {
            Timber.d("onItemSelected ${itemData.text}")
        } else if (itemData is InvalidKeyItem) {
            Timber.d("onItemSelected InvalidKeyItem")
        } else if (itemData is GifItem) {
            Timber.d("onItemSelected ${itemData.media}")
        } else if (itemData is ClipItem) {
            Timber.d("onItemSelected ${itemData.media}")
            showVideoPlayerDialog(itemData.media)
        }
    }

    private fun showSettingsDialog(): Boolean {
        val dialog = SettingsDialogFragment.newInstance(settings, clipsPlaybackSetting)
        dialog.dismissListener = ::applyNewSettings
        dialog.show(supportFragmentManager, "settings_dialog")
        return true
    }

    private fun showVideoPlayerDialog(media: Media): Boolean {
        val dialog = ClipDialogFragment.newInstance(media)
        dialog.show(supportFragmentManager, "video_player_dialog")
        return true
    }

    private fun applyNewSettings(settings: GPHSettings, clipsPlaybackSetting: SettingsDialogFragment.ClipsPlaybackSetting) {
        this.settings = settings
        this.clipsPlaybackSetting = clipsPlaybackSetting
        feedAdapter?.adapterHelper?.clipsPlaybackSetting = clipsPlaybackSetting
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)
    }
}
