package com.tofukma.orderapp.Model

import com.tofukma.orderapp.Utils.Decryption
import com.tofukma.orderapp.Utils.Encryption

class CategoryModel {
    var menu_id:String ?= null
        get() {
            if(menu_id != null){
                return Decryption.decrypt(menu_id!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    var name:String ?= null
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

    var image:String ?= null
//        get() {
//            if(image != null){
//                return Decryption.decrypt(image!!) as String
//            }
//            return "";
//        }
//        set(value) {
//            val dataEncryption = Encryption.encrypt(value!!) as String
//            field =  if(dataEncryption != null )dataEncryption else ""
//        }

    var foods:List<FoodModel>?=null

}