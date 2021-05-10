package com.tofukma.orderapp.View.HomeUI

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.asksira.loopingviewpager.LoopingViewPager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tofukma.orderapp.Adapter.MyBestDealsAdapter
import com.tofukma.orderapp.Adapter.MyCategoriesBestAdatper

import com.tofukma.orderapp.Adapter.MyPopularCategoriesAdapter
import com.tofukma.orderapp.EventBus.MenuItemBack
import com.tofukma.orderapp.Utils.Common
import com.tofukma.orderapp.Utils.SpacesItemDecoration
import com.tofukma.orderapp.R
import com.tofukma.orderapp.ViewModel.home.HomeViewModel
import com.tofukma.orderapp.ViewModel.menu.MenuViewModel
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.fragment_home_2.*
import org.greenrobot.eventbus.EventBus
import org.w3c.dom.Text
import java.time.LocalDate
import kotlin.reflect.typeOf

class HomeFragment : Fragment() {

    private var adapter: MyCategoriesBestAdatper?= null
    private lateinit var dialog: AlertDialog
    private var recycler_menu: RecyclerView?= null


    private lateinit var homeViewModel: HomeViewModel
    private lateinit var menuViewModel: MenuViewModel

    var recyclerView:RecyclerView ?= null
    var viewPager:LoopingViewPager ?= null
    var recommendTextView: TextView ?= null

    var layoutAnimationController:LayoutAnimationController ?= null

//    var unbinder: Unbinder?= null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        menuViewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home_2, container, false)


        val key = arguments!!.getString("restaurant")

//        unbinder = ButterKnife.bind(this,root)
        initView(root)

        // Bind Data
        homeViewModel.getPopularList(key).observe(this, Observer {
            val listData = it
            val adapter = MyPopularCategoriesAdapter(context!!,listData)
            recyclerView!!.adapter = adapter
            recyclerView!!.layoutAnimation = layoutAnimationController
        })

        homeViewModel.getBestDealList(key).observe(this, Observer {
            val adapter = MyBestDealsAdapter(context!!,it,false)
            viewPager!!.adapter = adapter
            viewPager!!.layoutAnimation = layoutAnimationController

        })
//        homeViewModel.getRecommenndList().observe(this, Observer {
//            Log.d("Call123","Recommend List")
//        })
        homeViewModel.loadRecommendList()
        homeViewModel.listRecomnend.observe(this, Observer {
            //data lay dc o day roi nha
           //xong roi
           //
            if(it.isNullOrEmpty()){
                Log.d("HomeFr","data null")
                recommendTextView!!.visibility = View.INVISIBLE


            } else {
                Log.d("HomeFr",it.toString())
            }


        })

        // Binding data hay là lắng nghe sự thay đổi dữ liệu rồi truyển vào view
        menuViewModel.getCategoryBestList().observe(this, Observer {
            dialog.dismiss()
            adapter = MyCategoriesBestAdatper(context!!,it)
            recycler_menu!!.adapter = adapter
            recycler_menu!!.layoutAnimation = layoutAnimationController
        })

        return root
    }

    private fun loadRecommendation(){
        FirebaseDatabase.getInstance().getReference(Common.RECOMMENDATION_REF)
            .child(Common.currentRestaurant!!.uid)
            .child(Common.RECOMMENDATION_REF)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("Loi",p0.toString())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for(item in p0!!.children){
                        Log.d("Data",item.toString())
                    }

                }

            })
    }

    private fun initView(root:View) {
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        recycler_menu = root.findViewById(R.id.recycler_menu2) as RecyclerView
        recycler_menu!!.setHasFixedSize(true)
        recommendTextView = root.findViewById(R.id.recommendTV) as TextView
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
        recommendTextView


    }

    override fun onResume() {
        super.onResume()
        viewPager!!.resumeAutoScroll()
    }

    override fun onPause() {
        viewPager!!.pauseAutoScroll()
        super.onPause()

    }
    override fun onDestroy() {
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()

    }
}