package com.tofukma.orderapp.Model

import com.tofukma.orderapp.Utils.Decryption
import com.tofukma.orderapp.Utils.Encryption

class UserModel {
    var uid:String ?= null
//        get() {
//            if(uid != null){
//                return Decryption.decrypt(uid!!) as String
//            }
//            return "";
//        }
//        set(value) {
//            val dataEncryption = Encryption.encrypt(value!!) as String
//            field =  if(dataEncryption != null )dataEncryption else ""
//        }

    var name:String ?= null
//        get() {
//            if(name != null){
//                return Decryption.decrypt(name!!) as String
//            }
//            return "";
//        }
//        set(value) {
//            val dataEncryption = Encryption.encrypt(value!!) as String
//            field =  if(dataEncryption != null )dataEncryption else ""
//        }

    var addrss:String ?= null
//        get() {
//            if(addrss != null){
//                return Decryption.decrypt(addrss!!) as String
//            }
//            return "";
//        }
//        set(value) {
//            val dataEncryption = Encryption.encrypt(value!!) as String
//            field =  if(dataEncryption != null )dataEncryption else ""
//        }

    var phone:String ?= null
//        get() {
//            if(phone != null){
//                return Decryption.decrypt(phone!!) as String
//            }
//            return "";
//        }
//        set(value) {
//            val dataEncryption = Encryption.encrypt(value!!) as String
//            field =  if(dataEncryption != null )dataEncryption else ""
//        }

    var lat:Double=0.0
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

    var lng:Double=0.0
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

    constructor(){}


}