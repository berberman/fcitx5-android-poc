package me.rocka.fcitx5test.ui.setup

import android.content.Context
import android.content.Intent
import android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS
import android.view.inputmethod.InputMethodManager
import me.rocka.fcitx5test.R

enum class SetupPage {
    Enable, Select;

    fun getHintText(context: Context) = context.getString(
        when (this) {
            Enable -> R.string.enable_ime
            Select -> R.string.select_ime
        }
    )

    fun getButtonAction(context: Context) = when (this) {
        Enable -> context.startActivity(Intent(ACTION_INPUT_METHOD_SETTINGS))
        Select -> context.getSystemService(InputMethodManager::class.java).showInputMethodPicker()
    }
}