package com.tofukma.orderapp.Model

import com.tofukma.orderapp.Utils.Decryption
import com.tofukma.orderapp.Utils.Encryption

class TokenModel {
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

    var token:String ?= null
//        get() {
//            if(token != null){
//                return Decryption.decrypt(token!!) as String
//            }
//            return "";
//        }
//        set(value) {
//            val dataEncryption = Encryption.encrypt(value!!) as String
//            field =  if(dataEncryption != null )dataEncryption else ""
//        }

    constructor()
    constructor(uid:String,token:String){
        this.uid = uid
        this.token = token
    }
}