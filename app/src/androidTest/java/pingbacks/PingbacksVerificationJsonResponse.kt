package com.giphy.sdk.uidemo.pingbacks

import com.google.gson.annotations.SerializedName

data class PingbacksVerificationJsonResponse(
    @SerializedName("pingbacks") val pingbacks: List<PingbacksResponse>
)
