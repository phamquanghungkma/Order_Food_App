package com.tofukma.orderapp.ui.foodlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tofukma.orderapp.Common.Common
import com.tofukma.orderapp.Model.FoodModel

class FoodListViewModel : ViewModel() {

    private var mutableFoodModelListData : MutableLiveData<List<FoodModel>> ?= null

        fun getMutableFoodModelListData():MutableLiveData<List<FoodModel>>{
                if (mutableFoodModelListData == null)
                    mutableFoodModelListData = MutableLiveData()
                    mutableFoodModelListData!!.value = Common.categorySelected!!.foods
                    return mutableFoodModelListData!!



        }

}