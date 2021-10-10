package kz.gvsx.tou

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kz.gvsx.tou.databinding.FragmentHomeViewPagerBinding
import kz.gvsx.tou.ui.news.NewsFragment
import kz.gvsx.tou.ui.notifications.NotificationsFragment

private const val NUM_PAGES = 2

@AndroidEntryPoint
class HomeViewPagerFragment : Fragment(R.layout.fragment_home_view_pager) {

    private val binding: FragmentHomeViewPagerBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = PagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)

        val tabPositionToTitle =
            mapOf(0 to getString(R.string.tab_news), 1 to getString(R.string.tab_notifications))

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabPositionToTitle[position]
        }.attach()
    }

    private inner class PagerAdapter(fragment: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragment, lifecycle) {
        override fun getItemCount(): Int = NUM_PAGES
        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> NewsFragment()
            1 -> NotificationsFragment()
            else -> throw IndexOutOfBoundsException("Not valid position for HomeViewPagerFragment.PagerAdapter.createFragment()")
        }
    }
}