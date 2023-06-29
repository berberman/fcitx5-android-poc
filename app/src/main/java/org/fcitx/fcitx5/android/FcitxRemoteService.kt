package org.fcitx.fcitx5.android

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import org.fcitx.fcitx5.android.common.ipc.IClipboardEntryTransformer
import org.fcitx.fcitx5.android.common.ipc.IFcitxRemoteService
import org.fcitx.fcitx5.android.core.data.DataManager
import org.fcitx.fcitx5.android.daemon.FcitxDaemon
import org.fcitx.fcitx5.android.data.clipboard.ClipboardManager
import org.fcitx.fcitx5.android.utils.Const
import timber.log.Timber
import java.util.PriorityQueue

class FcitxRemoteService : Service() {

    private val clipboardTransformers =
        PriorityQueue<IClipboardEntryTransformer>(3, compareByDescending { it.priority })

    private fun updateClipboardManager() {
        ClipboardManager.transformer = if (clipboardTransformers.isEmpty()) null else { s ->
            var x = s
            clipboardTransformers.forEach { x = it.transform(x) }
            x
        }
    }

    private val binder = object : IFcitxRemoteService.Stub() {
        override fun getVersionName(): String = Const.versionName

        override fun getPid(): Int = Process.myPid()

        override fun getLoadedPlugins(): MutableMap<String, String> =
            DataManager.getLoadedPlugins().map {
                it.packageName to it.versionName
            }.let { mutableMapOf<String, String>().apply { putAll(it) } }

        override fun restartFcitx() {
            FcitxDaemon.restartFcitx()
        }

        override fun registerClipboardEntryTransformer(transformer: IClipboardEntryTransformer?) {
            transformer?.let { clipboardTransformers.add(it) }
            updateClipboardManager()
        }

        override fun unregisterClipboardEntryTransformer(transformer: IClipboardEntryTransformer?) {
            transformer?.let { clipboardTransformers.remove(it) }
            updateClipboardManager()
        }

    }

    override fun onBind(intent: Intent?): IBinder {
        Timber.d("onBind: $intent")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.d("onUnbind: $intent")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
    }
}