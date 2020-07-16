package com.tofukma.orderapp.Remote

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface ICloudFuntions  {
    @GET("")
    fun getCustomToken(@Query("access_token") accessToken: String): Observable<ResponseBody>


}