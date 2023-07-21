@file:JvmName("Injector")

package com.almighty.downloader.browser.di

import com.almighty.downloader.BrowserApp
import android.content.Context
import androidx.fragment.app.Fragment

/**
 * The [AppComponent] attached to the application [Context].
 */
val Context.injector: AppComponent
    get() =null as AppComponent // (applicationContext as BrowserApp).applicationComponent

/**
 * The [AppComponent] attached to the context, note that the fragment must be attached.
 */
val Fragment.injector: AppComponent
    get() =null as AppComponent// (context!!.applicationContext as BrowserApp).applicationComponent

/**
 * The [AppComponent] attached to the context, note that the fragment must be attached.
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("Consumers should switch to support.v4.app.Fragment")
val android.app.Fragment.injector: AppComponent
    get() = null as AppComponent//(activity!!.applicationContext as BrowserApp).applicationComponent
