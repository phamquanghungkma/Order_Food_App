package com.tofukma.orderapp.Remote

import com.tofukma.orderapp.Model.BraintreeToken
import com.tofukma.orderapp.Model.BraintreeTransaction
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.*

interface ICloudFunction {
    @GET(value = "token")
    fun getToken() : Observable<BraintreeToken>

    @POST(value = "checkout")
    @FormUrlEncoded
    fun submitPayment(@Field(value = "amount") amount:Double, @Field(value = "payment_method_nonce") nonce:String) : Observable<BraintreeTransaction>
}