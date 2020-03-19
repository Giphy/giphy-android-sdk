package com.giphy.sdk.uidemo

import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GiphyGridFragment
import kotlinx.android.synthetic.main.grid_activity.*

class GridActivity : AppCompatActivity() {

    var gridFragment: GiphyGridFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Giphy Grid for Apps"

        setContentView(R.layout.grid_activity)
        gridFragment =
            supportFragmentManager.findFragmentById(R.id.gifsGridFragment) as GiphyGridFragment

        spanCountBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                gridFragment?.spanCount = (seekBar?.progress ?: 1) + 1
            }
        })

        paddingBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                gridFragment?.cellPadding = seekBar?.progress ?: 0
            }
        })

        orientationToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            gridFragment?.direction =
                if (isChecked) androidx.recyclerview.widget.RecyclerView.HORIZONTAL else androidx.recyclerview.widget.RecyclerView.VERTICAL
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
        gridFragment?.content = GPHContent.searchQuery(searchInput.text.toString(), MediaType.gif)
    }

    fun dismissKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
    }
}
