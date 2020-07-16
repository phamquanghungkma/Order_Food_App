package com.tofukma.orderapp.ui.home

import android.os.Bundle
import android.service.media.MediaBrowserService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.asksira.loopingviewpager.LoopingViewPager
import com.tofukma.orderapp.Adapter.MyBestDealsAdapter

import com.tofukma.orderapp.Adapter.MyPopularCategoriesAdapter
import com.tofukma.orderapp.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    var recyclerView:RecyclerView ?= null
    var viewPager:LoopingViewPager ?= null


//    var unbinder: Unbinder?= null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

//        unbinder = ButterKnife.bind(this,root)
        initView(root)
        // Bind Data
        homeViewModel.popularList.observe(this, Observer {
            val listData = it
            val adapter = MyPopularCategoriesAdapter(context!!,listData)
            recyclerView!!.adapter = adapter
        })

        homeViewModel.bestDealList.observe(this, Observer {

            val adapter = MyBestDealsAdapter(context!!,it,false)
            viewPager!!.adapter = adapter

        })

        return root
    }

    private fun initView(root:View) {
        viewPager = root.findViewById(R.id.viewpaper) as LoopingViewPager
        recyclerView = root.findViewById(R.id.recycler_popular) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)

    }

    override fun onResume() {
        super.onResume()
        viewPager!!.resumeAutoScroll()
    }

    override fun onPause() {
        viewPager!!.pauseAutoScroll()
        super.onPause()

    }
}