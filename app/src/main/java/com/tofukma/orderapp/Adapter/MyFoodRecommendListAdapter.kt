package com.tofukma.orderapp.Adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tofukma.orderapp.CallBack.IRecyclerItemClickListener
import com.tofukma.orderapp.Database.CartDataSource
import com.tofukma.orderapp.Database.CartDatabase
import com.tofukma.orderapp.Database.LocalCartDataSource
import com.tofukma.orderapp.EventBus.PopularFoodItemClick
import com.tofukma.orderapp.EventBus.RecommendFoodItemClick
import com.tofukma.orderapp.Model.RecommendModel
import com.tofukma.orderapp.R
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus

class MyFoodRecommendListAdapter (internal var context:Context,
                                  internal  var foodRecommendList: List<RecommendModel>) :
    RecyclerView.Adapter<MyFoodRecommendListAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),  View.OnClickListener {
        var recommendFood_name: TextView ?= null
        var recommendFood_image: ImageView ?= null
        var recommend_img_fav: ImageView? = null
        var recommend_img_cart: ImageView? = null

        internal  var listener: IRecyclerItemClickListener?= null

        fun setListener(listener: IRecyclerItemClickListener){
            this.listener = listener
        }
        init {
            recommendFood_name = itemView.findViewById(R.id.txt_food_recommend_name)
            recommendFood_image = itemView.findViewById(R.id.img_food_recommend_image)
            recommend_img_cart = itemView.findViewById(R.id.img_quick_recommend_cart)
            recommend_img_fav = itemView.findViewById(R.id.img_fav_recommend)

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listener!!.onItemClick(view!!, adapterPosition)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_food_recommend_item,parent,false))
    }

    override fun getItemCount(): Int {
        return foodRecommendList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(foodRecommendList.get(position).image).into(holder.recommendFood_image!!)
        holder.recommendFood_name!!.setText(foodRecommendList.get(position).name)

        holder.setListener(object: IRecyclerItemClickListener{
            override fun onItemClick(view: View, post: Int) {
                EventBus.getDefault()
                    .postSticky(RecommendFoodItemClick(foodRecommendList[position]))
            }

        })

    }
}


