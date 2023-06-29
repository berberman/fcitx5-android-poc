package org.fcitx.fcitx5.android.common.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

fun Context.bindFcitxRemoteService(
    debugBuild: Boolean,
    onDisconnected: () -> Unit = {},
    onConnected: (IFcitxRemoteService) -> Unit
): FcitxRemoteConnection {
    val connection = FcitxRemoteConnection(onDisconnected, onConnected)
    bindService(
        Intent().apply {
            val pkgName = "org.fcitx.fcitx5.android" + if (debugBuild) ".debug" else ""
            Log.e("Bind", pkgName)
            setClassName(
                pkgName,
                "${pkgName}.FcitxRemoteService"
            )
        },
        connection,
        Context.BIND_AUTO_CREATE
    )
    return connection
}

open class FcitxRemoteConnection(
    private val onDisconnected: () -> Unit = {},
    private val onConnected: (IFcitxRemoteService) -> Unit
) : ServiceConnection {

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        onConnected(IFcitxRemoteService.Stub.asInterface(service))
    }

    override fun onServiceDisconnected(name: ComponentName) {
        onDisconnected()
    }

}