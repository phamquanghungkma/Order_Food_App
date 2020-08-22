package com.tofukma.orderapp.Adapter

import android.content.Context
import android.media.Image
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tofukma.orderapp.Model.Order
import com.tofukma.orderapp.R
import org.w3c.dom.Text

class MyOrderAdapter(private val context:Context,private val orderList:List<Order>):RecyclerView.Adapter<MyOrderAdapter.MyViewOrder>() {

    inner class MyViewOrder(itemView:View) : RecyclerView.ViewHolder(itemView)
    {
        internal var img_order : ImageView ?= null
        internal var txt_order_date:TextView ?= null
        internal  var txt_order_status:TextView ?= null
        internal var txt_order_number: TextView ?= null
        internal  var txt_order_comment: TextView ?= null

        init {
            img_order = itemView.findViewById(R.id.img_order) as ImageView
            txt_order_comment = itemView.findViewById(R.id.txt_order_comment) as TextView
            txt_order_date = itemView.findViewById(R.id.txt_order_date) as TextView
            txt_order_number = itemView.findViewById(R.id.txt_order_number) as TextView
            txt_order_status = itemView.findViewById(R.id.txt_order_status) as TextView

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewOrder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MyViewOrder, position: Int) {
        TODO("Not yet implemented")
    }


}