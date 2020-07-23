package com.tofukma.orderapp.ui.fooddetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tofukma.orderapp.Common.Common
import com.tofukma.orderapp.Model.CommentModel
import com.tofukma.orderapp.Model.FoodModel

class FoodDetailViewModel : ViewModel() {

    private var mutableLiveDataFood:MutableLiveData<FoodModel> ?= null
    private var mutableLiveDataCommentModel:MutableLiveData<CommentModel> ?= null


    init {
        mutableLiveDataCommentModel = MutableLiveData()

    }

    fun getMutableLiveDataFood():MutableLiveData<FoodModel>{

        if (mutableLiveDataFood == null)
            mutableLiveDataFood = MutableLiveData()
        mutableLiveDataFood!!.value = Common.foodSelected
        return mutableLiveDataFood!!
    }

    fun getMutableLiveDataComment():MutableLiveData<CommentModel>{

        if (mutableLiveDataCommentModel == null)
            mutableLiveDataCommentModel = MutableLiveData()
        return mutableLiveDataCommentModel!!
    }

    fun setCommentModel(commentModel: CommentModel) {
        if(mutableLiveDataCommentModel != null)
            mutableLiveDataCommentModel!!.value = (commentModel)


    }

    fun setFoodModel(foodModel: FoodModel) {
         if(mutableLiveDataFood != null)
             mutableLiveDataFood!!.value = foodModel



    }

}