package com.giphy.sdk.uidemo

import android.app.Application
import com.squareup.leakcanary.LeakCanary

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.FLAVOR == "debug") {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return
            }
            LeakCanary.install(this)
        }
    }
}
