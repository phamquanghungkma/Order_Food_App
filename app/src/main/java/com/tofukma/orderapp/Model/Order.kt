package com.tofukma.orderapp.Model

import com.tofukma.orderapp.Database.CartItem
import com.tofukma.orderapp.Utils.Decryption
import com.tofukma.orderapp.Utils.Encryption

class Order {

    var userId: String ?= null
    get() {
        if(userId != null){
            return Decryption.decrypt(userId!!) as String
        }
        return "";
    }
    set(value) {
        val dataEncryption = Encryption.encrypt(value!!) as String
        field =  if(dataEncryption != null )dataEncryption else ""
    }

    var userName:String ?= null
        get() {
            if(userName != null){
                return Decryption.decrypt(userName!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    var userPhone :String ?= null
        get() {
            if(userPhone != null){
                return Decryption.decrypt(userPhone!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    var shippingAddress:String ?= null
        get() {
            if(shippingAddress != null){
                return Decryption.decrypt(shippingAddress!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    var comment:String ?= null
        get() {
            if(comment != null){
                return Decryption.decrypt(comment!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    var transactionId: String ?= null
        get() {
            if(transactionId != null){
                return Decryption.decrypt(transactionId!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    var lat: Double = 0.toDouble()
//        get() {
//            if(lat != null){
//                return Decryption.decrypt(lat!!) as Double
//            }
//            return 0.toDouble();
//        }
//        set(value) {
//            val dataEncryption = Encryption.encrypt(value!!) as Double
//            field =  if(dataEncryption != null )dataEncryption else 0.toDouble()
//        }

    var lng:Double = 0.toDouble()
//        get() {
//            if(lng != null){
//                return Decryption.decrypt(lng!!) as Double
//            }
//            return 0.toDouble();
//        }
//        set(value) {
//            val dataEncryption = Encryption.encrypt(value!!) as Double
//            field =  if(dataEncryption != null )dataEncryption else 0.toDouble()
//        }

    var totalPayment: Double = 0.toDouble()
        get() {
            if(totalPayment != null){
                return Decryption.decrypt(totalPayment!!) as Double
            }
            return 0.toDouble();
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as Double
            field =  if(dataEncryption != null )dataEncryption else 0.toDouble()
        }

    var finalPayment:Double =  0.toDouble()
        get() {
            if(finalPayment != null){
                return Decryption.decrypt(finalPayment!!) as Double
            }
            return 0.toDouble();
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as Double
            field =  if(dataEncryption != null )dataEncryption else 0.toDouble()
        }

    var isCod: Boolean = false

    var discount:Int = 0
        get() {
            if(discount != null){
                return Decryption.decrypt(discount!!) as Int
            }
            return 0;
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as Int
            field =  if(dataEncryption != null )dataEncryption else 0
        }

    var carItemList:List<CartItem> ?= null

    var createDate: Long?= 0
        get() {
            if(createDate != null){
                return Decryption.decrypt(createDate!!) as Long
            }
            return 0;
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as Long
            field =  if(dataEncryption != null )dataEncryption else 0
        }

    var orderNumber:String ?= null
        get() {
            if(orderNumber != null){
                return Decryption.decrypt(orderNumber!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    var orderStatus:Int = 0
        get() {
            if(orderStatus != null){
                return Decryption.decrypt(orderStatus!!) as Int
            }
            return 0;
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as Int
            field =  if(dataEncryption != null )dataEncryption else 0
        }
}