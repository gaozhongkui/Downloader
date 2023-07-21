package com.almighty.downloader.browser.di

import com.almighty.downloader.browser.BrowserContract
import com.almighty.downloader.browser.BrowserNavigator
import com.almighty.downloader.browser.cleanup.DelegatingExitCleanup
import com.almighty.downloader.browser.cleanup.ExitCleanup
import com.almighty.downloader.browser.image.FaviconImageLoader
import com.almighty.downloader.browser.image.ImageLoader
import com.almighty.downloader.browser.tab.TabsRepository
import com.almighty.downloader.browser.theme.DefaultThemeProvider
import com.almighty.downloader.browser.theme.ThemeProvider
import android.app.Activity
import androidx.fragment.app.FragmentActivity
import dagger.Binds
import dagger.Module

/**
 * Binds implementations to interfaces for the browser scope.
 */
@Module
interface Browser2BindsModule {

    @Binds
    fun bindsActivity(fragmentActivity: FragmentActivity): Activity

    @Binds
    fun bindsBrowserModel(tabsRepository: TabsRepository): BrowserContract.Model

    @Binds
    fun bindsFaviconImageLoader(faviconImageLoader: FaviconImageLoader): ImageLoader

    @Binds
    fun bindsBrowserNavigator(browserNavigator: BrowserNavigator): BrowserContract.Navigator

    @Binds
    fun bindsExitCleanup(delegatingExitCleanup: DelegatingExitCleanup): ExitCleanup

    @Binds
    fun bindsThemeProvider(legacyThemeProvider: DefaultThemeProvider): ThemeProvider
}
