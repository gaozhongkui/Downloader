package com.almighty.downloader.browser.di

import com.almighty.downloader.adblock.allowlist.AllowListModel
import com.almighty.downloader.adblock.allowlist.SessionAllowListModel
import com.almighty.downloader.adblock.source.AssetsHostsDataSource
import com.almighty.downloader.adblock.source.HostsDataSource
import com.almighty.downloader.adblock.source.HostsDataSourceProvider
import com.almighty.downloader.adblock.source.PreferencesHostsDataSourceProvider
import com.almighty.downloader.database.adblock.HostsDatabase
import com.almighty.downloader.database.adblock.HostsRepository
import com.almighty.downloader.database.allowlist.AdBlockAllowListDatabase
import com.almighty.downloader.database.allowlist.AdBlockAllowListRepository
import com.almighty.downloader.database.bookmark.BookmarkDatabase
import com.almighty.downloader.database.bookmark.BookmarkRepository
import com.almighty.downloader.database.downloads.DownloadsDatabase
import com.almighty.downloader.database.downloads.DownloadsRepository
import com.almighty.downloader.database.history.HistoryDatabase
import com.almighty.downloader.database.history.HistoryRepository
import com.almighty.downloader.ssl.SessionSslWarningPreferences
import com.almighty.downloader.ssl.SslWarningPreferences
import dagger.Binds
import dagger.Module

/**
 * Dependency injection module used to bind implementations to interfaces.
 */
@Module
interface AppBindsModule {

    @Binds
    fun bindsBookmarkModel(bookmarkDatabase: BookmarkDatabase): BookmarkRepository

    @Binds
    fun bindsDownloadsModel(downloadsDatabase: DownloadsDatabase): DownloadsRepository

    @Binds
    fun bindsHistoryModel(historyDatabase: HistoryDatabase): HistoryRepository

    @Binds
    fun bindsAdBlockAllowListModel(adBlockAllowListDatabase: AdBlockAllowListDatabase): AdBlockAllowListRepository

    @Binds
    fun bindsAllowListModel(sessionAllowListModel: SessionAllowListModel): AllowListModel

    @Binds
    fun bindsSslWarningPreferences(sessionSslWarningPreferences: SessionSslWarningPreferences): SslWarningPreferences

    @Binds
    fun bindsHostsDataSource(assetsHostsDataSource: AssetsHostsDataSource): HostsDataSource

    @Binds
    fun bindsHostsRepository(hostsDatabase: HostsDatabase): HostsRepository

    @Binds
    fun bindsHostsDataSourceProvider(preferencesHostsDataSourceProvider: PreferencesHostsDataSourceProvider): HostsDataSourceProvider
}
