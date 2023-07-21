package com.almighty.downloader.browser.data

import com.almighty.downloader.preference.UserPreferences
import android.webkit.CookieManager
import javax.inject.Inject

/**
 * The default cookie administrator that sets cookie preferences for the default browser instance.
 */
class DefaultCookieAdministrator @Inject constructor(
    private val userPreferences: UserPreferences
) : CookieAdministrator {
    override fun adjustCookieSettings() {
        CookieManager.getInstance().setAcceptCookie(userPreferences.cookiesEnabled)
    }
}
