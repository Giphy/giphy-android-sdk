package com.giphy.sdk.uidemo

import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.pagination.GPHContent
import kotlinx.android.synthetic.main.grid_view_extensions_activity.*

class GridViewExtensionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Giphy Grid for Extensions"
//        GiphyCoreUI.configure(this, "oUThALwXNzrOG4b1jRyoPDtmZJmmW5HU")

        setContentView(R.layout.grid_view_extensions_activity)

        spanCountBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                gifsGridView?.spanCount = (seekBar?.progress ?: 1) + 1
            }
        })

        paddingBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                gifsGridView?.cellPadding = seekBar?.progress ?: 0
            }
        })

        orientationToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            gifsGridView?.direction = if (isChecked) androidx.recyclerview.widget.RecyclerView.HORIZONTAL else androidx.recyclerview.widget.RecyclerView.VERTICAL
        }

        searchBtn.setOnClickListener {
            dismissKeyboard()
            performCustomSearch()
        }

        searchInput.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_NULL || actionId == EditorInfo.IME_ACTION_GO) {
                dismissKeyboard()
                performCustomSearch()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun performCustomSearch() {
        gifsGridView?.content = GPHContent.searchQuery(searchInput.text.toString(), MediaType.gif)
    }

    fun dismissKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
    }
}
