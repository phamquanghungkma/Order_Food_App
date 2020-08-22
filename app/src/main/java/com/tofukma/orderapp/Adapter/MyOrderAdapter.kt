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
import com.tofukma.orderapp.Model.Order
import com.tofukma.orderapp.R
import com.tofukma.orderapp.Utils.Common
import kotlinx.android.synthetic.main.layout_order_item.view.*
import org.w3c.dom.Text
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class MyOrderAdapter(private val context:Context,private val orderList:List<Order>):
    RecyclerView.Adapter<MyOrderAdapter.MyViewHolder>() {

    internal  var calender : Calendar
    internal  var simpleDataFormat: SimpleDateFormat
    init {
        calender = Calendar.getInstance()
        simpleDataFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context!!)
            .inflate(R.layout.layout_order_item,parent,false))
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context!!)
            .load(orderList[position].carItemList!![0].foodImage)
            .into(holder.img_order!!)

        calender.timeInMillis = orderList[position].createDate!!

        val date = orderList[position].createDate

        holder.txt_order_date!!.text = StringBuilder(Common.getDateOfWeek(calender.get(Calendar.DAY_OF_WEEK)))
            .append(" ")
            .append(simpleDataFormat.format(date))
        holder.txt_order_number!!.text = StringBuilder("Số đơn hàng: ").append(orderList[position].orderNumber)
        holder.txt_order_comment!!.text = StringBuilder("Bình luận: ").append(orderList[position].comment)
        holder.txt_order_status!!.text = StringBuilder("Trạng thái: ").append(Common.convertStatusToText(orderList[position].orderStatus))
    }






    inner class MyViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView)
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

}