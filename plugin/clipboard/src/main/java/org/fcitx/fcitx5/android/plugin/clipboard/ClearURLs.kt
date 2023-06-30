package org.fcitx.fcitx5.android.plugin.clipboard

import android.content.res.AssetManager
import android.util.Log
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.net.URLDecoder


object ClearURLs {

    private const val TAG = "ClearURLs"

    private val providersSerializer: KSerializer<Map<String, ClearURLsProvider>> = serializer()
    private val catalogSerializer: KSerializer<Map<String, Map<String, ClearURLsProvider>>> =
        MapSerializer(serializer(), providersSerializer)

    private var catalog: Map<String, ClearURLsProvider>? = null

    fun initCatalog(assetManager: AssetManager) {
        catalog = Json.decodeFromString(
            catalogSerializer,
            assetManager.open("data.minify.json").bufferedReader().readText()
        )["providers"]
    }

    fun transform(text: String): String {
        var x = text
        catalog?.let { map ->
            for ((name, provider) in map) {
                // matches url pattern
                if (!provider.urlPattern.toRegex().matches(x))
                    continue
                // not in exceptions
                if (provider.exceptions?.any { it.toRegex().matches(x) } == true)
                    continue
                Log.d(TAG, "$name matches $x")
                // apply redirections
                provider.redirections?.forEach { redirection ->
                    redirection.toRegex().matchEntire(x)?.let { matchResult ->
                        matchResult.groupValues.takeIf { it.size > 1 }?.let {
                            val redir = decodeURL(it[1])
                            Log.d(TAG, "$name makes $x redir to $redir")
                            x = redir
                        }
                    }
                }
                provider.rawRules?.forEach { rawRule ->
                    val r = rawRule.toRegex()
                    if (r.matches(x)) {
                        Log.d(TAG, "$name clears $rawRule from $x")
                        x = x.replace(r, "")
                    }
                }
                // apply rules
                provider.rules?.forEach { rule ->
                    val r = "(?:&amp;|[/?#&])$rule=[^&]*".toRegex()
                    if (r.matches(x)) {
                        Log.d(TAG, "$name clears $r from $x")
                        x = x.replace(r, "")
                    }
                }
            }
            return x
        } ?: throw IllegalStateException("Catalog is unavailable")
    }

    private fun decodeURL(url: String) =
        URLDecoder.decode(url.replace("+", "%2B"), "UTF-8").replace("%2B", "+")
}