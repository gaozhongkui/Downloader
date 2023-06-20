package com.almighty.downloader.internal

import android.content.MutableContextWrapper
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.almighty.downloader.R
import com.almighty.downloader.TheApp
import com.almighty.downloader.bean.WebBean
import com.almighty.downloader.component.CommonWebView
import java.util.*

class WebTabManager private constructor(){

    private object Holder{
        val instance = WebTabManager()
    }

    companion object Instance {
        var increaseKey = 0
        @JvmStatic
        fun getInstance(): WebTabManager{
            return Holder.instance
        }
    }

    private val cacheWebTab = LinkedList<WebBean>()

    fun divideAllWebView(){
        for (wb in cacheWebTab){
            wb.webView.parent?.let {
                (it as ViewGroup).removeAllViews()
            }
        }
    }

    fun getCacheWebTab(): List<WebBean>{
        return cacheWebTab
    }

    fun addNewTab(): WebBean {
        val webView = CommonWebView(MutableContextWrapper(TheApp.getInstance()))
        val wb = WebBean(increaseKey++, webView, "首页", ContextCompat.getDrawable(TheApp.getInstance(), R.drawable.web_tab_icon_home), ContextCompat.getDrawable(TheApp.getInstance(), R.drawable.ic_launcher_foreground))
        cacheWebTab.add(wb)
        return wb
    }

    fun joinWebTabToLast(key: Int){
        for (wb in cacheWebTab){
            if (wb.id == key){
                cacheWebTab.remove(wb)
                cacheWebTab.addLast(wb)
                break
            }
        }
    }

    fun updateLastTabTitle(tabTitle: String){
        val wb = cacheWebTab.last
        wb.tabTitle = tabTitle
    }

    fun updateLastTabIcon(tabIcon: Drawable){
        val wb = cacheWebTab.last
        wb.tabIcon = tabIcon
    }
}