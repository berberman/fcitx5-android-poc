package me.rocka.fcitx5test.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.rocka.fcitx5test.databinding.FragmentSetupBinding

class SetupFragment(private val page: SetupPage) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSetupBinding.inflate(inflater)
        with(binding) {
            hintText.text = page.getHintText(requireContext())
            actionButton.text = page.getButtonText(requireContext())
            actionButton.setOnClickListener { page.getButtonAction(requireContext()) }
        }
        return binding.root
    }
}