package com.tofukma.orderapp.ViewModel.restaurant

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tofukma.orderapp.CallBack.IRestaurantCallbackListener
import com.tofukma.orderapp.Model.CategoryModel
import com.tofukma.orderapp.Model.RestaurantModel
import com.tofukma.orderapp.Utils.Common

class RestaurantViewModel : ViewModel(), IRestaurantCallbackListener {
    private var restaurantsListMutable : MutableLiveData<List<RestaurantModel>>?= null
    private var messageError: MutableLiveData<String> = MutableLiveData()
    private  val restaurantCallBackListener: IRestaurantCallbackListener

    init { restaurantCallBackListener = this }

    fun getRestaurantList():MutableLiveData<List<RestaurantModel>>{
        if (restaurantsListMutable == null)
        {
            restaurantsListMutable = MutableLiveData()
            loadRestaurantFromFirebase()
        }
        return restaurantsListMutable!!
    }
    private fun loadRestaurantFromFirebase() {
        val tempList = ArrayList<RestaurantModel>()
        val restaurantRef = FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
        restaurantRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                restaurantCallBackListener.onRestaurantLoadFaild((error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                for (itemSnapShot in snapshot!!.children){
                    val model = itemSnapShot.getValue<RestaurantModel>(RestaurantModel::class.java)
                    model!!.uid = itemSnapShot.key!!
                    tempList.add(model!!)
                    }
                    if(tempList.size > 0)
                        restaurantCallBackListener.onRestaurantLoadSuccess(tempList)
                    else
                        restaurantCallBackListener.onRestaurantLoadFaild("Danh trong")
                }else
                    restaurantCallBackListener.onRestaurantLoadFaild("Nhà hàng không tồn tại")
            }
        })
    }

    fun getMessageError():MutableLiveData<String>{

        return messageError
    }

    override fun onRestaurantLoadSuccess(restaurantList: List<RestaurantModel>) {
        restaurantsListMutable!!.value = restaurantList
    }

    override fun onRestaurantLoadFaild(message: String) {
        messageError.value= message
    }
}