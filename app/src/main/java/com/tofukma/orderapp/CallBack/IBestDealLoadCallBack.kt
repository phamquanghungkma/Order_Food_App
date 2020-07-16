package com.tofukma.orderapp.CallBack

import com.tofukma.orderapp.Model.BestDealModel
import com.tofukma.orderapp.Model.PopularCategoryModel

interface IBestDealLoadCallBack {

    fun onBestDealLoadSucess(bestDealList: List<BestDealModel>)
    fun onBestDealLoadFailed(message:String)

}