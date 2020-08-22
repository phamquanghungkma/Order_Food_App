package com.tofukma.orderapp.CallBack

import com.tofukma.orderapp.Model.Order

interface ILoadOrderCallbackListener {

    fun onLoadOrderSucess(orderList: ArrayList<Order>)
    fun onLoadOrderFailed(message: String)
}