package com.tofukma.orderapp.ViewModel.vieworder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tofukma.orderapp.Model.Order

class ViewOrderModel : ViewModel() {
    val mutableLiveDataOrderList: MutableLiveData<List<Order>>

    init {
        mutableLiveDataOrderList  = MutableLiveData()
    }
    fun setMutableLiveDataOrderList(orderList: List<Order>){
        mutableLiveDataOrderList.value = orderList
    }

}