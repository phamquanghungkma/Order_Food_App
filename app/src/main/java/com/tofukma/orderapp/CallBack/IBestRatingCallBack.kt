package com.tofukma.orderapp.CallBack

import com.tofukma.orderapp.Model.BestDealModel

interface IBestRatingCallBack {
    fun onBestRatingLoadSuccess(bestDealList: List<BestDealModel>)
    fun onBestDealLoadFailed(message:String)

}