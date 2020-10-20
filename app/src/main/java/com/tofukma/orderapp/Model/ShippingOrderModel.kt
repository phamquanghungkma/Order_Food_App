package com.tofukma.orderapp.Model

class ShippingOrderModel {
    var key:String?=null
    var shipperPhone:String?=null
    var shipperName:String?=null
    var currentLat=0.0
    var currentLng=0.0
    var orderModel:Order?=null
    var isStartTrip=false
   var estimateTime:String=""
}