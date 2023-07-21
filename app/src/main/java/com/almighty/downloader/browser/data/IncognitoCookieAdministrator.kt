package com.almighty.downloader.browser.data

import com.almighty.downloader.Capabilities
import com.almighty.downloader.isSupported
import com.almighty.downloader.preference.UserPreferences
import android.webkit.CookieManager
import javax.inject.Inject

/**
 * The cookie administrator used to set cookie preferences for the incognito instance.
 */
class IncognitoCookieAdministrator @Inject constructor(
    private val userPreferences: UserPreferences
) : CookieAdministrator {
    override fun adjustCookieSettings() {
        val cookieManager = CookieManager.getInstance()
        if (Capabilities.FULL_INCOGNITO.isSupported) {
            cookieManager.setAcceptCookie(userPreferences.cookiesEnabled)
        } else {
            cookieManager.setAcceptCookie(userPreferences.incognitoCookiesEnabled)
        }
    }
}
