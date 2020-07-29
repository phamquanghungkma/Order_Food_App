package com.tofukma.orderapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tofukma.orderapp.CallBack.IRecyclerItemClickListener
import com.tofukma.orderapp.Common.Common
import com.tofukma.orderapp.Database.CartDataSource
import com.tofukma.orderapp.Database.CartDatabase
import com.tofukma.orderapp.Database.CartItem
import com.tofukma.orderapp.Database.LocalCartDataSource
import com.tofukma.orderapp.EventBus.CountCartEvent
import com.tofukma.orderapp.EventBus.FoodItemClick
import com.tofukma.orderapp.Model.FoodModel
import com.tofukma.orderapp.R
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

class MyFoodListAdapter (internal var context: Context,
                         internal var foodList: List<FoodModel>) :
    RecyclerView.Adapter<MyFoodListAdapter.MyViewHolder>() {
    private val compositeDisposable: CompositeDisposable
    private val cartDataSource: CartDataSource

    init {
        compositeDisposable = CompositeDisposable()
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var txt_food_name: TextView? = null
        var txt_food_price: TextView? = null

        var img_food_image: ImageView? = null
        var img_fav: ImageView? = null
        var img_cart: ImageView? = null

        internal var listener: IRecyclerItemClickListener? = null

        fun setListener(listener: IRecyclerItemClickListener) {
            this.listener = listener
        }

        init {
            txt_food_name = itemView.findViewById(R.id.txt_food_name) as TextView
            txt_food_price = itemView.findViewById(R.id.txt_food_price) as TextView

            img_food_image = itemView.findViewById(R.id.img_food_image) as ImageView
            img_fav = itemView.findViewById(R.id.img_fav) as ImageView
            img_cart = itemView.findViewById(R.id.img_quick_cart) as ImageView

            itemView.setOnClickListener(this)

        }

        override fun onClick(view: View?) {
            listener!!.onItemClick(view!!, adapterPosition)
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyFoodListAdapter.MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_food_item, parent, false)
        )

    }

    override fun getItemCount(): Int {
        return foodList.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(foodList.get(position).image).into(holder.img_food_image!!)
        holder.txt_food_name!!.setText(foodList.get(position).name)
        holder.txt_food_price!!.setText(foodList.get(position).price.toString())

        // Event
        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, post: Int) {
                Common.foodSelected = foodList.get(post)
                Common.foodSelected!!.key = post.toString()
                EventBus.getDefault().postSticky(FoodItemClick(true, foodList.get(post)))
            }


        })

        holder.img_cart!!.setOnClickListener {
            val cartItem = CartItem()
            cartItem.uid = Common.currentUser!!.uid
            cartItem.userPhone = Common.currentUser!!.phone
            cartItem.foodId = foodList.get(position).id!!
            cartItem.foodName = foodList.get(position).name!!
            cartItem.foodImage = foodList.get(position).image!!
            cartItem.foodPrice = foodList.get(position).price!!.toDouble()
            cartItem.foodQuantity = 1
            cartItem.foodExtraPrice = 0.0
            cartItem.foodAddon = "Default"
            cartItem.foodSize = "Default"

           cartDataSource.getItemWithAllOptionsInCart(Common.currentUser!!.uid!!,
           cartItem.foodId,
               cartItem.foodSize!!,
               cartItem.foodAddon!!)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(object : SingleObserver<CartItem>{
                   override fun onSuccess(cartItemFromDB: CartItem){
                       if (cartItemFromDB.equals(cartItem)) {
                           //if item alreadly in database just update
                           cartItemFromDB.foodExtraPrice = cartItem.foodExtraPrice
                           cartItemFromDB.foodAddon = cartItem.foodAddon
                           cartItemFromDB.foodSize = cartItem.foodSize
                           cartItemFromDB.foodQuantity =
                               cartItemFromDB.foodQuantity + cartItem.foodQuantity
                           cartDataSource.updatecart(cartItemFromDB)
                               .subscribeOn(Schedulers.io())
                               .observeOn(AndroidSchedulers.mainThread())
                               .subscribe(object : SingleObserver<Int>{
                                   override fun onSuccess(t: Int) {
                                       Toast.makeText(
                                           context,
                                           "Cập nhật giỏ hàng thành công",
                                           Toast.LENGTH_SHORT
                                       ).show()
                                       EventBus.getDefault().postSticky(CountCartEvent(true))
                                   }

                                   override fun onSubscribe(d: Disposable) {

                                   }

                                   override fun onError(e: Throwable) {
                                       Toast.makeText(
                                           context,
                                           "Cập nhật giỏ hàng" + e.message!!,
                                           Toast.LENGTH_SHORT
                                       ).show()
                                   }

                               })
                   } else{
                           //if item not available in databse , just insert
                           compositeDisposable.add(
                               cartDataSource.insertOrReplaceAll(cartItem)
                                   .subscribeOn(Schedulers.io())
                                   .observeOn(AndroidSchedulers.mainThread())
                                   .subscribe({
                                       Toast.makeText(
                                           context,
                                           "Thêm hàng thành công",
                                           Toast.LENGTH_SHORT
                                       ).show()
                                       //Here we will send a notify to HomeActivity to update CounterFab
                                       EventBus.getDefault().postSticky(CountCartEvent(true))
                                   }, { t: Throwable? ->
                                       Toast.makeText(
                                           context,
                                           "(Thêm hàng)" + t!!.message,
                                           Toast.LENGTH_SHORT
                                       ).show()
                                   }
                                   )
                           )
                       }
                   }

                   override fun onError(e: Throwable) {
                       if(e.message!!.contains("empty")){
                           compositeDisposable.add(
                               cartDataSource.insertOrReplaceAll(cartItem)
                                   .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                   .subscribe({
                                       Toast.makeText(context, "Thêm hàng thành công", Toast.LENGTH_SHORT).show()
                                       //Here we will send a notify to HomeActivity to update CounterFab
                                       EventBus.getDefault().postSticky(CountCartEvent(true))
                                   }, { t: Throwable? ->
                                       Toast.makeText(context, "(Thêm hàng)" + t!!.message, Toast.LENGTH_SHORT)
                                           .show()
                                        })

                           )
                   }else{
                          Toast.makeText(context,"[Giỏ hàng lỗi]"+e.message,Toast.LENGTH_SHORT).show()
                       }
               }

                   override fun onSubscribe(d: Disposable) {
                       TODO("Not yet implemented")
                   }

               })
        }
        fun onStop(){
            if(compositeDisposable !=null)
                compositeDisposable.clear()
        }


    }
}
