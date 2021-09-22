package com.example.bigstepsampleapp.ui.main.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bigstepsampleapp.R
import com.example.bigstepsampleapp.ui.main.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val tabTitles = arrayOf(
        "Video",
        "History"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
    }



    private fun setupUI() {
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager?.adapter = viewPagerAdapter
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}