package com.tofukma.orderapp.Model

import com.tofukma.orderapp.Utils.Decryption
import com.tofukma.orderapp.Utils.Encryption

class FoodModel {
    var key:String ?=null
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

    var image:String ?= null
        get() {
            if(image != null){
                return Decryption.decrypt(image!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    var id:String ?= null
        get() {
            if(id != null){
                return Decryption.decrypt(id!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    var description:String ?= null
        get() {
            if(description != null){
                return Decryption.decrypt(description!!) as String
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

    var addon:List<AddonModel> = ArrayList<AddonModel>()
    var size:List<SizeModel> = ArrayList<SizeModel>()

    var ratingValue:Double = 0.toDouble()
        get() {
            if(ratingValue != null){
                return Decryption.decrypt(ratingValue!!) as Double
            }
            return 0.toDouble();
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as Double
            field =  if(dataEncryption != null )dataEncryption else 0.toDouble()
        }

    var ratingCount:Long = 0.toLong()
        get() {
            if(ratingCount != null){
                return Decryption.decrypt(ratingCount!!) as Long
            }
            return 0;
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as Long
            field =  if(dataEncryption != null )dataEncryption else 0
        }

    var userSelectedAddon:MutableList<AddonModel>?=null
    var userSelectedSize:SizeModel?=null

}