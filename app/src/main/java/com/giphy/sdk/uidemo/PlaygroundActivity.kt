package com.giphy.sdk.uidemo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.giphy.sdk.core.GiphyCore
import com.giphy.sdk.core.models.enums.RenditionType
import com.giphy.sdk.core.network.api.CompletionHandler
import com.giphy.sdk.core.network.response.MediaResponse
import com.giphy.sdk.ui.GiphyCoreUI
import kotlinx.android.synthetic.main.playground_activity.*

class PlaygroundActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GiphyCoreUI.configure(this, "oUThALwXNzrOG4b1jRyoPDtmZJmmW5HU")
        setContentView(R.layout.playground_activity)

        GiphyCore.apiClient.gifById("3LziMSsb1n3e9Atunf", object: CompletionHandler<MediaResponse> {
            override fun onComplete(result: MediaResponse?, e: Throwable?) {
                gifView.setMedia(result?.data, RenditionType.original)
            }
        })

        startSDK.setOnClickListener {
            startActivity(Intent(this, DemoActivity::class.java))
        }
    }
}