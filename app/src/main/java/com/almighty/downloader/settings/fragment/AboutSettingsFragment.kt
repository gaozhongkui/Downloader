/*
 * Copyright 2014 A.C.R. Development
 */
package com.almighty.downloader.settings.fragment

import com.permissionx.guolindev.BuildConfig
import com.almighty.downloader.R
import android.os.Bundle

class AboutSettingsFragment : AbstractSettingsFragment() {

    override fun providePreferencesXmlResource() = R.xml.preference_about

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        clickablePreference(
            preference = SETTINGS_VERSION,
            summary = "",//BuildConfig.VERSION_NAME,
            onClick = { }
        )
    }

    companion object {
        private const val SETTINGS_VERSION = "pref_version"
    }
}
