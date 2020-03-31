package com.giphy.sdk.uidemo

import android.net.Uri
import android.util.Log
import com.giphy.sdk.analytics.GiphyPingbacks
import com.giphy.sdk.analytics.models.Action
import com.giphy.sdk.core.GiphyCore
import com.giphy.sdk.core.network.api.GPHApiClient
import com.giphy.sdk.uidemo.pingbacks.PingbacksVerificationJsonResponse
import com.giphy.sdk.uidemo.pingbacks.PingbacksVerificationResponse
import org.junit.Assert

object PingbacksTestInfo {

    val TAG = PingbacksTestInfo.javaClass.simpleName
    val verificationTag = "giphyanalyticsverifyd41d8c"
    val verificationUrl = Uri.parse("https://pingback.giphy.com")
    val verificationPath = "verify/onboarding"
    val verificationJsonPath = "verify/json"
    val KEY_RANDOM_ID = "random_id"
    val KEY_CONTENT_TYPE = "content_type"

    val feedLoadingDelay = 2000L
    val pingbacksProcessingDelay = 3000L

    fun checkTestResponse(contentType: String) {
        val randomId = GiphyCore.apiClient.analyticsId.randomId ?: ""
        val pingbacksResponse = GiphyCore.apiClient.queryStringConnectionWrapper(verificationUrl,
                verificationPath,
                GPHApiClient.HTTPMethod.GET,
                PingbacksVerificationResponse::class.java,
                hashMapOf(KEY_RANDOM_ID to (randomId),
                        KEY_CONTENT_TYPE to contentType))
                .executeImmediately()

        Log.d(TAG, "Onboarding Errors")
        pingbacksResponse.onboardingErrors.forEach {
            Log.d(TAG, "${it.requirement} = ${it.result}")
            Assert.assertEquals("randomId=$randomId requirement=${it.requirement}", "PASSED", it.result)
        }

        Log.d(TAG, "Specification Errors")
        pingbacksResponse.specificationErrors.forEach {
            Log.d(TAG, "${it.requirement} = ${it.result}")
            Assert.assertEquals("randomId=$randomId requirement=${it.requirement}", "PASSED", it.result)
        }
    }

    fun checkTestResponse(): List<Action> {
        val sessions = GiphyPingbacks.pingbackCollector.sessions.values.toList()
        val pingbacksResponse = GiphyCore.apiClient.queryStringConnectionWrapper(verificationUrl,
                verificationJsonPath,
                GPHApiClient.HTTPMethod.GET,
                PingbacksVerificationJsonResponse::class.java,
                hashMapOf(KEY_RANDOM_ID to (GiphyCore.apiClient.analyticsId.randomId ?: "")))
                .executeImmediately()

        val localActions = sessions.flatMap { it.events }.flatMap { it.actions }
        val serverActions = pingbacksResponse.pingbacks.flatMap { it.sessions }.flatMap { it.events }.flatMap { it.actions }

        for (localEvent in localActions) {
            if (serverActions.contains(localEvent) == false) {
                // TODO fix server comparison
                // Assert.assertTrue(false)
            }
        }
        return serverActions
    }
}
