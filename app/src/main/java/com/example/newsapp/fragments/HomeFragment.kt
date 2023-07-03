package com.example.newsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.newsapp.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

//Home Fragment used for switching between non search news pages
class HomeFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments =
        listOf(
            FypFragment(),
            TopTenFragment(),
            LocalFragment(),
            SpotlightFragment()
        )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}

class HomeFragment : Fragment() {

    private lateinit var homeAdapter: HomeFragmentAdapter
    private lateinit var viewPager2: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)

        homeAdapter = HomeFragmentAdapter(this)
        viewPager2 = view.findViewById(R.id.viewPager2)
        viewPager2.adapter = homeAdapter

        //Setting the text of each tab in the layout and moving to the fragment at that index when
        //its highlighted
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "For You"
                1 -> tab.text = "Top 10"
                2 -> tab.text = "Local"
                3 -> tab.text = "Spotlight"
            }
        }.attach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}