package com.giphy.sdk.uidemo.VideoPlayer

import android.content.Context
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import java.io.File

object VideoCache {

    lateinit var cache: Cache
    lateinit var cacheDataSourceFactory: CacheDataSource.Factory
    lateinit var cacheDataSource: CacheDataSource

    fun initialize(context: Context, maxBytes: Long = 100 * 1024 * 1024) {
        if (this::cache.isInitialized) { return }
        val cacheFolder = File(context.filesDir, "example-video-cache")
        val cacheEvictor = LeastRecentlyUsedCacheEvictor(maxBytes)
        cache = SimpleCache(cacheFolder, cacheEvictor, ExoDatabaseProvider(context))

        cacheDataSourceFactory = CacheDataSource.Factory().apply {
            setCache(this@VideoCache.cache!!)
            setUpstreamDataSourceFactory(
                DefaultDataSourceFactory(
                    context, Util.getUserAgent(
                        context,
                        "GiphySDK"
                    )
                )
            )
        }

        cacheDataSource = cacheDataSourceFactory.createDataSource()
    }
}
