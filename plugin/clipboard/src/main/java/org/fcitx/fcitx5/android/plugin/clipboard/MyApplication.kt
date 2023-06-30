package org.fcitx.fcitx5.android.plugin.clipboard

import android.app.Application
import org.fcitx.fcitx5.android.common.ipc.FcitxRemoteConnection
import org.fcitx.fcitx5.android.common.ipc.IClipboardEntryTransformer
import org.fcitx.fcitx5.android.common.ipc.bindFcitxRemoteService

class MyApplication : Application() {

    private lateinit var connection: FcitxRemoteConnection

    override fun onCreate() {
        super.onCreate()
        ClearURLs.initCatalog(assets)
        val transformer = object : IClipboardEntryTransformer.Stub() {
            override fun getPriority(): Int = 100

            override fun transform(clipboardText: String): String =
                ClearURLs.transform(clipboardText)

            override fun getDescription(): String = "test"
        }
        connection = applicationContext.bindFcitxRemoteService(BuildConfig.BUILD_TYPE == "debug") {
            it.registerClipboardEntryTransformer(transformer)
        }
    }
}
