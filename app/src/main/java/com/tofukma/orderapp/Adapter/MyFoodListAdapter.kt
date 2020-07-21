package com.tofukma.orderapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tofukma.orderapp.Model.FoodModel
import com.tofukma.orderapp.R

class MyFoodListAdapter (internal var context: Context, internal var foodList: List<FoodModel>) : RecyclerView.Adapter<MyFoodListAdapter.MyViewHolder>(){

    inner class MyViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

        var txt_food_name: TextView?= null
        var txt_food_price: TextView?= null

        var img_food_image:ImageView?=null
        var img_fav:ImageView?=null
        var img_cart:ImageView?=null






        init {
            txt_food_name = itemView.findViewById(R.id.txt_food_name) as TextView
            txt_food_price = itemView.findViewById(R.id.txt_food_price) as TextView

            img_food_image = itemView.findViewById(R.id.img_food_image) as ImageView
            img_fav = itemView.findViewById(R.id.img_fav) as ImageView
            img_cart = itemView.findViewById(R.id.img_quick_cart) as ImageView


        }



    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyFoodListAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_food_item,parent,false))

    }

    override fun getItemCount(): Int {
        return foodList.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(foodList.get(position).image).into(holder.img_food_image!!)
        holder.txt_food_name!!.setText(foodList.get(position).name)
        holder.txt_food_price!!.setText(foodList.get(position).price.toString())




    }



}