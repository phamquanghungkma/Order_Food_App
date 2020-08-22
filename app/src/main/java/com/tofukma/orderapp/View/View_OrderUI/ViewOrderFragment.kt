package com.tofukma.orderapp.View.View_OrderUI

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tofukma.orderapp.Adapter.MyOrderAdapter
import com.tofukma.orderapp.CallBack.ILoadOrderCallbackListener
import com.tofukma.orderapp.Model.Order
import com.tofukma.orderapp.R
import com.tofukma.orderapp.Utils.Common
import com.tofukma.orderapp.ViewModel.vieworder.ViewOrderModel
import dmax.dialog.SpotsDialog
import java.util.*
import kotlin.collections.ArrayList

class ViewOrderFragment : Fragment(), ILoadOrderCallbackListener {

    private var viewOrderModel : ViewOrderModel ?= null

    internal lateinit var dialog:AlertDialog

    internal lateinit var recycler_order : RecyclerView

    internal lateinit var listener : ILoadOrderCallbackListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewOrderModel = ViewModelProviders.of(this).get(ViewOrderModel::class.java!!)
        val root = inflater.inflate(R.layout.fragment_view_orders,container,false)

        initView(root)

        loadOrderFromServer()

        viewOrderModel!!.mutableLiveDataOrderList.observe(this, Observer {

            Collections.reverse(it!!)
            val adapter = MyOrderAdapter(context!!,it)
            recycler_order!!.adapter = adapter

        })

        return root
    }

    private fun loadOrderFromServer() {
        dialog.show()

        val orderList  = ArrayList<Order>()

        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
            .orderByChild("userId")
            .equalTo(Common.currentUser!!.uid)
            .limitToLast(100)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                        listener.onLoadOrderFailed(error.message!!)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                        for (orderSnapshot in snapshot.children){
                            val order = orderSnapshot.getValue(Order::class.java)
                            order!!.orderNumber = orderSnapshot.key
                            orderList.add(order!!)

                        }
                    listener.onLoadOrderSucess(orderList)
                }

            })

    }

    private fun initView(root: View?) {
            listener = this
            dialog = SpotsDialog.Builder().setContext(context!!).setCancelable(false).build()

            recycler_order = root!!.findViewById(R.id.recycler_order) as RecyclerView
            recycler_order.setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(requireContext())
            recycler_order.layoutManager = layoutManager
            recycler_order.addItemDecoration(DividerItemDecoration(context!!,layoutManager.orientation))

    }


    override fun onLoadOrderSucess(orderList: ArrayList<Order>) {
            dialog.dismiss()// bo cai dialog loading
            viewOrderModel!!.setMutableLiveDataOrderList(orderList)
    }

    override fun onLoadOrderFailed(message: String) {
        dialog.dismiss()
    }


}