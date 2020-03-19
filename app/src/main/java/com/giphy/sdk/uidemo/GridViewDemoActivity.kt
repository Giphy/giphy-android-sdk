package com.giphy.sdk.uidemo

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GPHSearchGridCallback
import com.giphy.sdk.ui.views.GifView
import com.giphy.sdk.ui.views.GiphyGridView
import kotlinx.android.synthetic.main.grid_view_demo_activity.*
import timber.log.Timber

class GridViewDemoActivity : AppCompatActivity(R.layout.grid_view_demo_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gifsGridView.direction = DemoConfig.direction
        gifsGridView.spanCount = DemoConfig.spanCount
        gifsGridView.cellPadding = DemoConfig.cellPadding
        gifsGridView.fixedSizeCells = DemoConfig.fixedSizeCells
        setTrendingQuery()
        if (DemoConfig.mediaType == MediaType.emoji) {
            searchInput.isEnabled = false
            searchBtn.isEnabled = false
        }

        if (DemoConfig.direction == GiphyGridView.HORIZONTAL) {
            // Limit height
            val constraints = ConstraintSet()
            constraints.clone(parentView)
            constraints.clear(R.id.gifsGridView, ConstraintSet.BOTTOM)
            constraints.constrainHeight(R.id.gifsGridView, 200 * DemoConfig.spanCount)
            constraints.applyTo(parentView)
        }

        gifsGridView.callback = object : GPHGridCallback {
            override fun contentDidUpdate(resultCount: Int) {
                Timber.d("contentDidUpdate $resultCount")
            }

            override fun didSelectMedia(media: Media) {
                Timber.d("didSelectMedia ${media.id}")
                Toast.makeText(
                    this@GridViewDemoActivity,
                    "media selected: ${media.id}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        gifsGridView.searchCallback = object : GPHSearchGridCallback {
            override fun didTapUsername(username: String) {
                Timber.d("didTapUsername $username")
            }

            override fun didLongPressCell(cell: GifView) {
                Timber.d("didLongPressCell")
            }

            override fun didScroll() {
                Timber.d("didScroll")
            }
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

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) = Unit

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                performCustomSearch()
            }
        })
    }

    private fun performCustomSearch() {
        if (searchInput.text.isNullOrEmpty())
            setTrendingQuery()
        else
            gifsGridView?.content =
                GPHContent.searchQuery(searchInput.text.toString(), DemoConfig.mediaType)
    }

    fun dismissKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
    }

    private fun setTrendingQuery() {
        gifsGridView.content = when (DemoConfig.mediaType) {
            MediaType.gif -> GPHContent.trendingGifs
            MediaType.sticker -> GPHContent.trendingStickers
            MediaType.text -> GPHContent.trendingText
            MediaType.emoji -> GPHContent.emoji
            else -> throw Exception("MediaType ${DemoConfig.mediaType} not supported ")
        }
    }
}
