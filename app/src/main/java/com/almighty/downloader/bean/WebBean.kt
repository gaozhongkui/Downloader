package com.almighty.downloader.bean

import android.graphics.drawable.Drawable
import com.almighty.downloader.component.CommonWebView

data class WebBean(val id: Int, val webView: CommonWebView, var tabTitle: String,
                   var tabIcon: Drawable?, var picture: Drawable?)
