package com.almighty.downloader.html.homepage

import com.almighty.downloader.R
import com.almighty.downloader.browser.theme.ThemeProvider
import com.almighty.downloader.constant.FILE
import com.almighty.downloader.constant.UTF8
import com.almighty.downloader.html.HtmlPageFactory
import com.almighty.downloader.html.jsoup.andBuild
import com.almighty.downloader.html.jsoup.body
import com.almighty.downloader.html.jsoup.charset
import com.almighty.downloader.html.jsoup.id
import com.almighty.downloader.html.jsoup.parse
import com.almighty.downloader.html.jsoup.style
import com.almighty.downloader.html.jsoup.tag
import com.almighty.downloader.html.jsoup.title
import com.almighty.downloader.search.SearchEngineProvider
import android.app.Application
import io.reactivex.rxjava3.core.Single
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

/**
 * A factory for the home page.
 */
class HomePageFactory @Inject constructor(
    private val application: Application,
    private val searchEngineProvider: SearchEngineProvider,
    private val homePageReader: HomePageReader,
    private val themeProvider: ThemeProvider
) : HtmlPageFactory {

    private val title = application.getString(R.string.home)

    private fun Int.toColor(): String {
        val string = Integer.toHexString(this)

        return string.substring(2) + string.substring(0, 2)
    }

    private val backgroundColor: String
        get() = themeProvider.color(0/*R.attr.colorPrimary*/).toColor()
    private val cardColor: String
        get() = themeProvider.color(R.attr.autoCompleteBackgroundColor).toColor()
    private val textColor: String
        get() = themeProvider.color(R.attr.autoCompleteTitleColor).toColor()

    override fun buildPage(): Single<String> = Single
        .just(searchEngineProvider.provideSearchEngine())
        .map { (iconUrl, queryUrl, _) ->
            parse(homePageReader.provideHtml()) andBuild {
                title { title }
                style { content ->
                    content.replace("--body-bg: {COLOR}", "--body-bg: #$backgroundColor;")
                        .replace("--box-bg: {COLOR}", "--box-bg: #$cardColor;")
                        .replace("--box-txt: {COLOR}", "--box-txt: #$textColor;")
                }
                charset { UTF8 }
                body {
                    id("image_url") { attr("src", iconUrl) }
                    tag("script") {
                        html(
                            html()
                                .replace("\${BASE_URL}", queryUrl)
                                .replace("&", "\\u0026")
                        )
                    }
                }
            }
        }
        .map { content -> Pair(createHomePage(), content) }
        .doOnSuccess { (page, content) ->
            FileWriter(page, false).use {
                it.write(content)
            }
        }
        .map { (page, _) -> "$FILE$page" }

    /**
     * Create the home page file.
     */
    fun createHomePage() = File(application.filesDir, FILENAME)

    companion object {

        const val FILENAME = "homepage.html"

    }

}
