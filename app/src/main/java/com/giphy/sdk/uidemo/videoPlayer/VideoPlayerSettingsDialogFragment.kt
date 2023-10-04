package com.giphy.sdk.uidemo

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.giphy.sdk.ui.GPHSettings

import com.giphy.sdk.uidemo.databinding.FragmentVideoPlayerSettingsBinding

class VideoPlayerSettingsDialogFragment : androidx.fragment.app.DialogFragment() {

    enum class VideoPlaybackSetting {
        inline,
        popup;
    }

    private var _binding: FragmentVideoPlayerSettingsBinding? = null
    private val binding get() = _binding!!

    private var settings: GPHSettings = GPHSettings()
    private var clipsPlaybackSetting = VideoPlaybackSetting.inline
    var dismissListener: (GPHSettings, VideoPlaybackSetting) -> Unit = { _, _ -> }

    companion object {

        private val KEY_SETTINGS = "key_settings"
        private val KEY_SETTINGS_CLIPS = "key_settings_clips"
        fun newInstance(gphSettings: GPHSettings, clipsPlaybackSetting: VideoPlaybackSetting): VideoPlayerSettingsDialogFragment {
            val fragment = VideoPlayerSettingsDialogFragment()
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
        settings = requireArguments().parcelable(KEY_SETTINGS)!!
        clipsPlaybackSetting = requireArguments().serializable(KEY_SETTINGS_CLIPS)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideoPlayerSettingsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            clipsPlaybackSettingsSelector.setToggled(
                if (clipsPlaybackSetting == VideoPlaybackSetting.inline) R.id.inline else R.id.popup,
                true
            )

            clipsPlaybackSettingsSelector.onToggledListener = { toggle, _ ->
                if (toggle.id == R.id.inline) {
                    clipsPlaybackSetting = VideoPlaybackSetting.inline
                    clipsPlaybackSettingsSelector.setToggled(R.id.inline, true)
                } else {
                    clipsPlaybackSetting = VideoPlaybackSetting.popup
                    clipsPlaybackSettingsSelector.setToggled(R.id.popup, true)
                }
            }

            dismissBtn.setOnClickListener { dismiss() }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        dismissListener(settings, clipsPlaybackSetting)
        super.onDismiss(dialog)
    }
}
