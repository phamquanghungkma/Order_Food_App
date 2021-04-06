package com.tofukma.orderapp.ViewModel.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tofukma.orderapp.CallBack.IBestDealLoadCallBack
import com.tofukma.orderapp.CallBack.IPopularLoadCallBack
import com.tofukma.orderapp.Model.BestDealModel
import com.tofukma.orderapp.Model.PopularCategoryModel
import com.tofukma.orderapp.Utils.Common

class HomeViewModel : ViewModel(),IPopularLoadCallBack, IBestDealLoadCallBack{
//    ICategoryCallBackListener


//    private var categoriesListMutable : MutableLiveData<List<CategoryModel>> ?= null

    private  var popularListMutableLiveData: MutableLiveData<List<PopularCategoryModel>> ?= null
    private  var bestDealListMutableLiveData: MutableLiveData<List<BestDealModel>> ?= null

    private  lateinit var messageError:MutableLiveData<String>
    private  var popularLoadCallBackListener: IPopularLoadCallBack
    private var bestDealCallBackListener: IBestDealLoadCallBack
//    private  val categoryCallBackListener: ICategoryCallBackListener





    fun getBestDealList(key: String):LiveData<List<BestDealModel>>
         {
            if(bestDealListMutableLiveData == null){

                bestDealListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadBestDealList(key)
            }
            return bestDealListMutableLiveData!!
        }

    private fun loadBestDealList(key:String) {
        val tempList = ArrayList<BestDealModel>()
        val bestDealRef = FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
            .child(key).child(Common.BEST_DEALS_REF)

        bestDealRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                bestDealCallBackListener.onBestDealLoadFailed((error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapShot in snapshot!!.children){
                    val model = itemSnapShot.getValue<BestDealModel>(BestDealModel::class.java)
                    tempList.add(model!!)
                }
                bestDealCallBackListener.onBestDealLoadSucess(tempList)
            }
        })

    }

    fun  getPopularList(key:String):LiveData<List<PopularCategoryModel>>
     {
         if(popularListMutableLiveData == null)
         {
             popularListMutableLiveData =  MutableLiveData()
             messageError = MutableLiveData()
             loadPopularList(key)
         }
         return popularListMutableLiveData!!
     }

    init {

        popularLoadCallBackListener = this
        bestDealCallBackListener = this
//        categoryCallBackListener = this
    }

    override fun onPopularLoadSuccess(popularList: List<PopularCategoryModel>) {
        popularListMutableLiveData!!.value = popularList
    }

    override fun onPopularLoadFailed(message: String) {
        messageError.value = message
    }

    private fun loadPopularList(key: String){
        val tempList = ArrayList<PopularCategoryModel>()
        val popularRef = FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF).child(key).child(Common.POPULAR_REF)
        popularRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                popularLoadCallBackListener.onPopularLoadFailed((error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                 for (itemSnapShot in snapshot!!.children){
                     val model = itemSnapShot.getValue<PopularCategoryModel>(PopularCategoryModel::class.java)
                     tempList.add(model!!)
                 }
                popularLoadCallBackListener.onPopularLoadSuccess(tempList)
            }
        })
    }

    override fun onBestDealLoadSucess(bestDealList: List<BestDealModel>) {
        bestDealListMutableLiveData!!.value = bestDealList
    }

    override fun onBestDealLoadFailed(message: String) {
        messageError.value = message
    }

//    override fun onCategoryLoadSuccess(categoriesList: List<CategoryModel>) {
//        categoriesListMutable!!.value = categoriesList
//    }

//    override fun onCategoryLoadFailed(message: String) {
//        messageError!!.value = message
//    }
//    fun getCategoryList():MutableLiveData<List<CategoryModel>>{
//        if (categoriesListMutable == null)
//        {
//            categoriesListMutable = MutableLiveData()
//            loadCategory()
//        }
//        return categoriesListMutable!!
//    }
    fun getMessageError():MutableLiveData<String>{

        return messageError
    }

//    private fun loadCategory() {
//        val tempList = ArrayList<CategoryModel>()
//        val categoryRef = FirebaseDatabase.getInstance().getReference(com.tofukma.orderapp.Common.Common.CATEGORY_REF)
//        categoryRef.addListenerForSingleValueEvent(object: ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//                categoryCallBackListener.onCategoryLoadFailed((error.message))
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (itemSnapShot in snapshot!!.children){
//                    val model = itemSnapShot.getValue<CategoryModel>(CategoryModel::class.java)
//                    model!!.menu_id = itemSnapShot.key
//                    tempList.add(model!!)
//                }
//                categoryCallBackListener.onCategoryLoadSuccess(tempList)
//            }
//        })
//
//
//
//    }
}