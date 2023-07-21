package com.almighty.downloader.browser

import com.almighty.downloader.IncognitoBrowserActivity
import com.almighty.downloader.R
import com.almighty.downloader.browser.cleanup.ExitCleanup
import com.almighty.downloader.browser.download.DownloadPermissionsHelper
import com.almighty.downloader.browser.download.PendingDownload
import com.almighty.downloader.extensions.copyToClipboard
import com.almighty.downloader.extensions.snackbar
import com.almighty.downloader.log.Logger
import com.almighty.downloader.settings.activity.SettingsActivity
import com.almighty.downloader.utils.IntentUtils
import com.almighty.downloader.utils.Utils
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject

/**
 * The navigator implementation.
 */
class BrowserNavigator @Inject constructor(
    private val activity: FragmentActivity,
    private val clipboardManager: ClipboardManager,
    private val logger: Logger,
    private val downloadPermissionsHelper: DownloadPermissionsHelper,
    private val exitCleanup: ExitCleanup
) : BrowserContract.Navigator {

    override fun openSettings() {
        activity.startActivity(Intent(activity, SettingsActivity::class.java))
    }

    override fun sharePage(url: String, title: String?) {
        IntentUtils(activity).shareUrl(url, title)
    }

    override fun copyPageLink(url: String) {
        clipboardManager.copyToClipboard(url)
        activity.snackbar(R.string.message_link_copied)
    }

    override fun closeBrowser() {
        exitCleanup.cleanUp()
        activity.finish()
    }

    override fun addToHomeScreen(url: String, title: String, favicon: Bitmap?) {
        Utils.createShortcut(activity, url, title, favicon)
        logger.log(TAG, "Creating shortcut: $title $url")
    }

    override fun download(pendingDownload: PendingDownload) {
        downloadPermissionsHelper.download(
            activity = activity,
            url = pendingDownload.url,
            userAgent = pendingDownload.userAgent,
            contentDisposition = pendingDownload.contentDisposition,
            mimeType = pendingDownload.mimeType,
            contentLength = pendingDownload.contentLength
        )
    }

    override fun backgroundBrowser() {
        activity.moveTaskToBack(true)
    }

    override fun launchIncognito(url: String?) {
        IncognitoBrowserActivity.launch(activity, url)
    }

    companion object {
        private const val TAG = "BrowserNavigator"
    }

}
