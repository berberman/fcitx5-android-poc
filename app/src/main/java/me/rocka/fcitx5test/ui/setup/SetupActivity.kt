package me.rocka.fcitx5test.ui.setup

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import me.rocka.fcitx5test.databinding.ActivitySetupBinding

class SetupActivity : FragmentActivity() {

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewPager = binding.viewpager
        viewPager.adapter = Adapter()
    }

    private inner class Adapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = SetupPage.values().size

        override fun createFragment(position: Int): Fragment =
            SetupFragment(SetupPage.values()[position])

    }
}