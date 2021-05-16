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
import com.tofukma.orderapp.Model.HighRatingModel
import com.tofukma.orderapp.Model.PopularCategoryModel
import com.tofukma.orderapp.Model.RecommendModel
import com.tofukma.orderapp.Utils.Common

class HomeViewModel : ViewModel(),IPopularLoadCallBack, IBestDealLoadCallBack{
//    ICategoryCallBackListener


//    private var categoriesListMutable : MutableLiveData<List<CategoryModel>> ?= null

    private  var popularListMutableLiveData: MutableLiveData<List<PopularCategoryModel>> ?= null
    private  var bestDealListMutableLiveData: MutableLiveData<List<BestDealModel>> ?= null
    private var recommendListMutableLiveData= MutableLiveData<List<RecommendModel>>()

    private lateinit var messageError:MutableLiveData<String>
    private  var popularLoadCallBackListener: IPopularLoadCallBack
    private var bestDealCallBackListener: IBestDealLoadCallBack
//    private  val categoryCallBackListener: ICategoryCallBackListener


    val dataTest=MutableLiveData<List<RecommendModel>>()

    fun getRecommenndList(key:String)//
    {
        if(recommendListMutableLiveData == null){
            loadRecommendList()
        }
       /// return recommendListMutableLiveData!!
    }

    val listTest= mutableListOf<RecommendModel>()
    var listRecomnend=MutableLiveData<MutableList<RecommendModel>>()
     fun loadRecommendList() {
        Log.d("current uid",Common.currentUser!!.uid.toString())
        FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
            .child(Common.currentRestaurant!!.uid)
            .child(Common.RECOMMENDATION_REF)
            .child(Common.currentUser!!.uid!!)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("Loi",p0.toString())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (pos in p0.children){
                        val model=pos.getValue(RecommendModel::class.java)
                        listTest.add(model!!)
                    }
                    listRecomnend.value=listTest
                }

            })
    }
    // High Rating
    var listRating= mutableListOf<HighRatingModel>()
    var listHighRating = MutableLiveData<MutableList<HighRatingModel>>()
    fun loadHighRatingList() {

       val dataFirebase= FirebaseDatabase.getInstance().getReference("Restaurant")
           .child(Common.currentRestaurant!!.uid)
           .child("BestRating")
        var query=dataFirebase.limitToFirst(3)
            query.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                   // Log.d("listRate","$p0")
                    for (pos in p0.children){
                        var model=pos.getValue(HighRatingModel::class.java)
                        var objectModel=HighRatingModel(
                            pos.child("food_id").value.toString(),
                            pos.child("image").value.toString(),
                            pos.child("menu_id").value.toString(),
                            pos.child("name").value.toString()

                        )
                        Log.d("listRate","${pos.child("food_id").value.toString()}")
                       listRating.add(objectModel)
                    }



                }



            })
    }


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