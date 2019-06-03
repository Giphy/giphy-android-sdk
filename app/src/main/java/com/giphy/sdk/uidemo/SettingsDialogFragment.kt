package com.giphy.sdk.uidemo

import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.giphy.sdk.core.models.enums.RenditionType
import com.giphy.sdk.ui.GPHMediaTypeConfiguration
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.themes.DarkTheme
import com.giphy.sdk.ui.themes.GridType
import com.giphy.sdk.ui.themes.LightTheme
import com.giphy.sdk.ui.views.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.giphy_button_holder.view.*

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

        val lightTab = themeSelector.newTab()
        lightTab.text = "Light"
        val darkTab = themeSelector.newTab()
        darkTab.text = "Dark"

        themeSelector.addTab(lightTab, settings.theme == LightTheme)
        themeSelector.addTab(darkTab, settings.theme == DarkTheme)

        val waterfalTab = layoutSelector.newTab()
        waterfalTab.text = "Waterfal"
        val carouselTab = layoutSelector.newTab()
        carouselTab.text = "Carousel"
//        val emojiTab = layoutSelector.newTab()
//        emojiTab.text = "Emoji"

        layoutSelector.addTab(waterfalTab, settings.gridType == GridType.waterfall)
        layoutSelector.addTab(carouselTab, settings.gridType == GridType.carousel)
//        layoutSelector.addTab(emojiTab, settings.gridType == GridType.emoji)

        val allMediaTab = mediaSelector.newTab()
        allMediaTab.text = "Gifs & Stickers"
        val gifsTab = mediaSelector.newTab()
        gifsTab.text = "GIFs"
        val stickersTab = mediaSelector.newTab()
        stickersTab.text = "Stickers"
        val textTab = mediaSelector.newTab()
        textTab.text = "Text"
        mediaSelector.addTab(allMediaTab, settings.mediaTypeConfig == GPHMediaTypeConfiguration.gifsAndStickers)
        mediaSelector.addTab(gifsTab, settings.mediaTypeConfig == GPHMediaTypeConfiguration.gifsOnly)
        mediaSelector.addTab(stickersTab, settings.mediaTypeConfig == GPHMediaTypeConfiguration.stickersOnly)
        mediaSelector.addTab(textTab, settings.mediaTypeConfig == GPHMediaTypeConfiguration.textOnly)

        applyTheme()
        setupIconsList()

        dimBackgroundCheck.isChecked = settings.dimBackground
        showAttributionCheck.isChecked = settings.showAttributeScreenOnSelection

        themeSelector.addOnTabSelectedListener(getThemeSelectorListener())
        dismissBtn.setOnClickListener { dismiss() }
        gridRenditionType.setOnClickListener { openRenditionPicker(PICK_GRID_RENDITION) }
        attributionRenditionType.setOnClickListener { openRenditionPicker(PICK_ATTRIBUTION_RENDTION) }
    }

    private fun applyTheme() {
        themeTitle.setTextColor(settings.theme.textColor)
        layoutTitle.setTextColor(settings.theme.textColor)
        mediaTitle.setTextColor(settings.theme.textColor)
        dismissBtn.setColorFilter(settings.theme.textColor)
        mainView.setBackgroundColor(settings.theme.backgroundColor)
        applyThemeToSelector(themeSelector)
        applyThemeToSelector(layoutSelector)
        applyThemeToSelector(mediaSelector)
        iconSelector.setBackgroundColor(if (settings.theme == LightTheme) lightIconsBackground else darkIconsBackground)
        dimBackgroundCheck.setTextColor(settings.theme.textColor)
        showAttributionCheck.setTextColor(settings.theme.textColor)
    }

    private fun applyThemeToSelector(selector: TabLayout) {
        selector.setSelectedTabIndicatorColor(settings.theme.activeTextColor)
        selector.setTabTextColors(settings.theme.textColor, settings.theme.activeTextColor)
    }

    private fun getThemeSelectorListener() = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
            Log.d("sdasd", "Asdasd")
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            Log.d("sdasd", "Asdasd")
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            settings.theme = if (themeSelector.selectedTabPosition == 0) LightTheme else DarkTheme
            applyTheme()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        settings.gridType = when (layoutSelector.selectedTabPosition) {
            0 -> GridType.waterfall
            1 -> GridType.carousel
            2 -> GridType.emoji
            else -> GridType.waterfall
        }
        settings.mediaTypeConfig = when (mediaSelector.selectedTabPosition) {
            0 -> GPHMediaTypeConfiguration.gifsAndStickers
            1 -> GPHMediaTypeConfiguration.gifsOnly
            2 -> GPHMediaTypeConfiguration.stickersOnly
            3 -> GPHMediaTypeConfiguration.textOnly
            else -> GPHMediaTypeConfiguration.gifsAndStickers
        }

        settings.showAttributeScreenOnSelection = showAttributionCheck.isChecked
        settings.dimBackground = dimBackgroundCheck.isChecked
        dismissListener(settings, gphButtonConfig)
        super.onDismiss(dialog)
    }

    private fun setupIconsList() {
        iconSelector.layoutManager = GridLayoutManager(context, 3)
        iconSelector.adapter = GiphyButtonAdapter()
    }

    private fun openRenditionPicker(renditionPlace: Int) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(if (renditionPlace == PICK_GRID_RENDITION) "Pick Grid Rendition" else "Pick Attribution Rendition")
        val renditions = RenditionType.values().map { it.name }.toTypedArray()
        builder.setItems(renditions) { dialog, which ->
            val renditionType = RenditionType.values().find { it.ordinal == which }
            if (renditionPlace == PICK_GRID_RENDITION) {
                settings.gridRenditionType = renditionType
            } else {
                settings.attributionRenditionType = renditionType
            }
        }

        val dialog = builder.create()
        dialog.show()

    }

    inner class GiphyButtonAdapter : RecyclerView.Adapter<GiphyButtonViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): GiphyButtonViewHolder {
            return GiphyButtonViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.giphy_button_holder, p0, false))
        }

        override fun getItemCount(): Int {
            return GiphyButtonStore.itemCount
        }

        override fun onBindViewHolder(p0: GiphyButtonViewHolder, p1: Int) {
            var button: View? = null
            when (val buttonType = GiphyButtonStore.getButtonType(p1)) {
                ButtonItems.branded -> {
                    button = GPHBrandButton(p0.itemView.context)
                    button.rounded = p1 == 1
                    button.fill = brandButtonFills[p1 % brandButtonFills.size]
                    p0.itemView.buttonContainer.addView(button)
                }
                ButtonItems.generic, ButtonItems.genericRounded -> {
                    button = GPHGenericButton(p0.itemView.context)
                    button.style = genericButtonStyles[p1 % genericButtonStyles.size]
                    button.gradient = genericButtonGradients[GiphyButtonStore.getGradientIndex(buttonType, p1)]
                    button.rounded = buttonType == ButtonItems.genericRounded
                    p0.itemView.buttonContainer.addView(button)
                }
            }
            p0.itemView.setOnClickListener {
                gphButtonConfig = GPHButtonConfig(button.javaClass)
                (button as? GPHBrandButton)?.let { brandButton ->
                    gphButtonConfig?.gphBrandFill = brandButton.fill
                    gphButtonConfig?.rounded = brandButton.rounded
                }
                (button as? GPHGenericButton)?.let { genericButton ->
                    gphButtonConfig?.gphGenericGradient = genericButton.gradient
                    gphButtonConfig?.rounded = genericButton.rounded
                    gphButtonConfig?.gphGenericStyle = genericButton.style
                }
                dismiss()
            }
        }

        override fun onViewRecycled(holder: GiphyButtonViewHolder) {
            holder.itemView.buttonContainer.removeAllViews()
        }

    }

    class GiphyButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val brandButtonFills: Array<GPHBrandButtonFill>
        get() {
            return arrayOf(GPHBrandButtonFill.color, GPHBrandButtonFill.color, if (settings.theme == DarkTheme) GPHBrandButtonFill.white else GPHBrandButtonFill.black)
        }

    private val genericButtonStyles: Array<GPHGenericButtonStyle>
        get() {
            return GPHGenericButtonStyle.values()
        }

    private val genericButtonGradients: Array<GPHGenericButtonGradient>
        get() {
            return arrayOf(GPHGenericButtonGradient.blue, GPHGenericButtonGradient.pink, if (settings.theme == DarkTheme) GPHGenericButtonGradient.white else GPHGenericButtonGradient.black)
        }
}