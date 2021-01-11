package com.tofukma.orderapp.Model

import com.tofukma.orderapp.Utils.Decryption
import com.tofukma.orderapp.Utils.Encryption

class AddonModel {
    var name:String?= null
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


    var price:Long = 0
        get() {
            if(price != null){
                return Decryption.decrypt(price!!) as Long
            }
            return 0;
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as Long
            field =  if(dataEncryption != null )dataEncryption else 0
        }
}