package com.tofukma.orderapp.EventBus

import com.tofukma.orderapp.Model.CategoryModel
import com.tofukma.orderapp.Model.FoodModel

class FoodItemClick(var isSuccess:Boolean, var foodModel: FoodModel) {
}