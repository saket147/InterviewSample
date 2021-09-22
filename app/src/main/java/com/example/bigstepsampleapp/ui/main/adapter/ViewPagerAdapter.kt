package com.example.bigstepsampleapp.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bigstepsampleapp.ui.main.view.fragment.HistoryFragment
import com.example.bigstepsampleapp.ui.main.view.fragment.VideosFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return VideosFragment()
            1 -> return HistoryFragment()
        }
        return VideosFragment()
    }
}