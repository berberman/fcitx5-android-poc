package me.rocka.fcitx5test.ui.setup

import android.content.Context
import me.rocka.fcitx5test.R
import me.rocka.fcitx5test.utils.InputMethodUtil

enum class SetupPage {
    Enable, Select;

    fun getHintText(context: Context) = context.getString(
        when (this) {
            Enable -> R.string.enable_ime_hint
            Select -> R.string.select_ime_hint
        }
    )

    fun getButtonText(context: Context) = context.getString(
        when (this) {
            Enable -> R.string.enable_ime
            Select -> R.string.select_ime
        }
    )

    fun getButtonAction(context: Context) = when (this) {
        Enable -> InputMethodUtil.startSettingsActivity(context)
        Select -> InputMethodUtil.showSelector(context)
    }

    fun isDone() = when (this) {
        Enable -> InputMethodUtil.isEnabled()
        Select -> InputMethodUtil.isSelected()
    }
}