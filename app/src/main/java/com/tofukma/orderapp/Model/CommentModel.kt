package com.tofukma.orderapp.Model

import com.tofukma.orderapp.Utils.Decryption
import com.tofukma.orderapp.Utils.Encryption

class CommentModel {
    var ratingValue:Float = 0.toFloat()
        get() {
            if(ratingValue != null){
                return Decryption.decrypt(ratingValue!!) as Float
            }
            return 0.toFloat();
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as Float
            field =  if(dataEncryption != null )dataEncryption else 0.toFloat()
        }


    var comment:String?=null
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

    var name:String ?=null
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

    var uid:String?=null
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

    var commentTimeStamp:HashMap<String,Any>?=null


}