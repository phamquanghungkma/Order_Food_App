package com.tofukma.orderapp.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.asksira.loopingviewpager.LoopingViewPager
import com.tofukma.orderapp.Adapter.MyBestDealsAdapter
import com.tofukma.orderapp.Adapter.MyCategoriesAdapter

import com.tofukma.orderapp.Adapter.MyPopularCategoriesAdapter
import com.tofukma.orderapp.Common.Common
import com.tofukma.orderapp.Common.SpacesItemDecoration
import com.tofukma.orderapp.R
import com.tofukma.orderapp.ui.menu.MenuViewModel
import dmax.dialog.SpotsDialog

class HomeFragment : Fragment() {

    private var adapter: MyCategoriesAdapter?= null
    private lateinit var dialog: AlertDialog
    private var recycler_menu: RecyclerView?= null


    private lateinit var homeViewModel: HomeViewModel
    private lateinit var menuViewModel: MenuViewModel




    var recyclerView:RecyclerView ?= null
    var viewPager:LoopingViewPager ?= null

    var layoutAnimationController:LayoutAnimationController ?= null



//    var unbinder: Unbinder?= null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        menuViewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home_2, container, false)

//        unbinder = ButterKnife.bind(this,root)
        initView(root)
        // Bind Data
        homeViewModel.popularList.observe(this, Observer {
            val listData = it
            val adapter = MyPopularCategoriesAdapter(context!!,listData)
            recyclerView!!.adapter = adapter
            recyclerView!!.layoutAnimation = layoutAnimationController
        })

        homeViewModel.bestDealList.observe(this, Observer {

            val adapter = MyBestDealsAdapter(context!!,it,false)
            viewPager!!.adapter = adapter

        })

        menuViewModel.getCategoryList().observe(this, Observer {
            dialog.dismiss()
            adapter = MyCategoriesAdapter(context!!,it)
            recycler_menu!!.adapter = adapter
            recycler_menu!!.layoutAnimation = layoutAnimationController
        })

        return root
    }

    private fun initView(root:View) {
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        recycler_menu = root.findViewById(R.id.recycler_menu2) as RecyclerView
        recycler_menu!!.setHasFixedSize(true)
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2,RecyclerView.VERTICAL)
        val layoutManager = GridLayoutManager(context,2)
        layoutManager.orientation = RecyclerView.VERTICAL
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return  if(adapter != null){
                    when(adapter!!.getItemViewType(position)){
                        Common.DEFAULT_COLUMN_COUNT -> 1
                        Common.FULL_WIDTH_COLUMN -> 2
                        else -> 1
                    }
                }
                else {
                    -1
                }
            }
        }
        recycler_menu!!.layoutManager = staggeredGridLayoutManager
        recycler_menu!!.addItemDecoration(
            SpacesItemDecoration(
                8
            )
        )


        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_item_from_left)
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