package com.tofukma.orderapp.Model

import com.tofukma.orderapp.Utils.Decryption
import com.tofukma.orderapp.Utils.Encryption

class RestaurantModel {
    var uid:String=""
        get() {
            if(uid != null){
                return Decryption.decrypt(uid!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    var name:String=""
        get() {
            if(name != null){
                return Decryption.decrypt(name!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    var address:String=""
        get() {
            if(address != null){
                return Decryption.decrypt(address!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    var imageUrl:String=""
//        get() {
//            if(imageUrl != null){
//                return Decryption.decrypt(imageUrl!!) as String
//            }
//            return "";
//        }
//        set(value) {
//            val dataEncryption = Encryption.encrypt(value!!) as String
//            field =  if(dataEncryption != null )dataEncryption else ""
//        }

    var phone:String=""
        get() {
            if(phone != null){
                return Decryption.decrypt(phone!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }
}