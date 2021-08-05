package com.giphy.sdk.uidemo

import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.uidemo.databinding.GridViewExtensionsActivityBinding

class GridViewExtensionsActivity : AppCompatActivity() {

    private lateinit var binding: GridViewExtensionsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Giphy Grid for Extensions"

        binding = GridViewExtensionsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spanCountBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                binding.gifsGridView.spanCount = (seekBar?.progress ?: 1) + 1
            }
        })

        binding.paddingBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                binding.gifsGridView.cellPadding = seekBar?.progress ?: 0
            }
        })

        binding.orientationToggle.setOnCheckedChangeListener { _, isChecked ->
            binding.gifsGridView.direction = if (isChecked) androidx.recyclerview.widget.RecyclerView.HORIZONTAL else androidx.recyclerview.widget.RecyclerView.VERTICAL
        }

        binding.searchBtn.setOnClickListener {
            dismissKeyboard()
            performCustomSearch()
        }

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_NULL || actionId == EditorInfo.IME_ACTION_GO) {
                dismissKeyboard()
                performCustomSearch()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun performCustomSearch() {
        binding.gifsGridView.content = GPHContent.searchQuery(binding.searchInput.text.toString(), MediaType.gif)
    }

    fun dismissKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
    }
}
