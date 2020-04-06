package com.giphy.sdk.uidemo

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.Giphy
import kotlinx.android.synthetic.main.grid_view_activity.*
import kotlinx.android.synthetic.main.grid_view_activity.orientationToggle
import kotlinx.android.synthetic.main.grid_view_activity.paddingBar
import kotlinx.android.synthetic.main.grid_view_activity.spanCountBar

class GridViewSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Giphy Grid for Applications"

        setContentView(R.layout.grid_view_activity)

        spanCountBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                DemoConfig.spanCount = (seekBar?.progress ?: 1) + 1
                displayConfig()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        paddingBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                DemoConfig.cellPadding = seekBar?.progress ?: 0
                displayConfig()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        mediaTypeContainer.setOnCheckedChangeListener { radioGroup, id ->
            when (id) {
                R.id.mediaGif -> DemoConfig.contentType = GPHContentType.gif
                R.id.mediaStickers -> DemoConfig.contentType = GPHContentType.sticker
                R.id.mediaText -> DemoConfig.contentType = GPHContentType.text
                R.id.mediaEmoji -> DemoConfig.contentType = GPHContentType.emoji
                R.id.mediaRecents -> DemoConfig.contentType = GPHContentType.recents
            }
            when (id) {
                R.id.mediaStickers,
                R.id.mediaText -> {
                    fixedSizeCells.isEnabled = true
                }
                else -> {
                    DemoConfig.fixedSizeCells = false
                    fixedSizeCells.isEnabled = false
                    fixedSizeCells.isChecked = false
                }
            }
        }

        orientationToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                DemoConfig.direction = androidx.recyclerview.widget.RecyclerView.HORIZONTAL
                spanCountBar.max = 1
                spanCountBar.progress = 0
            } else {
                DemoConfig.direction = androidx.recyclerview.widget.RecyclerView.VERTICAL
                spanCountBar.max = 4
                spanCountBar.progress = 1
            }
            spanCountBar.invalidate()
        }

        fixedSizeCells.setOnCheckedChangeListener { _, value ->
            DemoConfig.fixedSizeCells = value
        }

        showCheckeredBackground.setOnCheckedChangeListener { _, value ->
            DemoConfig.showCheckeredBackground = value
        }

        launchGrid.setOnClickListener {
            if (DemoConfig.contentType == GPHContentType.recents && Giphy.recents.count == 0) {
                Toast.makeText(applicationContext, "No recent GIFs found. Select other media type to click them.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, GridViewDemoActivity::class.java)
            startActivity(intent)
        }
        spanCountBar.progress = DemoConfig.spanCount
        paddingBar.progress = DemoConfig.cellPadding
        displayConfig()
    }

    private fun displayConfig() {
        spanCountView.text = DemoConfig.spanCount.toString()
        paddingView.text = DemoConfig.cellPadding.toString()
    }
}
