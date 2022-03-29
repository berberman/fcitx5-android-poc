package org.fcitx.fcitx5.android.input.clipboard

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.launch
import org.fcitx.fcitx5.android.R
import org.fcitx.fcitx5.android.data.Prefs
import org.fcitx.fcitx5.android.data.clipboard.ClipboardManager
import org.fcitx.fcitx5.android.input.FcitxInputMethodService
import org.fcitx.fcitx5.android.input.clipboard.ClipboardStateMachine.State.*
import org.fcitx.fcitx5.android.input.clipboard.ClipboardStateMachine.TransitionEvent.*
import org.fcitx.fcitx5.android.input.dependency.inputMethodService
import org.fcitx.fcitx5.android.input.wm.InputWindow
import org.fcitx.fcitx5.android.utils.EventStateMachine
import org.fcitx.fcitx5.android.utils.inputConnection
import kotlin.properties.Delegates

class ClipboardWindow : InputWindow.ExtendedInputWindow<ClipboardWindow>() {

    private val service: FcitxInputMethodService by manager.inputMethodService()

    private lateinit var stateMachine: EventStateMachine<ClipboardStateMachine.State, ClipboardStateMachine.TransitionEvent>

    private var isClipboardDbEmpty by Delegates.observable(ClipboardManager.itemCount == 0) { _, _, new ->
        stateMachine.push(
            if (new) ClipboardDbUpdatedEmpty
            else ClipboardDbUpdatedNonEmpty
        )
    }

    private val clipboardEnabledListener = Prefs.OnChangeListener<Boolean> {
        stateMachine.push(
            if (value)
                if (isClipboardDbEmpty) ClipboardListeningEnabledWithDbEmpty
                else ClipboardListeningEnabledWithDbNonEmpty
            else ClipboardListeningDisabled
        )
    }

    private val clipboardEnabledPref = Prefs.getInstance().clipboardListening

    private fun updateClipboardEntries() {
        service.lifecycleScope.launch {
            ClipboardManager.getAll().also {
                isClipboardDbEmpty = it.isEmpty()
                adapter.updateEntries(it)
            }
        }
    }

    private val onClipboardUpdateListener = ClipboardManager.OnClipboardUpdateListener {
        updateClipboardEntries()
    }

    private val adapter: ClipboardAdapter by lazy {
        object : ClipboardAdapter() {
            override suspend fun onPin(id: Int) = ClipboardManager.pin(id)
            override suspend fun onUnpin(id: Int) = ClipboardManager.unpin(id)
            override suspend fun onDelete(id: Int) = ClipboardManager.delete(id)
            override fun onPaste(id: Int) {
                service.inputConnection?.commitText(getEntryById(id).text, 1)
            }
        }
    }

    private val ui by lazy {
        ClipboardUi(context).apply {
            recyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                adapter = this@ClipboardWindow.adapter
            }
            enableUi.enableButton.setOnClickListener {
                clipboardEnabledPref.value = true
            }
            deleteAllButton.setOnClickListener {
                service.lifecycleScope.launch {
                    ClipboardManager.deleteAll()
                    adapter.updateEntries(emptyList())
                    isClipboardDbEmpty = true
                }
            }
        }
    }

    override val view by lazy {
        ui.root
    }

    override fun onAttached() {
        val initialState = when {
            !clipboardEnabledPref.value -> EnableListening
            isClipboardDbEmpty -> AddMore
            else -> Normal
        }
        stateMachine = ClipboardStateMachine.new(initialState) {
            ui.switchUiByState(it)
        }
        // manually switch to initial ui
        ui.switchUiByState(initialState)
        // manually sync clipboard entries form db
        updateClipboardEntries()
        clipboardEnabledPref.registerOnChangeListener(clipboardEnabledListener)
        ClipboardManager.addOnUpdateListener(onClipboardUpdateListener)
    }

    override fun onDetached() {
        clipboardEnabledPref.unregisterOnChangeListener(clipboardEnabledListener)
        ClipboardManager.removeOnUpdateListener(onClipboardUpdateListener)
    }

    override val title: String by lazy {
        context.getString(R.string.clipboard)
    }

    override val barExtension: View by lazy {
        ui.extension
    }
}