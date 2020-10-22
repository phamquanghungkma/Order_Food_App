package com.tofukma.orderapp.Database
import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


@Dao
interface CartDAO {
    @Query("SELECT * FROM Cart WHERE uid =:uid AND restaurantId = :restaurantId")
    fun getAllCart(uid:String,restaurantId:String):Flowable<List<CartItem>>

    @Query("SELECT SUM(foodQuantity) FROM  Cart WHERE uid = :uid AND restaurantId = :restaurantId")
    fun countItemInCart(uid:String,restaurantId:String):Single<Int>

    @Query("SELECT SUM((foodExtraPrice + foodPrice)*foodQuantity) FROM Cart WHERE uid =:uid  AND restaurantId = :restaurantId")
    fun sumPrice(uid:String,restaurantId:String):Single<Double>

    @Query("SELECT * FROM Cart  WHERE foodId=:foodId AND uid =:uid AND restaurantId = :restaurantId")
    fun getItemCart(foodId:String,uid:String,restaurantId:String):Single<CartItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceAll(vararg cartItems:CartItem):Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatecart(cart:CartItem):Single<Int>

    @Delete
    fun deleteCart(Cart:CartItem):Single<Int>

    @Query("DELETE FROM Cart WHERE uid =:uid AND restaurantId = :restaurantId")
    fun cleanCart(uid:String,restaurantId:String):Single<Int>

    @Query("SELECT * FROM Cart WHERE foodId =:foodId AND uid =:uid AND foodSize =:foodSize AND foodAddon =:foodAddon AND restaurantId = :restaurantId")
    fun getItemWithAllOptionsInCart(uid:String,foodId: String,foodSize:String,foodAddon:String,restaurantId:String):Single<CartItem>
}