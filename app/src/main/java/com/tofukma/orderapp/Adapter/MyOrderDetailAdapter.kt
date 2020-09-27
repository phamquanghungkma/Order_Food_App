package com.tofukma.orderapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tofukma.orderapp.Database.CartItem
import com.tofukma.orderapp.Model.AddonModel
import com.tofukma.orderapp.Model.SizeModel
import com.tofukma.orderapp.R
import java.lang.StringBuilder

class MyOrderDetailAdapter (internal var context: Context,
                            internal var cartItemList:MutableList<CartItem>)
    : RecyclerView.Adapter<MyOrderDetailAdapter.MyViewHolder>() {


    val gson: Gson = Gson()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var txt_food_name: TextView?=null
        var txt_food_size: TextView?=null
        var txt_food_addon: TextView?=null
        var txt_food_quantity: TextView?=null
        var img_food_image: ImageView?=null

        init{
            img_food_image = itemView.findViewById(R.id.img_food_image) as ImageView
            txt_food_name = itemView.findViewById(R.id.txt_food_name) as TextView
            txt_food_size = itemView.findViewById(R.id.txt_size) as TextView
            txt_food_addon = itemView.findViewById(R.id.txt_food_add_on) as TextView
            txt_food_quantity = itemView.findViewById(R.id.txt_food_quantity) as TextView

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
            .inflate(R.layout.layout_order_item_detail,parent,false))
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(cartItemList[position].foodImage)
            .into(holder.img_food_image!!)
        holder.txt_food_name!!.setText(StringBuilder().append(cartItemList[position].foodName))
        holder.txt_food_quantity!!.setText(StringBuilder("Số lượng: ").append(cartItemList[position].foodQuantity))
        val sizeModel: SizeModel = gson.fromJson(cartItemList[position].foodSize,
            object: TypeToken<SizeModel?>(){}.type)
        if(sizeModel != null) holder.txt_food_size!!.setText(StringBuilder("Kích thước: ").append(sizeModel.name))
        if(!cartItemList[position].foodAddon.equals("Default"))
        {
            val addonModels : List<AddonModel> = gson.fromJson(cartItemList[position].foodAddon,
                object: TypeToken<List<AddonModel?>?>(){}.type)
            val addonString = StringBuffer()
            if( addonModels != null)
            {
                for (addonModel in addonModels) addonString.append(addonModel.name).append(",")
                addonString.delete(addonString.length-1,addonString.length) //Remove last ","
                holder.txt_food_addon!!.setText(StringBuilder("Đồ thêm: ").append(addonString))
            }
        }
        else
            holder.txt_food_addon!!.setText(StringBuilder("Đồ thêm: Mặc định"))
    }
}