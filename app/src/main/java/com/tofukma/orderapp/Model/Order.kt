package com.tofukma.orderapp.Model

import com.tofukma.orderapp.Database.CartItem
import com.tofukma.orderapp.Utils.Decryption
import com.tofukma.orderapp.Utils.Encryption

class Order {

    var userId: String
    get() {
        if(userId != null){
            return Decryption.decrypt(userId) as String
        }
        return "";
    }
    set(value) {
        val dataEncryption = Encryption.encrypt(value) as String
        field =  if(dataEncryption != null )dataEncryption else ""
    }

    var userName:String ?= null
    var userPhone :String ?= null
    var shippingAddress:String ?= null
    var comment:String ?= null
    var transactionId: String ?= null
    var lat: Double = 0.toDouble()
    var lng:Double = 0.toDouble()
    var totalPayment: Double = 0.toDouble()
    var finalPayment:Double =  0.toDouble()
    var isCod: Boolean = false
    var discount:Int = 0
    var carItemList:List<CartItem> ?= null
    var createDate: Long?= 0
    var orderNumber:String ?= null
    var orderStatus:Int = 0
}