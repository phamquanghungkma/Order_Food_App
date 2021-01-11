package com.tofukma.orderapp.Model

import com.tofukma.orderapp.Utils.Decryption
import com.tofukma.orderapp.Utils.Encryption

class ShippingOrderModel {
    var key:String?=null
        get() {
            if(key != null){
                return Decryption.decrypt(key!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    var shipperPhone:String?=null
        get() {
            if(shipperPhone != null){
                return Decryption.decrypt(shipperPhone!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }
    var shipperName:String?=null
        get() {
            if(shipperName != null){
                return Decryption.decrypt(shipperName!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    var currentLat: Double ? = 0.toDouble()
//        get() {
//            if(currentLat != null){
//                return Decryption.decrypt(currentLat!!) as Double
//            }
//            return 0.toDouble();
//        }
//        set(value) {
//            val dataEncryption = Encryption.encrypt(value!!) as Double
//            field =  if(dataEncryption != null )dataEncryption else 0.toDouble()
//        }

    var currentLng :Double ? = 0.toDouble()
//        get() {
//            if(currentLng != null){
//                return Decryption.decrypt(currentLng!!) as Double
//            }
//            return 0.toDouble();
//        }
//        set(value) {
//            val dataEncryption = Encryption.encrypt(value!!) as Double
//            field =  if(dataEncryption != null )dataEncryption else 0.toDouble()
//        }

    var orderModel:Order?=null
    var isStartTrip=false

   var estimateTime:String=""
       get() {
           if(estimateTime != null){
               return Decryption.decrypt(estimateTime!!) as String
           }
           return "";
       }
       set(value) {
           val dataEncryption = Encryption.encrypt(value!!) as String
           field =  if(dataEncryption != null )dataEncryption else ""
       }
}