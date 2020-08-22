package com.tofukma.orderapp.Utils

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.TextView
import com.tofukma.orderapp.Model.CategoryModel
import com.tofukma.orderapp.Model.FoodModel
import com.tofukma.orderapp.Model.UserModel

import com.tofukma.orderapp.Model.*
import java.lang.StringBuilder

import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.random.Random

object Common {

    val ORDER_REF: String = "Order"
    val COMMENT_REF: String = "Comments"
    var foodSelected: FoodModel? = null
    var categorySelected: CategoryModel? = null
    val CATEGORY_REF: String = "Category"
    val CATEGORY_BEST_REF : String = "CategoryBest"
    val FULL_WIDTH_COLUMN: Int = 1
    val DEFAULT_COLUMN_COUNT: Int = 0
    val BEST_DEALS_REF: String = "BestDeals"
    val POPULAR_REF: String = "MostPopular"
    val USER_REFERENCE = "Users"
    var currentUser: UserModel? = null

    fun formatPrice(price: Double): Any {
        if(price != 0.toDouble())
        {
            val df = DecimalFormat("#,##0")
            df.roundingMode = RoundingMode.HALF_UP
            val finalPrice = StringBuffer(df.format(price)).toString()
            return finalPrice.replace(".",",")
        }else
            return "0,00"
    }

    fun setSpanString(welcome: String, name: String?, txtUser: TextView?) {
        val builder = SpannableStringBuilder()
        builder.append(welcome)
        val txtSpannable = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(boldSpan, 0, name!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(txtSpannable)
        txtUser!!.setText(builder, TextView.BufferType.SPANNABLE)
    }

    fun calcutaleExtraPrice(
            userSelectedSize: SizeModel?,
            userSelectedAddon: MutableList<AddonModel>?
        ): Double {
            var result: Double = 0.0
            if (userSelectedSize == null && userSelectedAddon == null)
                return 0.0
            else if (userSelectedSize == null) {
                for (addonModel in userSelectedAddon!!)
                    result += addonModel.price!!.toDouble()
                return result
            } else if (userSelectedAddon == null) {
                result = userSelectedSize!!.price!!.toDouble()
                return result
            } else {
                result = userSelectedSize!!.price!!.toDouble()
                for (addonModel in userSelectedAddon!!)
                    result += addonModel.price!!.toDouble()
                return result
            }
    }

    fun createOrderNumber(): String {
        return StringBuilder().append(System.currentTimeMillis()).append(Math.abs(Random.nextInt())).toString()

    }
}