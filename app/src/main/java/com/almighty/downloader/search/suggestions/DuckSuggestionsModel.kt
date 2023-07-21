package com.almighty.downloader.search.suggestions

import com.almighty.downloader.R
import com.almighty.downloader.constant.UTF8
import com.almighty.downloader.database.SearchSuggestion
import com.almighty.downloader.extensions.map
import com.almighty.downloader.extensions.preferredLocale
import com.almighty.downloader.log.Logger
import android.app.Application
import io.reactivex.rxjava3.core.Single
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * The search suggestions provider for the DuckDuckGo search engine.
 */
class DuckSuggestionsModel(
    okHttpClient: Single<OkHttpClient>,
    requestFactory: RequestFactory,
    application: Application,
    logger: Logger
) : BaseSuggestionsModel(okHttpClient, requestFactory, UTF8, application.preferredLocale, logger) {

    private val searchSubtitle = application.getString(R.string.suggestion)

    // https://duckduckgo.com/ac/?q={query}
    override fun createQueryUrl(query: String, language: String): HttpUrl = HttpUrl.Builder()
        .scheme("https")
        .host("duckduckgo.com")
        .encodedPath("/ac/")
        .addEncodedQueryParameter("q", query)
        .build()

    @Throws(Exception::class)
    override fun parseResults(responseBody: ResponseBody): List<SearchSuggestion> {
        return JSONArray(responseBody.string())
            .map { it as JSONObject }
            .map { it.getString("phrase") }
            .map { SearchSuggestion("$searchSubtitle \"$it\"", it) }
    }

}
