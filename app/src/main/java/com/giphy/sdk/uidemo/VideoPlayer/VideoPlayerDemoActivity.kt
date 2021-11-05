package com.giphy.sdk.uidemo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.tracking.isVideo
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.utils.videoUrl
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.giphy.sdk.uidemo.VideoPlayer.VideoCache
import com.giphy.sdk.uidemo.VideoPlayer.VideoPlayer
import com.giphy.sdk.uidemo.VideoPlayer.VideoPlayerState
import com.giphy.sdk.uidemo.feed.*
import com.giphy.sdk.uidemo.databinding.ActivityVideoPlayerDemoBinding
import timber.log.Timber

class VideoPlayerDemoActivity : AppCompatActivity() {

    companion object {
        val TAG = DemoActivity::class.java.simpleName
        val INVALID_KEY = "NOT_A_VALID_KEY"
    }
    private lateinit var binding: ActivityVideoPlayerDemoBinding
    var settings = GPHSettings(mediaTypeConfig = arrayOf(GPHContentType.clips))
    var feedAdapter: VideoPlayerMessageFeedAdapter? = null
    var messageItems = ArrayList<FeedDataItem>()

    // TODO: Set a valid API KEY
    val YOUR_API_KEY = INVALID_KEY

    val player: VideoPlayer = createVideoPlayer()
    private var videoPlaybackSetting = VideoPlayerSettingsDialogFragment.VideoPlaybackSetting.inline

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Giphy.configure(this, YOUR_API_KEY, true)
        VideoCache.initialize(this, 100 * 1024 * 1024)
        binding = ActivityVideoPlayerDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupFeed()

        binding.launchGiphyBtn.setOnClickListener {
            player.onPause()
            val dialog = GiphyDialogFragment.newInstance(settings)
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
                messageItems.forEach {
                    (it as? ClipItem)?.autoPlay = false
                }
                messageItems.add(ClipItem(media, Author.Me, autoPlay = true))
            }
            feedAdapter?.notifyItemInserted(messageItems.size - 1)
        }

        override fun onDismissed(selectedContentType: GPHContentType) {
            Timber.d(TAG, "onDismissed")
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
        if (YOUR_API_KEY == INVALID_KEY) {
            messageItems.add(InvalidKeyItem(Author.GifBot))
        }
        feedAdapter = VideoPlayerMessageFeedAdapter(messageItems)
        feedAdapter?.itemSelectedListener = ::onGifSelected
        feedAdapter?.adapterHelper?.player = player
        feedAdapter?.adapterHelper?.videoPlaybackSetting = videoPlaybackSetting

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
        val dialog = VideoPlayerSettingsDialogFragment.newInstance(settings, videoPlaybackSetting)
        dialog.dismissListener = ::applyNewSettings
        dialog.show(supportFragmentManager, "settings_dialog")
        return true
    }

    private fun showVideoPlayerDialog(media: Media): Boolean {
        val dialog = ClipDialogFragment.newInstance(media)
        dialog.show(supportFragmentManager, "video_player_dialog")
        return true
    }

    private fun applyNewSettings(settings: GPHSettings, videoPlaybackSetting: VideoPlayerSettingsDialogFragment.VideoPlaybackSetting) {
        this.settings = settings
        this.videoPlaybackSetting = videoPlaybackSetting
        feedAdapter?.adapterHelper?.videoPlaybackSetting = videoPlaybackSetting
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)
    }
}
