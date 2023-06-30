package org.fcitx.fcitx5.android.plugin.clipboard

import kotlinx.serialization.Serializable

@Serializable
data class ClearURLsProvider(
    val urlPattern: String,
    val completeProvider: Boolean = false,
    val rules: List<String>? = null,
    val rawRules: List<String>? = null,
    val referralMarketing: List<String>? = null,
    val exceptions: List<String>? = null,
    val redirections: List<String>? = null,
    val forceRedirection: Boolean = false
)