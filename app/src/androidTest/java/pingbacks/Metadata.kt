package com.giphy.sdk.uidemo.pingbacks

import com.google.gson.annotations.SerializedName

data class Metadata(
    @SerializedName("user_id") val userId: String,
    @SerializedName("response_id") val responseId: String,
    @SerializedName("tid") val tid: String,
    @SerializedName("random_id") val randomId: String,
    @SerializedName("event_type") val eventType: String,
    @SerializedName("action_type") val actionType: String,
    @SerializedName("gif_id") val gifId: String
)
