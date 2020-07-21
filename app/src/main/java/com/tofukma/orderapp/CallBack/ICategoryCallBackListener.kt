package com.tofukma.orderapp.CallBack

import com.tofukma.orderapp.Model.CategoryModel

interface ICategoryCallBackListener {

    fun onCategoryLoadSuccess(categoriesList:List<CategoryModel>)
    fun onCategoryLoadFailed(message:String)
}
