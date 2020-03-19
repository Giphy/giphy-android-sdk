package com.giphy.sdk.uidemo

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.giphy.sdk.core.models.enums.MediaType
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
                R.id.mediaGif -> DemoConfig.mediaType = MediaType.gif
                R.id.mediaStickers -> DemoConfig.mediaType = MediaType.sticker
                R.id.mediaText -> DemoConfig.mediaType = MediaType.text
                R.id.mediaEmoji -> DemoConfig.mediaType = MediaType.emoji
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

        launchGrid.setOnClickListener {
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
