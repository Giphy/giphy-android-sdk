package com.giphy.sdk.uidemo

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.giphy.sdk.core.models.enums.RenditionType
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.themes.DarkTheme
import com.giphy.sdk.ui.themes.GridType
import com.giphy.sdk.ui.themes.LightTheme
import com.giphy.sdk.ui.views.buttons.*
import com.savvyapps.togglebuttonlayout.ToggleButtonLayout
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * Created by Cristian Holdunu on 13/03/2019.
 */
class SettingsDialogFragment : DialogFragment() {

    private var settings: GPHSettings = GPHSettings()
    var dismissListener: (GPHSettings, GPHButtonConfig?) -> Unit = { settings, config -> }

    private var lightIconsBackground = 0xFFE9E9E9.toInt()
    private var darkIconsBackground = 0xFF242424.toInt()

    private var gphButtonConfig: GPHButtonConfig? = null

    companion object {
        private const val PICK_GRID_RENDITION = 201
        private const val PICK_ATTRIBUTION_RENDTION = 202

        private val KEY_SETTINGS = "key_settings"
        fun newInstance(gphSettings: GPHSettings): SettingsDialogFragment {
            val fragment = SettingsDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_SETTINGS, gphSettings)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getTheme(): Int {
        return R.style.SettingsDialogStyle
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settings = arguments!!.getParcelable(KEY_SETTINGS)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        themeSelector.setToggled(if (settings.theme == LightTheme) R.id.lightTheme else R.id.darkTheme, true)
        layoutSelector.setToggled(if (settings.gridType == GridType.waterfall) R.id.waterfall else R.id.carousel, true)
        mediaTypeSelector.inflateMenu(if (settings.gridType == GridType.waterfall) R.menu.waterfal_media_types else R.menu.carousel_media_types)
        settings.mediaTypeConfig.forEach {
            val id = when (it) {
                GPHContentType.gif -> R.id.typeGif
                GPHContentType.sticker -> R.id.typeStickers
                GPHContentType.text -> R.id.typeText
                GPHContentType.emoji -> R.id.typeEmoji
            }
            mediaTypeSelector.setToggled(id, true)
        }

        layoutSelector.onToggledListener = { toggle, selected ->
            (mediaTypeSelector.getChildAt(0) as LinearLayout).removeAllViews()
            mediaTypeSelector.toggles.clear()
            if (toggle.id == R.id.carousel) {
                mediaTypeSelector.multipleSelection = false
                mediaTypeSelector.inflateMenu(R.menu.carousel_media_types)
                mediaTypeSelector.setToggled(R.id.typeGif, true)
            } else {
                mediaTypeSelector.multipleSelection = true
                mediaTypeSelector.inflateMenu(R.menu.waterfal_media_types)
                mediaTypeSelector.toggles.forEach {
                    mediaTypeSelector.setToggled(it.id, true)
                }
            }
        }

        applyTheme()


        dimBackgroundCheck.isChecked = settings.dimBackground
        showAttributionCheck.isChecked = settings.showAttribution
        showConfirmationScreen.isChecked = settings.showConfirmationScreen

        themeSelector.onToggledListener = { toggle, selected ->
            settings.theme = if (toggle.id == R.id.lightTheme) LightTheme else DarkTheme
            applyTheme()
        }
        dismissBtn.setOnClickListener { dismiss() }
        gridRenditionType.setOnClickListener { openRenditionPicker(PICK_GRID_RENDITION) }
        attributionRenditionType.setOnClickListener { openRenditionPicker(PICK_ATTRIBUTION_RENDTION) }
    }

    private fun applyTheme() {
        themeTitle.setTextColor(settings.theme.textColor)
        layoutTitle.setTextColor(settings.theme.textColor)
        gifTitle.setTextColor(settings.theme.textColor)
        dismissBtn.setColorFilter(settings.theme.textColor)
        mainView.setBackgroundColor(settings.theme.backgroundColor)
        applyTheme(themeSelector)
        applyTheme(layoutSelector)
        applyTheme(mediaTypeSelector)
//        iconSelector.setBackgroundColor(if (settings.theme == LightTheme) lightIconsBackground else darkIconsBackground)
        dimBackgroundCheck.setTextColor(settings.theme.textColor)
        showAttributionCheck.setTextColor(settings.theme.textColor)
        showConfirmationScreen.setTextColor(settings.theme.textColor)
        setupIconsList()
    }

    private fun applyTheme(toggle: ToggleButtonLayout) {
        toggle.selectedColor = settings.theme.activeTextColor
        toggle.dividerColor = settings.theme.textColor
        toggle.setCardBackgroundColor(Color.LTGRAY)

    }

    override fun onDismiss(dialog: DialogInterface?) {
        settings.gridType = when (layoutSelector.selectedToggles().firstOrNull()?.id) {
            R.id.waterfall -> GridType.waterfall
            R.id.carousel -> GridType.carousel
            else -> GridType.waterfall
        }
        val contentTypes = ArrayList<GPHContentType>()
        mediaTypeSelector.selectedToggles().forEach {
            when (it.id) {
                R.id.typeGif -> contentTypes.add(GPHContentType.gif)
                R.id.typeStickers -> contentTypes.add(GPHContentType.sticker)
                R.id.typeText -> contentTypes.add(GPHContentType.text)
                R.id.typeEmoji -> contentTypes.add(GPHContentType.emoji)
            }
        }

        settings.mediaTypeConfig = contentTypes.toTypedArray()
        settings.showAttribution = showAttributionCheck.isChecked
        settings.showConfirmationScreen = showConfirmationScreen.isChecked
        settings.dimBackground = dimBackgroundCheck.isChecked
        dismissListener(settings, gphButtonConfig)
        super.onDismiss(dialog)
    }

    private fun setupIconsList() {
        setupIcons()
        setupLogo()
        setupGifHardCorners()
        setupGifRoundedCorners()
        setupText()
        setupMultiContent()
    }

    private fun setupIcons() {
        btnIconContainer.removeAllViews()
        btnIconContainer.setBackgroundColor(themeBackgroundColor)
        val iconTypes = arrayOf(GPHGiphyButtonStyle.iconSquareRounded, GPHGiphyButtonStyle.iconSquare, if (settings.theme == LightTheme) GPHGiphyButtonStyle.iconBlack else GPHGiphyButtonStyle.iconWhite)
        iconTypes.forEach {
            btnIconContainer.addView(getGridWrapper(GPHGiphyButton(context).apply {
                style = it
            }))
        }
    }


    private fun setupLogo() {
        btnLogoContainer.removeAllViews()
        btnLogoContainer.setBackgroundColor(themeBackgroundColor)
        val iconTypes = arrayOf(GPHGiphyButtonStyle.logo, GPHGiphyButtonStyle.logoRounded)
        iconTypes.forEach {
            btnLogoContainer.addView(getGridWrapper(GPHGiphyButton(context).apply {
                style = it
            }))
        }
    }

    private fun setupGifHardCorners() {
        btnGifHardContainer.removeAllViews()
        btnGifHardContainer.setBackgroundColor(themeBackgroundColor)
        val iconTypes = arrayOf(GPHGifButtonStyle.rectangle,
                GPHGifButtonStyle.rectangleOutline,
                GPHGifButtonStyle.square,
                GPHGifButtonStyle.squareOutline)
        GPHGifButtonColor.getThemeColors(settings.theme).forEach { color ->
            iconTypes.forEach {
                btnGifHardContainer.addView(getGridWrapper(GPHGifButton(context).apply {
                    style = it
                    this.color = color
                }))
            }
        }
    }

    private fun setupGifRoundedCorners() {
        btnGifRoundedContainer.removeAllViews()
        btnGifRoundedContainer.setBackgroundColor(themeBackgroundColor)

        val iconTypes = arrayOf(GPHGifButtonStyle.rectangleRounded,
                GPHGifButtonStyle.rectangleOutlineRounded,
                GPHGifButtonStyle.squareRounded,
                GPHGifButtonStyle.squareOutlineRounded)
        GPHGifButtonColor.getThemeColors(settings.theme).forEach { color ->
            iconTypes.forEach {
                btnGifRoundedContainer.addView(getGridWrapper(GPHGifButton(context).apply {
                    style = it
                    this.color = color
                }))
            }
        }
    }

    private fun setupText() {
        btnGifTextContainer.removeAllViews()
        btnGifTextContainer.setBackgroundColor(themeBackgroundColor)

        GPHGifButtonColor.values().forEach { color ->
            btnGifTextContainer.addView(getGridWrapper(GPHGifButton(context).apply {
                style = GPHGifButtonStyle.text
                this.color = color
            }))
        }
    }

    private fun setupMultiContent() {
        btnContentContainer.removeAllViews()
        btnContentContainer.setBackgroundColor(themeBackgroundColor)

        GPHGifButtonColor.getThemeColors(settings.theme).forEach { color ->
            GPHContentTypeButtonStyle.values().forEach {
                btnContentContainer.addView(getGridWrapper(GPHContentTypeButton(context).apply {
                    style = it
                    this.color = color
                }))
            }
        }
    }

    private fun getGridWrapper(view: View): View {
        val wrapper = FrameLayout(context)
        wrapper.addView(view, FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        })
        val params = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f))
        params.height = 200
        wrapper.layoutParams = params

        wrapper.setOnClickListener {
            gphButtonConfig = GPHButtonConfig(view.javaClass)
            when(view) {
                is GPHGifButton-> {
                    gphButtonConfig?.gifButtonStyle = view.style
                    gphButtonConfig?.color = view.color
                }
                is GPHGiphyButton->{
                    gphButtonConfig?.brandButtonStyle = view.style
                }
                is GPHContentTypeButton->{
                    gphButtonConfig?.contentTypeStyle = view.style
                    gphButtonConfig?.color = view.color
                }
            }
        }
        return wrapper
    }

    private fun openRenditionPicker(renditionPlace: Int) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(if (renditionPlace == PICK_GRID_RENDITION) "Pick Grid Rendition" else "Pick Attribution Rendition")
        val renditions = RenditionType.values().map { it.name }.toTypedArray()
        builder.setItems(renditions) { dialog, which ->
            val renditionType = RenditionType.values().find { it.ordinal == which }
            if (renditionPlace == PICK_GRID_RENDITION) {
                settings.renditionType = renditionType
            } else {
                settings.confirmationRenditionType = renditionType
            }
        }

        val dialog = builder.create()
        dialog.show()

    }

    private val themeBackgroundColor: Int
        get() {
            return if (settings.theme == LightTheme) lightIconsBackground else darkIconsBackground
        }
}