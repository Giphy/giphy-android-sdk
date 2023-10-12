package com.giphy.sdk.uidemo.videoPlayer

import android.content.Context
import androidx.media3.common.util.Util
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

object VideoCache {

    lateinit var cache: Cache
    lateinit var cacheDataSourceFactory: CacheDataSource.Factory
    private lateinit var cacheDataSource: CacheDataSource

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun initialize(context: Context, maxBytes: Long = 100 * 1024 * 1024) {
        if (this::cache.isInitialized) { return }
        val cacheFolder = File(context.filesDir, "example-video-cache")
        val cacheEvictor = LeastRecentlyUsedCacheEvictor(maxBytes)
        cache = SimpleCache(cacheFolder, cacheEvictor, StandaloneDatabaseProvider(context))

        cacheDataSourceFactory = CacheDataSource.Factory().apply {
            setCache(this@VideoCache.cache)
            setUpstreamDataSourceFactory(
                DefaultHttpDataSource.Factory()
                    .setUserAgent(
                        Util.getUserAgent(
                            context,
                            "GiphySDK"
                        )
                    )
            )
        }

        cacheDataSource = cacheDataSourceFactory.createDataSource()
    }
}
