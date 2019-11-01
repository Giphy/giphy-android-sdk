package com.giphy.sdk.uidemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.SeekBar
import com.giphy.sdk.ui.GiphyCoreUI
import com.giphy.sdk.ui.views.GiphyGridFragment
import kotlinx.android.synthetic.main.grid_activity.*

class GridActivity : AppCompatActivity() {

    var gridFragment: GiphyGridFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.grid_activity)
        gridFragment = supportFragmentManager.findFragmentById(R.id.gifsGridFragment) as GiphyGridFragment

        spanCountBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                gridFragment?.spanCount = (seekBar?.progress ?: 1) + 1
            }

        })

        paddingBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                gridFragment?.cellPadding = seekBar?.progress ?: 0
            }

        })

        orientationToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            gridFragment?.direction = if (isChecked) androidx.recyclerview.widget.RecyclerView.HORIZONTAL else androidx.recyclerview.widget.RecyclerView.VERTICAL
        }
    }
}