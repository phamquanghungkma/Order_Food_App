package com.tofukma.orderapp.Database
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tofukma.orderapp.Utils.Decryption
import com.tofukma.orderapp.Utils.Encryption

@Entity(tableName = "Cart",primaryKeys = ["uid","foodId","foodSize","foodAddon","restaurantId"])
class CartItem {

    @NonNull
    @ColumnInfo(name = "restaurantId")
    var restaurantId:String = ""
        get() {
            if(restaurantId != null){
                return Decryption.decrypt(restaurantId!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }



    @ColumnInfo(name = "foodId")
    var foodId:String =""
        get() {
            if(foodId != null){
                return Decryption.decrypt(foodId!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    @ColumnInfo(name="foodName")
    var foodName:String?=null
        get() {
            if(foodName != null){
                return Decryption.decrypt(foodName!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    @ColumnInfo(name="foodImage")
    var foodImage:String?=null
        get() {
            if(foodImage != null){
                return Decryption.decrypt(foodImage!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    @ColumnInfo(name="foodPrice")
    var foodPrice:Double=0.0
        get() {
            if(foodPrice != null){
                return Decryption.decrypt(foodPrice!!) as Double
            }
            return 0.toDouble();
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as Double
            field =  if(dataEncryption != null )dataEncryption else 0.toDouble()
        }

    @ColumnInfo(name="foodQuantity")
    var foodQuantity:Int=0
        get() {
            if(foodQuantity != null){
                return Decryption.decrypt(foodQuantity!!) as Int
            }
            return 0;
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as Int
            field =  if(dataEncryption != null )dataEncryption else 0
        }

    @NonNull
    @ColumnInfo(name="foodAddon")
    var foodAddon:String?=null
        get() {
            if(foodAddon != null){
                return Decryption.decrypt(foodAddon!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    @NonNull
    @ColumnInfo(name="foodSize")
    var foodSize:String?=null
        get() {
            if(foodSize != null){
                return Decryption.decrypt(foodSize!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }

    @ColumnInfo(name="userPhone")
    var userPhone:String?=""
        get() {
            if(userPhone != null){
                return Decryption.decrypt(userPhone!!) as String
            }
            return "";
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as String
            field =  if(dataEncryption != null )dataEncryption else ""
        }


    @ColumnInfo(name="foodExtraPrice")
    var foodExtraPrice:Double = 0.0
        get() {
            if(foodExtraPrice != null){
                return Decryption.decrypt(foodExtraPrice!!) as Double
            }
            return 0.toDouble();
        }
        set(value) {
            val dataEncryption = Encryption.encrypt(value!!) as Double
            field =  if(dataEncryption != null )dataEncryption else 0.toDouble()
        }

    @NonNull
    @ColumnInfo(name="uid")
    var uid:String?=""
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

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if(other !is CartItem)
            return false
        val cartItem = other as CartItem?
        return cartItem!!.foodId ==this.foodId &&
                cartItem.foodAddon == this.foodAddon &&
                cartItem.foodSize == this.foodSize

    }
}