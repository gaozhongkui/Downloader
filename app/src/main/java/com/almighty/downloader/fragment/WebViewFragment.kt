package com.almighty.downloader.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
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


            }

        }

        binding.webView.loadUrl("https://twitter.com/")

    }

    companion object {
        private const val TAG = "WebViewFragment"
    }
}