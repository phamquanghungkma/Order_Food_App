package com.tofukma.orderapp.View.View_OrderUI

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.tofukma.orderapp.CallBack.IMyButtonCallback
import com.tofukma.orderapp.EventBus.CountCartEvent
import com.tofukma.orderapp.EventBus.MenuItemBack
import com.tofukma.orderapp.Model.Order
import com.tofukma.orderapp.R
import com.tofukma.orderapp.Utils.Common
import com.tofukma.orderapp.Utils.MySwipeHelper
import com.tofukma.orderapp.ViewModel.vieworder.ViewOrderModel
import dmax.dialog.SpotsDialog
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
            val adapter = MyOrderAdapter(context!!,it!!.toMutableList())
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
        val swipe = object: MySwipeHelper(context!!, recycler_order!!, 250)  {
            override fun instantianteMyButton( viewHolder: RecyclerView.ViewHolder, buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(context!!,
                    "Huy Don",
                    30,
                    0,
                    Color.parseColor("#FF3C30"),
                    object: IMyButtonCallback {
                        override fun onClick(pos: Int) {
                            val orderModel = (recycler_order.adapter as MyOrderAdapter).getItemAtPosition(pos)
                            if(orderModel.orderStatus == 0){
                            val builder = androidx.appcompat.app.AlertDialog.Builder(context!!)
                                builder.setTitle("Cancel Order")
                                    .setMessage("Ban thuc su muon huy don hang?")
                                    .setNegativeButton("Khong"){dialogInterface, i ->
                                        dialogInterface.dismiss()
                                    }
                                    .setPositiveButton("Co"){dialog: DialogInterface?,i->
                                        val update_data = HashMap<String,Any>()
                                        update_data.put("orderStatus",-1) //cancel order
                                        FirebaseDatabase.getInstance()
                                            .getReference(Common.ORDER_REF)
                                            .child(orderModel.orderNumber!!)
                                            .updateChildren(update_data)
                                            .addOnFailureListener{e ->
                                                Toast.makeText(context!!,e.message,Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .addOnSuccessListener {
                                                orderModel.orderStatus = -1 //Local update
                                              (recycler_order.adapter as MyOrderAdapter).setItemAtPosition(pos,orderModel)
                                                (recycler_order.adapter as MyOrderAdapter).notifyItemChanged(pos) //update
                                                Toast.makeText(context!!,"Huy don hang thanh cong",Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    val dialog = builder.create()
                                    dialog.show()

                            }else{
                                Toast.makeText(context!!,StringBuilder("Trang thai don hang cua ban da duoc thay doi ")
                                    .append(Common.convertStatusToText(orderModel.orderStatus))
                                    .append(" ,nen ban ko the huy no"),Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }))
            }
        }
    }


    override fun onLoadOrderSucess(orderList: ArrayList<Order>) {
            dialog.dismiss()// bo cai dialog loading
            viewOrderModel!!.setMutableLiveDataOrderList(orderList)
    }

    override fun onLoadOrderFailed(message: String) {
        dialog.dismiss()
    }
    override fun onDestroy() {
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()

    }


}