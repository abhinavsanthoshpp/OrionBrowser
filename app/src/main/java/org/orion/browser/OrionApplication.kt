package org.orion.browser

import android.app.Application
import org.orion.browser.adblock.AdBlockEngine
import org.orion.browser.network.DoHResolver

class OrionApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Native AdBlock Bloom engine in background thread
        AdBlockEngine.initialize(this)

        // Initialize DNS-over-HTTPS resolver
        DoHResolver.initialize(this)
    }

    companion object {
        lateinit var instance: OrionApplication
            private set
    }
}
