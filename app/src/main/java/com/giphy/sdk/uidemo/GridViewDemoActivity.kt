package com.giphy.sdk.uidemo

import android.app.Activity
import android.graphics.drawable.Drawable
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
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GiphyLoadingProvider
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
        gifsGridView.showCheckeredBackground = DemoConfig.showCheckeredBackground
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

            override fun didScroll(dx: Int, dy: Int) {
                Timber.d("didScroll")
            }
        }

        searchBtn.setOnClickListener {
            dismissKeyboard()
            performCustomSearch()
        }

        gifsGridView.setGiphyLoadingProvider(loadingProviderClient)

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
        gifsGridView.content = when (DemoConfig.contentType) {
            GPHContentType.clips -> GPHContent.trendingVideos
            GPHContentType.gif -> GPHContent.trendingGifs
            GPHContentType.sticker -> GPHContent.trendingStickers
            GPHContentType.text -> GPHContent.trendingText
            GPHContentType.emoji -> GPHContent.emoji
            GPHContentType.recents -> GPHContent.recents
            else -> throw Exception("MediaType ${DemoConfig.mediaType} not supported ")
        }
    }

    private val loadingProviderClient = object : GiphyLoadingProvider {
        override fun getLoadingDrawable(position: Int): Drawable {
            return LoadingDrawable(if (position % 2 == 0) LoadingDrawable.Shape.Rect else LoadingDrawable.Shape.Circle)
        }
    }
}
