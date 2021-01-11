package com.tofukma.orderapp.Model

import com.tofukma.orderapp.Utils.Decryption
import com.tofukma.orderapp.Utils.Encryption

class FCMResult {
    var message_id : String ?= null
        get() {
            if(message_id != null){
                return Decryption.decrypt(message_id!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }
}