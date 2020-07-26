package com.tofukma.orderapp.Common

import com.tofukma.orderapp.Model.CategoryModel
import com.tofukma.orderapp.Model.FoodModel
import com.tofukma.orderapp.Model.UserModel
import java.math.RoundingMode
import java.text.DecimalFormat

object Common {
    fun formatPrice(price: Double): Any {
        if(price != 0.toDouble())
        {
            val df = DecimalFormat("#,##0.0")
            df.roundingMode = RoundingMode.HALF_UP
            val finalPrice = StringBuffer(df.format(price)).toString()
            return finalPrice.replace(".",",")
        }else
            return "0,00"
    }

    val COMMENT_REF: String = "Comments"
    var foodSelected:FoodModel ?= null
    var categorySelected: CategoryModel ?= null
    val CATEGORY_REF: String = "Category"
    val FULL_WIDTH_COLUMN: Int = 1
    val DEFAULT_COLUMN_COUNT: Int = 0
    val BEST_DEALS_REF: String = "BestDeals"
    val POPULAR_REF : String = "MostPopular"
    val USER_REFERENCE = "Users"
    var currentUser:UserModel?=null

}