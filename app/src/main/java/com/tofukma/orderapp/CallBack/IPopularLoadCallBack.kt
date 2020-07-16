package com.tofukma.orderapp.CallBack

import com.tofukma.orderapp.Model.PopularCategoryModel

interface IPopularLoadCallBack {

    fun onPopularLoadSuccess (popularList: List<PopularCategoryModel>)
    fun onPopularLoadFailed (message:String)
}