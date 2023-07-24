package com.almighty.downloader.browser

import com.almighty.downloader.browser.tab.TabViewState
import com.almighty.downloader.database.Bookmark
import com.almighty.downloader.database.HistoryEntry
import com.almighty.downloader.database.downloads.DownloadEntry
import com.almighty.downloader.ssl.SslCertificateInfo
import com.almighty.downloader.ssl.showSslDialog
import android.content.Intent
import android.view.View
import com.almighty.downloader.browser.view.targetUrl.LongPress
import com.almighty.downloader.fragment.HomeFragment

/**
 * An adapter between [BrowserContract.View] and the [BrowserActivity] that creates partial states
 * to render in the activity.
 */
class BrowserStateAdapter(private val homeFragment: HomeFragment) : BrowserContract.View {

    private var currentState: BrowserViewState? = null
    private var currentTabs: List<TabViewState>? = null

    override fun renderState(viewState: BrowserViewState) {
        val (displayUrl, sslState, isRefresh, progress, enableFullMenu, themeColor, isForwardEnabled, isBackEnabled, bookmarks, isBookmarked, isBookmarkEnabled, isRootFolder, findInPage) = viewState

        homeFragment.renderState(
            PartialBrowserViewState(displayUrl = displayUrl.takeIf { it != currentState?.displayUrl },
                sslState = sslState.takeIf { it != currentState?.sslState },
                isRefresh = isRefresh.takeIf { it != currentState?.isRefresh },
                progress = progress.takeIf { it != currentState?.progress },
                enableFullMenu = enableFullMenu.takeIf { it != currentState?.enableFullMenu },
                themeColor = themeColor.takeIf { it != currentState?.themeColor },
                isForwardEnabled = isForwardEnabled.takeIf { it != currentState?.isForwardEnabled },
                isBackEnabled = isBackEnabled.takeIf { it != currentState?.isBackEnabled },
                bookmarks = bookmarks.takeIf { it != currentState?.bookmarks },
                isBookmarked = isBookmarked.takeIf { it != currentState?.isBookmarked },
                isBookmarkEnabled = isBookmarkEnabled.takeIf { it != currentState?.isBookmarkEnabled },
                isRootFolder = isRootFolder.takeIf { it != currentState?.isRootFolder },
                findInPage = findInPage.takeIf { it != currentState?.findInPage })
        )

        currentState = viewState
    }

    override fun renderTabs(tabs: List<TabViewState>) {
        tabs.takeIf { it != currentTabs }?.let(homeFragment::renderTabs)
    }

    override fun showAddBookmarkDialog(title: String, url: String, folders: List<String>) {
        homeFragment.showAddBookmarkDialog(title, url, folders)
    }

    override fun showBookmarkOptionsDialog(bookmark: Bookmark.Entry) {
        homeFragment.showBookmarkOptionsDialog(bookmark)
    }

    override fun showEditBookmarkDialog(
        title: String, url: String, folder: String, folders: List<String>
    ) {
        homeFragment.showEditBookmarkDialog(title, url, folder, folders)
    }

    override fun showFolderOptionsDialog(folder: Bookmark.Folder) {
        homeFragment.showFolderOptionsDialog(folder)
    }

    override fun showEditFolderDialog(title: String) {
        homeFragment.showEditFolderDialog(title)
    }

    override fun showDownloadOptionsDialog(download: DownloadEntry) {
        homeFragment.showDownloadOptionsDialog(download)
    }

    override fun showHistoryOptionsDialog(historyEntry: HistoryEntry) {
        homeFragment.showHistoryOptionsDialog(historyEntry)
    }

    override fun showFindInPageDialog() {
        homeFragment.showFindInPageDialog()
    }

    override fun showLinkLongPressDialog(longPress: LongPress) {
        homeFragment.showLinkLongPressDialog(longPress)
    }

    override fun showImageLongPressDialog(longPress: LongPress) {
        homeFragment.showImageLongPressDialog(longPress)
    }

    override fun showSslDialog(sslCertificateInfo: SslCertificateInfo) {
        homeFragment.context?.showSslDialog(sslCertificateInfo)
    }

    override fun showCloseBrowserDialog(id: Int) {
        homeFragment.showCloseBrowserDialog(id)
    }

    override fun openBookmarkDrawer() {
        homeFragment.openBookmarkDrawer()
    }

    override fun closeBookmarkDrawer() {
        homeFragment.closeBookmarkDrawer()
    }

    override fun openTabDrawer() {
        homeFragment.openTabDrawer()
    }

    override fun closeTabDrawer() {
        homeFragment.closeTabDrawer()
    }

    override fun showToolbar() {
        homeFragment.showToolbar()
    }

    override fun showToolsDialog(areAdsAllowed: Boolean, shouldShowAdBlockOption: Boolean) {
        homeFragment.showToolsDialog(areAdsAllowed, shouldShowAdBlockOption)
    }

    override fun showLocalFileBlockedDialog() {
        homeFragment.showLocalFileBlockedDialog()
    }

    override fun showFileChooser(intent: Intent) {
        homeFragment.showFileChooser(intent)
    }

    override fun showCustomView(view: View) {
        homeFragment.showCustomView(view)
    }

    override fun hideCustomView() {
        homeFragment.hideCustomView()
    }

    override fun clearSearchFocus() {
        homeFragment.clearSearchFocus()
    }
}
