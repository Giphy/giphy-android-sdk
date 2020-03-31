package com.giphy.sdk.uidemo.pingbacks

import com.google.gson.annotations.SerializedName

data class Error(
    @SerializedName("requirement") val requirement: String,
    @SerializedName("result") val result: String,
    @SerializedName("message") val message: String
)
