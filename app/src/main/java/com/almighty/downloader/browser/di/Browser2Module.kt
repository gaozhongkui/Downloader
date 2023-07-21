package com.almighty.downloader.browser.di

import com.almighty.downloader.R
import com.almighty.downloader.browser.BrowserContract
import com.almighty.downloader.browser.data.CookieAdministrator
import com.almighty.downloader.browser.data.DefaultCookieAdministrator
import com.almighty.downloader.browser.history.DefaultHistoryRecord
import com.almighty.downloader.browser.history.HistoryRecord
import com.almighty.downloader.browser.history.NoOpHistoryRecord
import com.almighty.downloader.browser.image.IconFreeze
import com.almighty.downloader.browser.notification.DefaultTabCountNotifier
import com.almighty.downloader.browser.notification.IncognitoTabCountNotifier
import com.almighty.downloader.browser.notification.TabCountNotifier
import com.almighty.downloader.browser.search.IntentExtractor
import com.almighty.downloader.browser.tab.DefaultUserAgent
import com.almighty.downloader.browser.tab.bundle.BundleStore
import com.almighty.downloader.browser.tab.bundle.DefaultBundleStore
import com.almighty.downloader.browser.tab.bundle.IncognitoBundleStore
import com.almighty.downloader.browser.ui.BookmarkConfiguration
import com.almighty.downloader.browser.ui.TabConfiguration
import com.almighty.downloader.browser.ui.UiConfiguration
import com.almighty.downloader.adblock.AdBlocker
import com.almighty.downloader.adblock.BloomFilterAdBlocker
import com.almighty.downloader.adblock.NoOpAdBlocker
import com.almighty.downloader.extensions.drawable
import com.almighty.downloader.preference.UserPreferences
import com.almighty.downloader.utils.IntentUtils
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.webkit.WebSettings
import androidx.core.graphics.drawable.toBitmap
import dagger.Module
import dagger.Provides
import javax.inject.Provider

/**
 * Constructs dependencies for the browser scope.
 */
@Module
class Browser2Module {

    @Provides
    fun providesAdBlocker(
        userPreferences: UserPreferences,
        bloomFilterAdBlocker: Provider<BloomFilterAdBlocker>,
        noOpAdBlocker: NoOpAdBlocker
    ): AdBlocker = if (userPreferences.adBlockEnabled) {
        bloomFilterAdBlocker.get()
    } else {
        noOpAdBlocker
    }

    // TODO: dont force cast
    @Provides
    @InitialUrl
    fun providesInitialUrl(
        @InitialIntent initialIntent: Intent,
        intentExtractor: IntentExtractor
    ): String? =
        (intentExtractor.extractUrlFromIntent(initialIntent) as? BrowserContract.Action.LoadUrl)?.url

    // TODO: auto inject intent utils
    @Provides
    fun providesIntentUtils(activity: Activity): IntentUtils = IntentUtils(activity)

    @Provides
    fun providesUiConfiguration(
        userPreferences: UserPreferences
    ): UiConfiguration = UiConfiguration(
        tabConfiguration = if (userPreferences.showTabsInDrawer) {
            TabConfiguration.DRAWER
        } else {
            TabConfiguration.DESKTOP
        },
        bookmarkConfiguration = if (userPreferences.bookmarksAndTabsSwapped) {
            BookmarkConfiguration.LEFT
        } else {
            BookmarkConfiguration.RIGHT
        }
    )

    @DefaultUserAgent
    @Provides
    fun providesDefaultUserAgent(application: Application): String =
        WebSettings.getDefaultUserAgent(application)


    @Provides
    fun providesHistoryRecord(
        @IncognitoMode incognitoMode: Boolean,
        defaultHistoryRecord: DefaultHistoryRecord
    ): HistoryRecord = if (incognitoMode) {
        NoOpHistoryRecord
    } else {
        defaultHistoryRecord
    }

    @Provides
    fun providesCookieAdministrator(
        @IncognitoMode incognitoMode: Boolean,
        defaultCookieAdministrator: DefaultCookieAdministrator,
        incognitoCookieAdministrator: DefaultCookieAdministrator
    ): CookieAdministrator = if (incognitoMode) {
        incognitoCookieAdministrator
    } else {
        defaultCookieAdministrator
    }

    @Provides
    fun providesTabCountNotifier(
        @IncognitoMode incognitoMode: Boolean,
        incognitoTabCountNotifier: IncognitoTabCountNotifier
    ): TabCountNotifier = if (incognitoMode) {
        incognitoTabCountNotifier
    } else {
        DefaultTabCountNotifier
    }

    @Provides
    fun providesBundleStore(
        @IncognitoMode incognitoMode: Boolean,
        defaultBundleStore: DefaultBundleStore
    ): BundleStore = if (incognitoMode) {
        IncognitoBundleStore
    } else {
        defaultBundleStore
    }

    @IconFreeze
    @Provides
    fun providesFrozenIcon(activity: Activity): Bitmap =
        activity.drawable(R.drawable.ic_frozen).toBitmap()

}
