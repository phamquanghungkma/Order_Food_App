package com.tofukma.orderapp.EventBus

import com.tofukma.orderapp.Model.CategoryModel
import com.tofukma.orderapp.Model.RestaurantModel

class MenuItemEvent (var isSuccess:Boolean, var restaurantModel: RestaurantModel) {
}