package com.almighty.downloader.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.almighty.downloader.R
import com.almighty.downloader.databinding.FragmentWebViewLayoutBinding

class WebViewFragment : Fragment() {
    private val binding by lazy {
        FragmentWebViewLayoutBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.useWideViewPort = true
            settings.databaseEnabled = true
            val userAgent = System.getProperty("http.agent")
            userAgent?.let {
                settings.userAgentString = userAgent + resources.getString(R.string.app_name)
            }
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            webChromeClient = object : WebChromeClient() {
                override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {

                }

                override fun onReceivedTitle(view: WebView?, title: String?) {

                }

            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url.toString())
                    return true
                }

                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
                    Log.d(TAG, "onLoadResource() called with: view = $view, url = $url")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.d(TAG, "onPageFinished() called with: view = $view, url = $url")
                    view?.let {
                        loadJs(view)
                    }
                }

            }

        }
        binding.webView.addJavascriptInterface(AndroidJSInterface, "Android")
        binding.webView.loadUrl("https://twitter.com/")

    }


    private fun loadJs(webView: WebView) {
        val script = "(function f() {var btns = document.getElementsByClassName('rec-item rn-typeNewsOne'); alert(btns.length);})()"
        webView.loadUrl("javascript:" + script)
    }


    object AndroidJSInterface {

        @JavascriptInterface
        fun onClicked() {
            Log.d(TAG, "Help button clicked")
        }
    }

    companion object {
        private const val TAG = "WebViewFragment"

    }
}