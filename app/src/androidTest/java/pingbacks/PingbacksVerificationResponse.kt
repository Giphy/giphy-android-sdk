package com.giphy.sdk.uidemo.pingbacks

import com.google.gson.annotations.SerializedName

data class PingbacksVerificationResponse(
    @SerializedName("onboarding_errors") val onboardingErrors: List<Error>,
    @SerializedName("specification_errors") val specificationErrors: List<Error>,
    @SerializedName("metadata") val metadata: Metadata
)
