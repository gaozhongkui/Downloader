package com.almighty.downloader.utils

import com.almighty.downloader.preference.DeveloperPreferences
import leakcanary.LeakCanary
import javax.inject.Inject

/**
 * Sets up LeakCanary.
 */
class LeakCanaryUtils @Inject constructor(private val developerPreferences: DeveloperPreferences) {

    /**
     * Setup LeakCanary
     */
    fun setup() {
        LeakCanary.config = LeakCanary.config.copy(
            dumpHeap = developerPreferences.useLeakCanary
        )
    }

}
