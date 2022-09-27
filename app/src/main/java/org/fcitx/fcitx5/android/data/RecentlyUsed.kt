package org.fcitx.fcitx5.android.data

import org.fcitx.fcitx5.android.utils.appContext
import java.io.File

// Not thread-safe
class RecentlyUsed(
    val name: String,
    val capacity: Int
) : LinkedHashMap<String, String>(0, .75f, true) {

    private val file = File(appContext.getExternalFilesDir(null), name).also {
        it.createNewFile()
    }

    fun load() {
        val xs = file.readLines()
        xs.forEach {
            if (it.isNotBlank())
                put(it, it)
        }
    }

    fun save() {
        file.writeText(values.joinToString("\n"))
    }

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>?) =
        size > capacity

    fun insert(s: String) = put(s, s)

    fun toOrderedList() = values.toList().reversed()
}