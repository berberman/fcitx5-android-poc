package me.rocka.fcitx5test.ui.setup

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import me.rocka.fcitx5test.R
import me.rocka.fcitx5test.databinding.ActivitySetupBinding

class SetupActivity : FragmentActivity() {

    private lateinit var viewPager: ViewPager2

    private val viewModel: SetupViewModel by viewModels()

    private lateinit var prevButton: Button
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prevButton = binding.prevButton.apply {
            text = getString(R.string.prev)
            setOnClickListener { viewPager.currentItem = viewPager.currentItem - 1 }
        }
        nextButton = binding.nextButton.apply {
            setOnClickListener {
                if (viewPager.currentItem != SetupPage.values().size - 1)
                    viewPager.currentItem = viewPager.currentItem + 1
                else finishActivity(0)
            }
        }
        viewPager = binding.viewpager
        viewPager.adapter = Adapter()
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                prevButton.visibility = if (position != 0) View.VISIBLE else View.GONE
                nextButton.text =
                    getString(
                        if (position == SetupPage.values().size - 1)
                            R.string.done else R.string.next
                    )
                // manually call following observer when page changed
                viewModel.isAllDone.postValue(viewModel.isAllDone.value)
            }
        })
        viewModel.isAllDone.observe(this) {
            nextButton.apply {
                (it || viewPager.currentItem != SetupPage.values().size - 1).let {
                    visibility = if (it) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private inner class Adapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = SetupPage.values().size

        override fun createFragment(position: Int): Fragment =
            SetupFragment(SetupPage.values()[position])

    }
}