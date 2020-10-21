package com.tofukma.orderapp.CallBack

import com.tofukma.orderapp.Model.RestaurantModel

interface IRestaurantCallbackListener {
    fun onRestaurantLoadSuccess(restaurantList: List<RestaurantModel>)
    fun onRestaurantLoadFaild(message:String)
}