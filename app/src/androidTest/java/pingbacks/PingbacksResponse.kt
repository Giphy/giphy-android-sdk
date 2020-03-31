package com.giphy.sdk.uidemo.pingbacks

import com.giphy.sdk.analytics.models.Session
import com.google.gson.annotations.SerializedName

data class PingbacksResponse(
    @SerializedName("sessions") val sessions: List<Session>
)
