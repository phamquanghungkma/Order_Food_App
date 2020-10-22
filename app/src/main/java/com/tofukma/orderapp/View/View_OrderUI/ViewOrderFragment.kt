package com.tofukma.orderapp.View.View_OrderUI

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.sip.SipSession
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidwidgets.formatedittext.widgets.FormatEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.tofukma.orderapp.Adapter.MyOrderAdapter
import com.tofukma.orderapp.CallBack.ILoadOrderCallbackListener
import com.tofukma.orderapp.CallBack.IMyButtonCallback
import com.tofukma.orderapp.Database.CartDataSource
import com.tofukma.orderapp.Database.CartDatabase
import com.tofukma.orderapp.Database.LocalCartDataSource
import com.tofukma.orderapp.EventBus.CountCartEvent
import com.tofukma.orderapp.EventBus.MenuItemBack
import com.tofukma.orderapp.Model.Order
import com.tofukma.orderapp.Model.ShippingOrderModel
import com.tofukma.orderapp.R
import com.tofukma.orderapp.TrackingOrderActivity
import com.tofukma.orderapp.Utils.Common
import com.tofukma.orderapp.Utils.MySwipeHelper
import com.tofukma.orderapp.ViewModel.vieworder.RefundRequestModel
import com.tofukma.orderapp.ViewModel.vieworder.ViewOrderModel
import dmax.dialog.SpotsDialog
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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

    lateinit var cartDataSource:CartDataSource
    var compositeDisposable = CompositeDisposable()
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

        FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
            .child(Common.currentRestaurant!!.uid)
            .child(Common.ORDER_REF)
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
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context!!).cartDAO())
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
                                if(orderModel.isCod){
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
                                }
                                else{
                                    val view = LayoutInflater.from(context!!)
                                        .inflate(R.layout.layout_refund_request,null)

                                    val edt_name = view.findViewById<EditText>(R.id.edt_card_name)
                                    val edt_card_number = view.findViewById<FormatEditText>(R.id.edt_card_number)
                                    val edt_card_exp = view.findViewById<FormatEditText>(R.id.edt_exp)

                                    //set Format

                                    edt_card_number.setFormat("---- ---- ---- ----")
                                    edt_card_exp.setFormat("--/--")

                                    val builder = androidx.appcompat.app.AlertDialog.Builder(context!!)
                                    builder.setTitle("Cancel Order")
                                        .setMessage("Ban thuc su muon huy don hang?")
                                        .setView(view)
                                        .setNegativeButton("Khong"){dialogInterface, i ->
                                            dialogInterface.dismiss()
                                        }
                                        .setPositiveButton("Co"){dialog: DialogInterface?,i->

                                          val refundRequestModel = RefundRequestModel()
                                            refundRequestModel.name = Common.currentUser!!.name!!
                                            refundRequestModel.phone = Common.currentUser!!.phone!!
                                            refundRequestModel.cardNumber = edt_card_number.text.toString()
                                            refundRequestModel.cardExp = edt_card_exp.text.toString()
                                            refundRequestModel.amount = orderModel.finalPayment
                                            refundRequestModel.cardName = edt_name.text.toString()

                                            FirebaseDatabase.getInstance()
                                                .getReference(Common.REFUND_REQUEST_REF)
                                                .child(orderModel.orderNumber!!)
                                                .setValue(refundRequestModel)
                                                .addOnFailureListener{e ->
                                                    Toast.makeText(context!!,e.message,Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                .addOnSuccessListener {
                                                    //update data firebase
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
                                        }
                                    val dialog = builder.create()
                                    dialog.show()
                                }

                            }
                            else{
                                Toast.makeText(context!!,StringBuilder("Trang thai don hang cua ban da duoc thay doi ")
                                    .append(Common.convertStatusToText(orderModel.orderStatus))
                                    .append(" ,nen ban ko the huy no"),Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }))

                //Tracking Buuton

                buffer.add(MyButton(context!!,
                    "Tracking Order ",
                    30,
                    0,
                    Color.parseColor("#001970"),
                    object: IMyButtonCallback {
                        override fun onClick(pos: Int) {
                            val orderModel = (recycler_order.adapter as MyOrderAdapter).getItemAtPosition(pos)
                            //Log.d("Test",orderModel.orderNumber)
                                FirebaseDatabase.getInstance()
                                    .getReference(Common.SHIPPING_ORDER_REF)
                                    .child(orderModel.orderNumber!!)
                                    .addListenerForSingleValueEvent(object:ValueEventListener{
                                        override fun onCancelled(p0: DatabaseError) {
                                        Toast.makeText(context!!,p0.message,Toast.LENGTH_SHORT).show()
                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                         //   Log.d("TEst", p0.exists().toString())
                                        if(p0.exists()){
                                        Common.currentShippingOrder = p0.getValue(ShippingOrderModel::class.java)
                                            Common.currentShippingOrder!!.key = p0.key
                                            if(Common.currentShippingOrder!!.currentLat!! != -1.0
                                                && Common.currentShippingOrder!!.currentLng!! != -1.0){
                                            startActivity(Intent(context!!, TrackingOrderActivity::class.java))
                                            }else{
                                                Toast.makeText(context!!,"Don hang cua ban chua duoc van chuyen, vui long cho doi",Toast.LENGTH_SHORT).show()
                                            }
                                        }else{
                                            Toast.makeText(context!!,"Ban vua dat hang ! Don hang cua ban se duoc giao sau it phut",Toast.LENGTH_SHORT).show()
                                        }
                                        }
                                    })
                        }
                    }))

             //Repeat Order
                buffer.add(MyButton(context!!,
                    "Repeat ",
                    30,
                    0,
                    Color.parseColor("#5d4037"),
                    object: IMyButtonCallback {
                        override fun onClick(pos: Int) {
                            val orderModel = (recycler_order.adapter as MyOrderAdapter).getItemAtPosition(pos)
                            //Log.d("Test",orderModel.orderNumber)
                            dialog.show()
                        //First clear all item in cart
                            cartDataSource.cleanCart(Common.currentUser!!.uid!!, Common.currentRestaurant!!.uid)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object:SingleObserver<Int>{
                                    override fun onSubscribe(d: Disposable) {
                                    //After cleans cart just add new
                                        val cartItems = orderModel.carItemList!!.toTypedArray()
                                        compositeDisposable.add(
                                            cartDataSource.insertOrReplaceAll(*cartItems) //mean wwe insert varanger item
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe({
                                                    dialog.dismiss()
                                                    EventBus.getDefault().postSticky(CountCartEvent(true))//Update
                                                    Toast.makeText(context!!,"Them tat ca san pham vao gio hang thanh cong",Toast.LENGTH_SHORT).show()
                                                },{
                                                    t:Throwable?->
                                                    dialog.dismiss()
                                                    Toast.makeText(context!!,"abc Them tat ca san pham vao gio hang thanh cong",Toast.LENGTH_LONG).show()
                                                    if (t != null) {
                                                        t.printStackTrace()
                                                    }
                                                })
                                        )
                                    }

                                    override fun onSuccess(t: Int) {

                                    }

                                    override fun onError(e: Throwable) {
                                    dialog.dismiss()
                                        Toast.makeText(context!!,e.message!!,Toast.LENGTH_LONG).show()
                                    }

                                })
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
        compositeDisposable.clear()
        super.onDestroy()

    }


}