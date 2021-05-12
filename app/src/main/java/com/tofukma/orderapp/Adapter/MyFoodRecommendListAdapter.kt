package com.tofukma.orderapp.Adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tofukma.orderapp.Database.CartDataSource
import com.tofukma.orderapp.Database.CartDatabase
import com.tofukma.orderapp.Database.LocalCartDataSource
import com.tofukma.orderapp.Model.RecommendModel
import io.reactivex.disposables.CompositeDisposable

class MyFoodRecommendListAdapter (internal var context:Context,
                                  internal  var foodRecommendList: List<RecommendModel>) :
    RecyclerView.Adapter<MyFoodRecommendListAdapter.MyViewHolder>() {
    private val compositeDisposable: CompositeDisposable
    private val cartDataSource: CartDataSource

    init {
        compositeDisposable = CompositeDisposable()
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),  View.OnClickListener {
        override fun onClick(v: View?) {

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}