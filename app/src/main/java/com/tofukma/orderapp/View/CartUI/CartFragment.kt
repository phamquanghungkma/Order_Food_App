package com.tofukma.orderapp.View.CartUI

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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asksira.loopingviewpager.LoopingViewPager
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tofukma.orderapp.Adapter.MyCartAdapter
import com.tofukma.orderapp.CallBack.ILoadTimeFromFirebaseCallBack
import com.tofukma.orderapp.CallBack.IMyButtonCallback
import com.tofukma.orderapp.Utils.Common
import com.tofukma.orderapp.Utils.MySwipeHelper
import com.tofukma.orderapp.Database.CartDataSource
import com.tofukma.orderapp.Database.CartDatabase
import com.tofukma.orderapp.Database.LocalCartDataSource
import com.tofukma.orderapp.EventBus.CountCartEvent
import com.tofukma.orderapp.EventBus.HideFABCart
import com.tofukma.orderapp.EventBus.MenuItemBack
import com.tofukma.orderapp.EventBus.UpdateItemInCart
import com.tofukma.orderapp.Model.FCMResponse
import com.tofukma.orderapp.Model.FCMSendData
import com.tofukma.orderapp.Model.Order
import com.tofukma.orderapp.R
import com.tofukma.orderapp.Remote.IFCMService
import com.tofukma.orderapp.Remote.RetrofitFCMClient
import com.tofukma.orderapp.ViewModel.cart.CartViewModel
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_cart_item.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class CartFragment : Fragment(),ILoadTimeFromFirebaseCallBack {

    private var placeSelected: Place?=null
    private  var places_fragment: AutocompleteSupportFragment ?= null
    private lateinit var placeClient: PlacesClient
    private val placeFields = Arrays.asList(
        Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS,
        Place.Field.LAT_LNG
    )


    override fun onLoadTimeSuccess(order: Order, estimatedTimeMs: Long) {
        order.createDate = estimatedTimeMs
        Log.d("Date", order.createDate.toString())
        order.orderStatus = 0
        pushOrderToServer(order)

    }


    override fun onLoadTimeFailed(message: String) {
        Toast.makeText(context!!,message,Toast.LENGTH_SHORT).show()
    }

    private var cartDataSource: CartDataSource?=null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var recyclerViewState: Parcelable?=null
    private lateinit var cartViewModel: CartViewModel
    private lateinit var btn_place_order : Button
    lateinit var listener:ILoadTimeFromFirebaseCallBack
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
    private lateinit var mainView: RelativeLayout; //= card_main_layout

    var layoutAnimationController:LayoutAnimationController ?= null

    lateinit var ifcmService: IFCMService

    override fun onResume(){
        super.onResume()
        calculateTotalPrice()
        if(fusedLocationProviderClient != null)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,
                Looper.getMainLooper()
            )
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        setHasOptionsMenu(true)

        EventBus.getDefault().postSticky(HideFABCart(true))
        cartViewModel =
            ViewModelProviders.of(this).get(CartViewModel::class.java)
        cartViewModel.initCartdataSorce(context!!)
        val root = inflater.inflate(R.layout.frament_cart, container, false)
        initViews(root)
        initLocation()
       // mainView = root.findViewById(R.id.card_main_layout)
        // hàm lắng nghe LiveData
        // fragment là các tp quan sát
        cartViewModel.getMutableLiveDataCartItem().observe(this, Observer {
            if(it == null || it.isEmpty()){
                recycler_cart!!.visibility = View.GONE // ẩn đi
                group_place_holder!!.visibility = View.GONE // ẩn đi
                txt_empty_cart!!.visibility = View.VISIBLE // hiện ra dòng chữ trống

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

//    private fun initPlacesClient(){
//        Places.initialize(context!!,getString(R.string.google_maps_key))
//        placeClient = Places.createClient(context!!)
//    }

    private fun initViews(root:View) {

//        initPlacesClient()

        setHasOptionsMenu(true) // Import , if not add it , menu will never be inflate

        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService::class.java)

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context!!).cartDAO())
        listener = this
        recycler_cart = root.findViewById(R.id.recycler_cart) as RecyclerView
        recycler_cart!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recycler_cart!!.layoutManager = layoutManager
        recycler_cart!!.addItemDecoration(DividerItemDecoration(context,layoutManager.orientation))




        val swipe = object:MySwipeHelper(context!!, recycler_cart!!, 200)
        {
            override fun instantianteMyButton( viewHolder: RecyclerView.ViewHolder, buffer: MutableList<MyButton>
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
            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle(" Địa chỉ nhận hàng  !")
            // cho nay o dang muon lam gi??. hien thi view nay de len man hinh nay a?

            val view = LayoutInflater.from(activity).inflate(R.layout.layout_place_order,null)
            // mainView.addView(view);
            val edt_address = view!!.findViewById<View>(R.id.edt_address) as EditText
            val edt_comment = view!!.findViewById<View>(R.id.edt_comment) as EditText
            val txt_address = view!!.findViewById<View>(R.id.txt_address_detail) as TextView

            val rdi_home = view!!.findViewById<View>(R.id.rdi_home_address) as RadioButton
//            val rdi_other_address = view.findViewById<View>(R.id.rdi_other_address) as RadioButton
            val rdi_ship_to_this_address = view!!.findViewById<View>(R.id.rdi_ship_this_address) as RadioButton

            val rdi_cod = view!!.findViewById<View>(R.id.rdi_cod) as RadioButton
            val rdi_braintree = view!!.findViewById<View>(R.id.rdi_braintree) as RadioButton
            // loi o day la do  activity!!.supportFragmentManager.findFragmentById(R.id.places_autocomplete_fragment) khong tim thay element nay tren view

            // Data
            edt_address.setText(Common.currentUser!!.addrss!!)

            rdi_home.setOnCheckedChangeListener{ compoundButton, b ->
                if(b){
                    edt_address.setText(Common.currentUser!!.addrss!!)
                }

            }
//            rdi_other_address.setOnCheckedChangeListener{ compoundButton, b ->
//                if(b){
//                    edt_address.setText("")
//                    edt_address.setHint("")
//                    txt_address.visibility = View.GONE
//                }
//
//            }
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

                                    edt_address.setText(t)
                                }

                                override fun onError(e: Throwable) {

                                    edt_address.setText(e.message!!)
                                }
                            })



                        }
                }

            }

            builder.setView(view)
            builder.setNegativeButton("NO",{dialog, _ ->dialog.dismiss()})
                .setPositiveButton("YES",{
                        dialog, _ -> if(rdi_cod.isChecked)
                    paymentCOD(txt_address.text.toString(),edt_comment.text.toString())
                })

//            var fragmentAddress = AutocompleteSupportFragment.newInstance()
//            if (fragmentAddress.view == null){
//                print("Loi 1")
//            } else {
//                view.findViewById<LinearLayout>(R.id.oder_main_view).addView(fragmentAddress.view,0)
//            }

            val dialog = builder.create()
            dialog.show()



            //   fragmentAddress = this.activity!!.supportFragmentManager.findFragmentById(R.id.places_autocomplete_fragment) as AutocompleteSupportFragment
            /*places_fragment = (context!! as FragmentActivity).supportFragmentManager.findFragmentById(R.id.places_autocomplete_fragment)
                    as AutocompleteSupportFragment
            */
//            fragmentAddress!!.setPlaceFields(placeFields)
//            fragmentAddress!!.setOnPlaceSelectedListener(object: PlaceSelectionListener {
//                override fun onPlaceSelected(p0: Place) {
//                    placeSelected = p0
//                    txt_address.text = placeSelected!!.address
//                }
//
//                override fun onError(p0: Status) {
//                    Toast.makeText(context,""+p0.statusMessage,Toast.LENGTH_SHORT).show()            }
//
//
//            })
        }
    }

    private fun paymentCOD(address: String, comment: String) {
        compositeDisposable.add(cartDataSource!!.getAllCart(Common.currentUser!!.uid!!).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    cartItemList ->
                // when we have all cartItem, we will get total price\
                if (cartDataSource == null){

                    Log.d("LOI","do cartDataSource null");
                }
                if (Common.currentUser == null){

                    Log.d("LOI","do Common.currentUser null");
                }
                if (Common.currentUser!!.uid == null){

                    Log.d("LOI","do Common.currentUser.uid null");
                }
                cartDataSource!!.sumPrice(Common.currentUser!!.uid!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object: SingleObserver<Double>{
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

                            syncLocalTimeWithServerTime(order)


                        }

                        override fun onSubscribe(d: Disposable) {
                            Log.d("LOI","FIXED!!!")
                        }

                        override fun onError(e: Throwable) {
                            if(!e.message!!.contains("Query returned emtpy"))
                                Toast.makeText(context,"[SUM CART]"+e.message,Toast.LENGTH_SHORT).show()
                        }


                    })


            },{ throwable -> Toast.makeText(context!!,"BI LOI "+throwable.message,Toast.LENGTH_SHORT).show()
                throwable.printStackTrace()
                Log.d("LOI",throwable.message.toString())
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

                                val dataSend = HashMap<String,String>()
                                // format thông báo đc gửi đi
                                dataSend.put(Common.NOTI_TITLE," Đơn mới ")
                                dataSend.put(Common.NOTI_CONTENT,"Bạn có đơn đặt hàng mới từ : "+Common.currentUser!!.phone)

                                val sendData = FCMSendData(Common.getNewOrderTopic(),dataSend)

                                compositeDisposable.add(ifcmService.sendNotification(sendData)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({t:FCMResponse? ->
                                        if(t!!.success != 0)
                                            Toast.makeText(context!!,"Đặt hàng thành công ",Toast.LENGTH_LONG).show()
                                    },{
                                            throwable: Throwable ->
                                        throwable.printStackTrace()
                                        Toast.makeText(context!!,"Thoong bao loi ",Toast.LENGTH_LONG).show()

                                    }))


                            }

                            override fun onSubscribe(d: Disposable) {
                                Log.d("LOI","FIXED LOI 3")
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(context!!," "+e.message,Toast.LENGTH_LONG).show()
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
                    if(!e.message!!.contains("Query returned emtpy"))
                        Toast.makeText(context,"[SUM CART]"+e.message,Toast.LENGTH_SHORT).show()
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

    private fun syncLocalTimeWithServerTime(order: Order){
        val offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset")
        offsetRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                listener.onLoadTimeFailed(error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val offset =  snapshot.getValue(Long::class.java)
                val estimatedServerTimeInMs = System.currentTimeMillis() + offset!! // them missing offset vao current time
                val sdf = SimpleDateFormat("MMM dd yyyy, HH:mm")
                val date = Date(estimatedServerTimeInMs)
                Log.d("TofuKMA",""+sdf.format(date))
                Log.d("TofuKMA1",""+estimatedServerTimeInMs)
                if(estimatedServerTimeInMs is Long){
                    Log.d("Type  ","Type is Long")

                }
                listener.onLoadTimeSuccess(order,estimatedServerTimeInMs)
            }

        })
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()

    }

//    override fun onPause() {
//        viewPager!!.pauseAutoScroll()
//        super.onPause()
//
//    }
}