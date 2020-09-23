package com.tofukma.orderapp.View.FoodListUI

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tofukma.orderapp.Adapter.MyFoodListAdapter
import com.tofukma.orderapp.EventBus.MenuItemBack
import com.tofukma.orderapp.Model.CategoryModel
import com.tofukma.orderapp.Model.FoodModel
import com.tofukma.orderapp.Utils.Common
import com.tofukma.orderapp.R
import com.tofukma.orderapp.ViewModel.foodlist.FoodListViewModel
import org.greenrobot.eventbus.EventBus

class FoodListFragment : Fragment() {

    private lateinit var foodListViewModel: FoodListViewModel

    var recycler_food_list : RecyclerView  ?= null
    var layoutAnimationController:LayoutAnimationController?= null

    var adapter : MyFoodListAdapter ?= null

    override fun onStop() {
        if(adapter !=null)
            adapter!!.onStop()
        super.onStop()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        foodListViewModel =
//            ViewModelProviders.of(this).get(FoodListViewModel::class.java)
            ViewModelProviders.of(this).get(FoodListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_food_list, container, false)
        initViews(root)
        foodListViewModel.getMutableFoodModelListData().observe(viewLifecycleOwner, Observer {
       if(it != null){
           adapter =  MyFoodListAdapter(requireContext(),it)
           recycler_food_list!!.adapter = adapter
           recycler_food_list!!.layoutAnimation = layoutAnimationController
       }
        })
        return root
    }

    private fun initViews(root: View?) {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar!!.setTitle(Common.categorySelected!!.name)
        recycler_food_list = root!!.findViewById(R.id.recycler_food_list) as RecyclerView
        recycler_food_list!!.setHasFixedSize(true)
        recycler_food_list!!.layoutManager = LinearLayoutManager(context)

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_item_from_left)


        (activity as AppCompatActivity).supportActionBar!!.title = Common.categorySelected!!.name
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu,menu)
        val menuItem = menu.findItem(R.id.action_search)
        val searchManager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menuItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))
        //Event
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(s: String?): Boolean {
                startSearch(s!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        //Clear text when click to clear button on Search View
        val closeButton = searchView.findViewById<View>(R.id.search_close_btn) as ImageView
        closeButton.setOnClickListener{
            val ed  = searchView.findViewById<View>(R.id.search_src_text) as EditText
            //clear text
            ed.setText(" ")
            //clearQuery
            searchView.setQuery("",false)
            //collapse the search widget
            searchView.onActionViewCollapsed()

            menuItem.collapseActionView()
            //Restore result to original
            foodListViewModel.getMutableFoodModelListData()
        }
    }
    private fun startSearch(s:String){
        val resultFood = ArrayList<FoodModel>()
        for (i in 0 until Common.categorySelected!!.foods!!.size){
            val categoryModel = Common.categorySelected!!.foods!![i]
            if(categoryModel.name!!.toLowerCase().contains(s))
                resultFood.add(categoryModel)
        }
        foodListViewModel.getMutableFoodModelListData().value = resultFood
    }
    override fun onDestroy() {
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()

    }
}