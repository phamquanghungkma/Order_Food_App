package com.tofukma.orderapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.internal.service.Common
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.tofukma.orderapp.CallBack.IBestDealLoadCallBack
import com.tofukma.orderapp.CallBack.IPopularLoadCallBack
import com.tofukma.orderapp.Model.BestDealModel
import com.tofukma.orderapp.Model.PopularCategoryModel

class HomeViewModel : ViewModel(),IPopularLoadCallBack, IBestDealLoadCallBack {

    private  var popularListMutableLiveData: MutableLiveData<List<PopularCategoryModel>> ?= null
    private  var bestDealListMutableLiveData: MutableLiveData<List<BestDealModel>> ?= null

    private  lateinit var messageError:MutableLiveData<String>
    private  var popularLoadCallBackListener: IPopularLoadCallBack
    private var bestDealCallBackListener: IBestDealLoadCallBack


    val bestDealList:LiveData<List<BestDealModel>>
        get() {
            if(bestDealListMutableLiveData == null){

                bestDealListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadBestDealList()
            }
            return bestDealListMutableLiveData!!
        }

    private fun loadBestDealList() {
        val tempList = ArrayList<BestDealModel>()
        val bestDealRef = FirebaseDatabase.getInstance().getReference(com.tofukma.orderapp.Common.Common.BEST_DEALS_REF)
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

    val popularList:LiveData<List<PopularCategoryModel>>
     get(){
         if(popularListMutableLiveData == null)
         {
             popularListMutableLiveData =  MutableLiveData()
             messageError = MutableLiveData()
             loadPopularList()
         }
         return popularListMutableLiveData!!
     }

    init {

        popularLoadCallBackListener = this
        bestDealCallBackListener = this
    }

    override fun onPopularLoadSuccess(popularList: List<PopularCategoryModel>) {
        popularListMutableLiveData!!.value = popularList
    }

    override fun onPopularLoadFailed(message: String) {
        messageError.value = message
    }

    private fun loadPopularList(){
        val tempList = ArrayList<PopularCategoryModel>()
        val popularRef = FirebaseDatabase.getInstance().getReference(com.tofukma.orderapp.Common.Common.POPULAR_REF)
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
}