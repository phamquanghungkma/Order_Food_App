package com.tofukma.orderapp.CallBack

import com.tofukma.orderapp.Model.CommentModel
import com.tofukma.orderapp.Model.Order

interface ILoadTimeFromFirebaseCallBack {
    fun onLoadTimeSuccess(order: Order,estimatedTimeMs:Long)
    fun onLoadTimeFailed(message:String)
}