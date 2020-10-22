package com.tofukma.orderapp.Database

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class LocalCartDataSource(private val cartDAO: CartDAO) : CartDataSource {
    override fun getAllCart(uid: String,restaurantId:String): Flowable<List<CartItem>> {
        return cartDAO.getAllCart(uid,restaurantId )
    }

    override fun getItemWithAllOptionsInCart(
        uid: String,
        foodId: String,
        foodSize: String,
        foodAddon: String,
        restaurantId:String
    ): Single<CartItem> {
       return cartDAO.getItemWithAllOptionsInCart(uid, foodId, foodSize, foodAddon,restaurantId)
    }

    override fun countItemInCart(uid: String,restaurantId:String): Single<Int> {
        return cartDAO.countItemInCart(uid,restaurantId)
    }

    override fun sumPrice(uid: String ,restaurantId:String): Single<Double> {
        return cartDAO.sumPrice(uid,restaurantId)
    }

    override fun getItemCart(foodId: String, uid: String,  restaurantId:String): Single<CartItem> {
        return cartDAO.getItemCart(foodId,uid,restaurantId)
    }

    override fun insertOrReplaceAll(vararg cartItems: CartItem): Completable {
        return cartDAO.insertOrReplaceAll(*cartItems)
    }

    override fun updatecart(cart: CartItem): Single<Int> {
        return cartDAO.updatecart(cart)
    }

    override fun cleanCart(uid: String,restaurantId:String): Single<Int> {
        return cartDAO.cleanCart(uid,restaurantId)
    }

    override fun deleteCart(cart: CartItem): Single<Int> {
        return cartDAO.deleteCart(cart)
    }
}