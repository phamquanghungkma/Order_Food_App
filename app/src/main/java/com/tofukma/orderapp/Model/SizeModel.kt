package com.tofukma.orderapp.Model

import com.tofukma.orderapp.Utils.Decryption
import com.tofukma.orderapp.Utils.Encryption

class SizeModel {
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
}
