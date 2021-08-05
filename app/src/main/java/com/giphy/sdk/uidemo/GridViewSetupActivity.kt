package com.giphy.sdk.uidemo

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.uidemo.databinding.GridViewActivityBinding

class GridViewSetupActivity : AppCompatActivity() {

    private lateinit var binding: GridViewActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Giphy Grid for Applications"

        binding = GridViewActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spanCountBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                DemoConfig.spanCount = (seekBar?.progress ?: 1) + 1
                displayConfig()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        binding.paddingBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                DemoConfig.cellPadding = seekBar?.progress ?: 0
                displayConfig()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        binding.mediaTypeContainer.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.mediaClips -> {
                    DemoConfig.contentType = GPHContentType.clips
                    DemoConfig.mediaType = MediaType.video
                }
                R.id.mediaGif -> {
                    DemoConfig.contentType = GPHContentType.gif
                    DemoConfig.mediaType = MediaType.gif
                }
                R.id.mediaStickers -> {
                    DemoConfig.contentType = GPHContentType.sticker
                    DemoConfig.mediaType = MediaType.sticker
                }
                R.id.mediaText -> {
                    DemoConfig.contentType = GPHContentType.text
                    DemoConfig.mediaType = MediaType.text
                }
                R.id.mediaEmoji -> {
                    DemoConfig.contentType = GPHContentType.emoji
                    DemoConfig.mediaType = MediaType.emoji
                }
                R.id.mediaRecents -> {
                    DemoConfig.contentType = GPHContentType.recents
                    DemoConfig.mediaType = MediaType.gif
                }

            }
            when (id) {
                R.id.mediaStickers,
                R.id.mediaText -> {
                    binding.fixedSizeCells.isEnabled = true
                }
                else -> {
                    DemoConfig.fixedSizeCells = false
                    binding.fixedSizeCells.isEnabled = false
                    binding.fixedSizeCells.isChecked = false
                }
            }
        }

        binding.orientationToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                DemoConfig.direction = androidx.recyclerview.widget.RecyclerView.HORIZONTAL
                binding.spanCountBar.max = 1
                binding.spanCountBar.progress = 0
            } else {
                DemoConfig.direction = androidx.recyclerview.widget.RecyclerView.VERTICAL
                binding.spanCountBar.max = 4
                binding.spanCountBar.progress = 1
            }
            binding.spanCountBar.invalidate()
        }

        binding.fixedSizeCells.setOnCheckedChangeListener { _, value ->
            DemoConfig.fixedSizeCells = value
        }

        binding.showCheckeredBackground.setOnCheckedChangeListener { _, value ->
            DemoConfig.showCheckeredBackground = value
        }

        binding.launchGrid.setOnClickListener {
            if (DemoConfig.contentType == GPHContentType.recents && Giphy.recents.count == 0) {
                Toast.makeText(applicationContext, "No recent GIFs found. Select other media type to click them.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, GridViewDemoActivity::class.java)
            startActivity(intent)
        }
        binding.spanCountBar.progress = DemoConfig.spanCount
        binding.paddingBar.progress = DemoConfig.cellPadding
        displayConfig()
    }

    private fun displayConfig() {
        binding.apply {
            spanCountView.text = DemoConfig.spanCount.toString()
            paddingView.text = DemoConfig.cellPadding.toString()
            when (DemoConfig.contentType) {
                GPHContentType.clips -> mediaClips.isChecked = true
                GPHContentType.gif -> mediaGif.isChecked = true
                GPHContentType.sticker -> mediaStickers.isChecked = true
                GPHContentType.text -> mediaText.isChecked = true
                GPHContentType.emoji -> mediaEmoji.isChecked = true
                GPHContentType.recents -> mediaRecents.isChecked = true
            }
            fixedSizeCells.isChecked = DemoConfig.fixedSizeCells
            showCheckeredBackground.isChecked = DemoConfig.showCheckeredBackground
        }
    }
}
