package com.wmagliano.mastertools

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsPagerAdapter(
    activity: AppCompatActivity,
    private val tabs: List<String>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = tabs.size

    override fun createFragment(position: Int): Fragment {
        return if (tabs[position] == "About") {
            AboutFragment()
        } else {
            ToolFragment.newInstance(tabs[position])
        }
    }
}