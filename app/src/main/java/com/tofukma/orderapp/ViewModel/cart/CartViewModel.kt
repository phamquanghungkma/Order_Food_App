package com.tofukma.orderapp.ViewModel.cart

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tofukma.orderapp.Utils.Common
import com.tofukma.orderapp.Database.CartDataSource
import com.tofukma.orderapp.Database.CartDatabase
import com.tofukma.orderapp.Database.CartItem
import com.tofukma.orderapp.Database.LocalCartDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

// ViewModel la cac subject
class CartViewModel : ViewModel() {

    private val compositeDisposable:CompositeDisposable
    private var cartDataSource:CartDataSource?=null
    private var mutableLiveDataCartItem:MutableLiveData<List<CartItem>>?=null

    init {
        compositeDisposable = CompositeDisposable()
    }
    fun initCartdataSorce(context: Context){
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())
    }
    fun getMutableLiveDataCartItem():MutableLiveData<List<CartItem>>{
        if (mutableLiveDataCartItem == null)
            mutableLiveDataCartItem = MutableLiveData()
        getCartItems()// lấy dữ liệu từ Repository
        return mutableLiveDataCartItem!!
    }
    private fun getCartItems(){
        compositeDisposable.addAll(cartDataSource!!.getAllCart(Common.currentUser!!.uid!!,Common.currentRestaurant!!.uid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ cartItems ->
                mutableLiveDataCartItem!!.value = cartItems
            },{t: Throwable? -> mutableLiveDataCartItem!!.value = null })
        )

//       observeOn  cho phép chúng ta quan sát các giá trị phát đi từ Observable trên một thread thích hợp, cụ thể đây là UI Thread.

//        AndroidSchedulers.mainThread()
//        Nó cung cấp quyền truy cập đến Main Thread/UI Thread.
//        Thông thường cập nhật giao diện hay tương tác với người dùng sẽ xảy ra trên luồng này.
//        Chúng ta không thực hiện bất kì công việc chuyên sâu trên luồng này vì nó sẽ làm cho ứng dụng bị crash hoặc ANR.
    }
    fun onStop(){
        compositeDisposable.clear()
    }

}