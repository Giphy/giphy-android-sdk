package com.giphy.sdk.uidemo

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.giphy.sdk.core.models.enums.RenditionType
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.themes.GridType
import com.giphy.sdk.uidemo.databinding.FragmentSettingsBinding

/**
 * Created by Cristian Holdunu on 13/03/2019.
 */
class SettingsDialogFragment : androidx.fragment.app.DialogFragment() {

    enum class ClipsPlaybackSetting {
        inline,
        popup;
    }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var settings: GPHSettings = GPHSettings()
    private var clipsPlaybackSetting = ClipsPlaybackSetting.inline
    var dismissListener: (GPHSettings, ClipsPlaybackSetting) -> Unit = { settings, clipsPlaybackSetting -> }

    companion object {
        private const val PICK_GRID_RENDITION = 201
        private const val PICK_ATTRIBUTION_RENDTION = 202

        private val KEY_SETTINGS = "key_settings"
        private val KEY_SETTINGS_CLIPS = "key_settings_clips"
        fun newInstance(gphSettings: GPHSettings, clipsPlaybackSetting: ClipsPlaybackSetting): SettingsDialogFragment {
            val fragment = SettingsDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_SETTINGS, gphSettings)
            bundle.putSerializable(KEY_SETTINGS_CLIPS, clipsPlaybackSetting)
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
        clipsPlaybackSetting = arguments!!.getSerializable(KEY_SETTINGS_CLIPS) as ClipsPlaybackSetting
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            themeSelector.setToggled(
                when (settings.theme) {
                    GPHTheme.Light -> R.id.lightTheme
                    GPHTheme.Dark -> R.id.darkTheme
                    GPHTheme.Automatic -> R.id.autoTheme
                    else -> R.id.autoTheme
                }, true
            )
            layoutSelector.setToggled(
                if (settings.gridType == GridType.waterfall) R.id.waterfall else R.id.carousel,
                true
            )
            clipsPlaybackSettingsSelector.setToggled(
                if (clipsPlaybackSetting == ClipsPlaybackSetting.inline) R.id.inline else R.id.popup,
                true
            )
            mediaTypeSelector.inflateMenu(if (settings.gridType == GridType.waterfall) R.menu.waterfal_media_types else R.menu.carousel_media_types)
            settings.mediaTypeConfig.forEach {
                val id = when (it) {
                    GPHContentType.gif -> R.id.typeGif
                    GPHContentType.clips -> R.id.typeClips
                    GPHContentType.sticker -> R.id.typeStickers
                    GPHContentType.text -> R.id.typeText
                    GPHContentType.emoji -> R.id.typeEmoji
                    GPHContentType.recents -> R.id.typeRecents
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

            clipsPlaybackSettingsSelector.onToggledListener = { toggle, selected ->
                if (toggle.id == R.id.inline) {
                    clipsPlaybackSetting = ClipsPlaybackSetting.inline
                    clipsPlaybackSettingsSelector.setToggled(R.id.inline, true)
                } else {
                    clipsPlaybackSetting = ClipsPlaybackSetting.popup
                    clipsPlaybackSettingsSelector.setToggled(R.id.popup, true)
                }
            }

            showAttributionCheck.isChecked = settings.showAttribution
            showConfirmationScreen.isChecked = settings.showConfirmationScreen
            showCheckeredBackground.isChecked = settings.showCheckeredBackground

            themeSelector.onToggledListener = { toggle, selected ->
                settings.theme = when (toggle.id) {
                    R.id.lightTheme -> GPHTheme.Light
                    R.id.darkTheme -> GPHTheme.Dark
                    else -> GPHTheme.Automatic
                }
            }
            dismissBtn.setOnClickListener { dismiss() }
            gridRenditionType.setOnClickListener { openRenditionPicker(PICK_GRID_RENDITION) }
            attributionRenditionType.setOnClickListener {
                openRenditionPicker(
                    PICK_ATTRIBUTION_RENDTION
                )
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        settings.gridType = when (binding.layoutSelector.selectedToggles().firstOrNull()?.id) {
            R.id.waterfall -> GridType.waterfall
            R.id.carousel -> GridType.carousel
            else -> GridType.waterfall
        }
        val contentTypes = ArrayList<GPHContentType>()
        binding.mediaTypeSelector.selectedToggles().forEach {
            when (it.id) {
                R.id.typeGif -> contentTypes.add(GPHContentType.gif)
                R.id.typeClips -> contentTypes.add(GPHContentType.clips)
                R.id.typeStickers -> contentTypes.add(GPHContentType.sticker)
                R.id.typeText -> contentTypes.add(GPHContentType.text)
                R.id.typeEmoji -> contentTypes.add(GPHContentType.emoji)
                R.id.typeRecents -> contentTypes.add(GPHContentType.recents)
            }
        }
        binding.apply {
            settings.mediaTypeConfig = contentTypes.toTypedArray()
            settings.showAttribution = showAttributionCheck.isChecked
            settings.showConfirmationScreen = showConfirmationScreen.isChecked
            settings.showCheckeredBackground = showCheckeredBackground.isChecked
        }
        dismissListener(settings, clipsPlaybackSetting)
        super.onDismiss(dialog)
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
}
