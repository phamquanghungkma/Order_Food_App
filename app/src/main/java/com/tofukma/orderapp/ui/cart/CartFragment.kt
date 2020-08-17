package com.tofukma.orderapp.ui.cart

import android.app.AlertDialog
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asksira.loopingviewpager.LoopingViewPager
import com.google.android.gms.common.internal.Objects
import com.google.android.gms.location.*
import com.google.firebase.database.FirebaseDatabase
import com.tofukma.orderapp.Adapter.MyCartAdapter
import com.tofukma.orderapp.CallBack.IMyButtonCallback
import com.tofukma.orderapp.Common.Common
import com.tofukma.orderapp.Common.MySwipeHelper
import com.tofukma.orderapp.Database.CartDataSource
import com.tofukma.orderapp.Database.CartDatabase
import com.tofukma.orderapp.Database.LocalCartDataSource
import com.tofukma.orderapp.EventBus.CountCartEvent
import com.tofukma.orderapp.EventBus.HideFABCart
import com.tofukma.orderapp.EventBus.UpdateItemInCart
import com.tofukma.orderapp.Model.Order
import com.tofukma.orderapp.R
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.frament_cart.*
import kotlinx.android.synthetic.main.layout_place_order.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.lang.StringBuilder
import java.util.*

class CartFragment : Fragment() {

    private var cartDataSource: CartDataSource?=null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var recyclerViewState: Parcelable?=null
    private lateinit var cartViewModel: CartViewModel
    private lateinit var btn_place_order : Button



    var txt_empty_cart: TextView?=null
    var txt_total_price:TextView?=null
    var group_place_holder:CardView?=null
    var recycler_cart:RecyclerView?=null
    var recyclerView:RecyclerView ?= null
    var viewPager:LoopingViewPager ?= null
    var adapter:MyCartAdapter?= null

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location


    var layoutAnimationController:LayoutAnimationController ?= null

    override fun onResume(){
        super.onResume()
        calculateTotalPrice()
        if(fusedLocationProviderClient != null)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,
            Looper.getMainLooper()
                )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        EventBus.getDefault().postSticky(HideFABCart(true))
        cartViewModel =
            ViewModelProviders.of(this).get(CartViewModel::class.java)
        cartViewModel.initCartdataSorce(context!!)
        val root = inflater.inflate(R.layout.frament_cart, container, false)
        initViews(root)
        initLocation()
        cartViewModel.getMutableLiveDataCartItem().observe(this, Observer {
            if(it == null || it.isEmpty()){
                recycler_cart!!.visibility = View.GONE
                group_place_holder!!.visibility = View.GONE
                txt_empty_cart!!.visibility = View.VISIBLE

            }else{
                recycler_cart!!.visibility = View.VISIBLE
                group_place_holder!!.visibility = View.VISIBLE
                txt_empty_cart!!.visibility = View.GONE
                adapter = MyCartAdapter(context!!,it)
                recycler_cart!!.adapter = adapter
            }
        } )
        return root
    }

    private fun initLocation() {
        buildLocationRequest()
        buildLocationCallback()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)
        fusedLocationProviderClient!!.requestLocationUpdates(locationRequest,locationCallback,Looper.getMainLooper())
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                currentLocation = p0!!.lastLocation
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(5000)
        locationRequest.setFastestInterval(3000)
        locationRequest.setSmallestDisplacement(10f)
    }

    private fun initViews(root:View) {

        setHasOptionsMenu(true) // Import , if not add it , menu will never be inflate

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context!!).cartDAO())

        recycler_cart = root.findViewById(R.id.recycler_cart) as RecyclerView
        recycler_cart!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recycler_cart!!.layoutManager = layoutManager
        recycler_cart!!.addItemDecoration(DividerItemDecoration(context,layoutManager.orientation))

        val swipe = object:MySwipeHelper(context!!, recycler_cart!!, 200)
        {
            override fun instantianteMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(context!!,
                "Delete",
                30,
                0,
                Color.parseColor("#FF3C30"),
                object:IMyButtonCallback{
                    override fun onClick(pos: Int) {
                        val deleteItem = adapter!!.getItemAtPosition(pos)
                        cartDataSource!!.deleteCart(deleteItem)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object:SingleObserver<Int>{
                                override fun onSuccess(t: Int) {
                                    adapter!!.notifyItemRemoved(pos)
                                    sumCart()
                                    EventBus.getDefault().postSticky(CountCartEvent(true))
                                    Toast.makeText(context, "Delete item success", Toast.LENGTH_SHORT).show()
                                }

                                override fun onSubscribe(d: Disposable) {
                                }

                                override fun onError(e: Throwable) {
                                    Toast.makeText(context, ""+ e.message, Toast.LENGTH_SHORT).show()
                                }

                            })
                    }
                }))
            }
        }

        txt_empty_cart = root.findViewById(R.id.txt_empty_cart) as TextView
        txt_total_price = root.findViewById(R.id.txt_total_price) as TextView
        group_place_holder = root.findViewById(R.id.group_place_holder) as CardView


        btn_place_order = root.findViewById(R.id.btn_place_order) as Button

        // Event
        btn_place_order.setOnClickListener{
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle("Thêm một bước nữa !")

            val view = LayoutInflater.from(context).inflate(R.layout.layout_place_order,null)

            val edt_address = view.findViewById<View>(R.id.edt_address) as EditText
            val edt_comment = view.findViewById<View>(R.id.edt_comment) as EditText
            val txt_address = view.findViewById<View>(R.id.txt_address_detail) as TextView

            val rdi_home = view.findViewById<View>(R.id.rdi_home_address) as RadioButton
            val rdi_other_address = view.findViewById<View>(R.id.rdi_other_address) as RadioButton
            val rdi_ship_to_this_address = view.findViewById<View>(R.id.rdi_ship_this_address) as RadioButton

            val rdi_cod = view.findViewById<View>(R.id.rdi_cod) as RadioButton
            val rdi_braintree = view.findViewById<View>(R.id.rdi_braintree) as RadioButton

            // Data
            edt_address.setText(Common.currentUser!!.addrss!!)

            rdi_home.setOnCheckedChangeListener{ compoundButton, b ->
                if(b){
                    edt_address.setText(Common.currentUser!!.addrss!!)
                    txt_address.visibility = View.GONE
                }

            }
            rdi_other_address.setOnCheckedChangeListener{ compoundButton, b ->
                if(b){
                    edt_address.setText("")
                    edt_address.setHint("")
                    txt_address.visibility = View.GONE
                }

            }
            rdi_ship_to_this_address.setOnCheckedChangeListener{ compoundButton, b ->
                if(b){
                       fusedLocationProviderClient!!.lastLocation
                           .addOnFailureListener{ e ->
                               txt_address.visibility = View.GONE
                               Toast.makeText(context!!,""+e.message,Toast.LENGTH_SHORT).show()}
                           .addOnCompleteListener{
                               task ->
                               val coordinates = StringBuilder()
                                   .append(task.result!!.latitude)
                                   .append("/")
                                   .append(task.result!!.longitude)
                                   .toString()
                                val singleAddress = Single.just(getAddressFromLatLng(task.result!!.latitude,task.result!!.longitude))
                              val disposable = singleAddress.subscribeWith(object:DisposableSingleObserver<String>(){
                                  override fun onSuccess(t: String) {
                                      edt_address.setText(coordinates)
                                      txt_address.visibility = View.VISIBLE
                                      txt_address.setText(t)
                                  }

                                  override fun onError(e: Throwable) {
                                      edt_address.setText(coordinates)
                                      txt_address.visibility = View.VISIBLE
                                      txt_address.setText(e.message!!)
                                  }
                              })



                           }
                    }

            }

            builder.setView(view)
            builder.setNegativeButton("NO",{dialog, _ ->dialog.dismiss()  })
                .setPositiveButton("YES",{
                    dialog, _ -> if(rdi_cod.isChecked)
                        paymentCOD(edt_address.text.toString(),edt_comment.text.toString())
                })
            val dialog = builder.create()
            dialog.show()

        }
    }

    private fun paymentCOD(address: String, comment: String) {
            compositeDisposable.add(cartDataSource!!.getAllCart(Common.currentUser!!.uid!!).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                        cartItemList ->
                        // when we have all cartItem, we will get total price
                    cartDataSource!!.sumPrice(Common.currentUser!!.uid!!).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(object: SingleObserver<Double>{
                            override fun onSuccess(totalPrice: Double) {
                                val finalPrice = totalPrice
                                val order = Order()
                                order.userId = Common.currentUser!!.uid!!
                                order.userName = Common.currentUser!!.name!!
                                order.userPhone = Common.currentUser!!.phone
                                order.shippingAddress = address
                                order.comment = comment
                                if(currentLocation != null) {
                                    order.lat = currentLocation!!.latitude
                                    order.lng = currentLocation!!.longitude
                                    }
                                order.carItemList = cartItemList
                                order.totalPayment = totalPrice
                                order.finalPayment = finalPrice
                                order.discount = 0
                                order.isCod = true
                                order.transactionId = "Thanh toán khi nhận hàng "

                                pushOrderToServer(order)
                            }

                            override fun onSubscribe(d: Disposable) {
                                TODO("Not yet implemented")
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(context!!,""+e.message,Toast.LENGTH_SHORT).show()
                            }


                        })


                },{ throwable -> Toast.makeText(context!!,"BI LOI "+throwable.message,Toast.LENGTH_SHORT).show()
                        Log.d("LOI",throwable.toString())
            })
            )

    }

    private fun pushOrderToServer(order: Order) {
        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF).child(Common.createOrderNumber())
            .setValue(order).addOnFailureListener{
                e -> Toast.makeText(context!!,""+e.message,Toast.LENGTH_LONG).show()
            }
            .addOnCompleteListener { task ->

                //clean cart
                if(task.isSuccessful){
                    cartDataSource!!.cleanCart(Common.currentUser!!.uid!!).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object: SingleObserver<Int>{
                            override fun onSuccess(t: Int) {
                                Toast.makeText(context!!,"Đặt hàng thành công !!",Toast.LENGTH_LONG).show()
                            }

                            override fun onSubscribe(d: Disposable) {
                                TODO("Not yet implemented")
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(context!!,"Bi loi "+e.message,Toast.LENGTH_LONG).show()
                            }


                        })

                }


            }


    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String {
        val geoCoder = Geocoder(context!!, Locale.getDefault())
        var result:String?=null
        try {
            val addressList = geoCoder.getFromLocation(latitude,longitude,1)
            if(addressList != null && addressList.size > 0){
                val address = addressList[0]
                val sb = StringBuilder(address.getAddressLine(0))
                result  = sb.toString()
            }
            else
                result="Địa chỉ không tồn tại !!!"
            return result
        }catch (e:IOException)
        {
            return e.message!!
        }
    }


    private fun sumCart() {
        cartDataSource!!.sumPrice(Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Double>{
                override fun onSuccess(t: Double) {
                    txt_total_price!!.text = StringBuilder("Total: đ")
                        .append(t)
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    if(!e.message!!.contains("Query returned empty"))
                        Toast.makeText(context,""+e.message!!,Toast.LENGTH_SHORT).show()
                }

            })
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        cartViewModel!!.onStop()
        compositeDisposable.clear()
        EventBus.getDefault().postSticky(HideFABCart(false))
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        if(fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }
    //    var unbinder: Unbinder?= null
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onUpdateItemInCart(event:UpdateItemInCart){
        if(event.cartItem !=null)
        {
            recyclerViewState = recycler_cart!!.layoutManager!!.onSaveInstanceState()
            cartDataSource!!.updatecart(event.cartItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Int>{
                    override fun onSuccess(t: Int) {
                        calculateTotalPrice()
                        recycler_cart!!.layoutManager!!.onRestoreInstanceState(recyclerViewState)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context,"[UPDATE CART]"+e.message,Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun calculateTotalPrice() {
        cartDataSource!!.sumPrice(Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:SingleObserver<Double>{
                override fun onSuccess(price: Double) {
                    txt_total_price!!.text = StringBuilder("Total: ")
                        .append(Common.formatPrice(price))
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    if(!e.message!!.contains("Query returned emtpy"))
                        Toast.makeText(context,"[SUM CART]"+e.message,Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu!!.findItem(R.id.action_settings).setVisible(false)  // Hide seting menu when in cart
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.cart_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item!!.itemId == R.id.action_clear_cart)
        {
            cartDataSource!!.cleanCart(Common.currentUser!!.uid!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object:SingleObserver<Int>{
                    override fun onSuccess(t: Int) {
                        Toast.makeText(context,"Clear Cart Success",Toast.LENGTH_SHORT)
//                        EventBus.getDefault().postSticky(CountCartEvent(true))
                        EventBus.getDefault().postSticky(CountCartEvent(true))
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context,""+e.message,Toast.LENGTH_SHORT)
                    }

                })
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView(root:View) {
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_item_from_left)
        viewPager = root.findViewById(R.id.viewpaper) as LoopingViewPager
        recyclerView = root.findViewById(R.id.recycler_popular) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)

    }



//    override fun onPause() {
//        viewPager!!.pauseAutoScroll()
//        super.onPause()
//
//    }
}