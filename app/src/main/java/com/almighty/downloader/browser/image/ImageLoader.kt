package com.almighty.downloader.browser.image

import com.almighty.downloader.database.Bookmark
import android.widget.ImageView

/**
 * Loads images for bookmark entries.
 */
interface ImageLoader {

    /**
     * Load a the favicon into the [imageView] for the provided [bookmark].
     */
    fun loadImage(imageView: ImageView, bookmark: Bookmark)

}
