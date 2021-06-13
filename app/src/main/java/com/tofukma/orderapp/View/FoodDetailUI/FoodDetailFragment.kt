package com.tofukma.orderapp.View.FoodDetailUI

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.andremion.counterfab.CounterFab
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.gson.Gson
import com.tofukma.orderapp.Utils.Common
import com.tofukma.orderapp.Database.CartDataSource
import com.tofukma.orderapp.Database.CartDatabase
import com.tofukma.orderapp.Database.CartItem
import com.tofukma.orderapp.Database.LocalCartDataSource
import com.tofukma.orderapp.EventBus.CountCartEvent
import com.tofukma.orderapp.EventBus.MenuItemBack
import com.tofukma.orderapp.Model.CommentModel
import com.tofukma.orderapp.Model.FoodModel
import com.tofukma.orderapp.R
import com.tofukma.orderapp.View.CommentUI.CommentFragment
import com.tofukma.orderapp.ViewModel.fooddetail.FoodDetailViewModel
import dmax.dialog.SpotsDialog
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class FoodDetailFragment : Fragment(), TextWatcher {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var cartDataSource: CartDataSource
    private lateinit var foodDetailViewModel: FoodDetailViewModel
    private lateinit var addonBottomSheetDialog: BottomSheetDialog
    private var img_food: ImageView? = null
    private var btnCart: CounterFab? = null
    private var btnRating: FloatingActionButton? = null
    private var food_name: TextView? = null
    private var food_description: TextView? = null
    private var food_price: TextView? = null
    private var number_button: ElegantNumberButton? = null
    private var ratingBar: RatingBar? = null
    private var btnShowComment: Button? = null
    private var rdi_group_size: RadioGroup? = null
    private var img_add_on: ImageView? = null
    private var chip_group_user_selected_addon: ChipGroup? = null
    //Addon layout
    private var chip_group_addon: ChipGroup? = null
    private var edt_search_addon: EditText? = null
    private var waitingDialog: android.app.AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        foodDetailViewModel =
            ViewModelProviders.of(this).get(FoodDetailViewModel::class.java)
        foodDetailViewModel.getMutableLiveDataFood().observe(viewLifecycleOwner, Observer {
            displayInfo(it)
        })
        foodDetailViewModel.getMutableLiveDataComment().observe(this, Observer {
            submitRatingToFirebase(it)
        })
        val root = inflater.inflate(R.layout.fragment_food_detail, container, false)
        initViews(root)
        return root
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        chip_group_addon!!.clearCheck()
        chip_group_addon!!.removeAllViews()
        for (addonModel in Common.foodSelected!!.addon!!) {
            if (addonModel.name!!.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                val chip = layoutInflater.inflate(R.layout.layout_chip, null, false) as Chip
                chip.text = StringBuilder(addonModel.name!!).append("+VND").append(addonModel.price)
                    .append(")").toString()
                chip.setOnCheckedChangeListener { compounButton, b ->
                    if (b) {
                        if (Common.foodSelected!!.userSelectedAddon == null)
                            Common.foodSelected!!.userSelectedAddon = ArrayList()
                        Common.foodSelected!!.userSelectedAddon!!.add(addonModel)
                    }
                }
                chip_group_addon!!.addView(chip)
            }
        }
    }


    private fun submitRatingToFirebase(commentModel: CommentModel?) {
        waitingDialog!!.show()

        //first, we will submit to comment  Ref
        FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
            .child(Common.currentRestaurant!!.uid!!)
            .child(Common.COMMENT_REF)
            .child(Common.foodSelected!!.id!!)
            .push().setValue(commentModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addRatingToFood(commentModel!!.ratingValue.toDouble())
                } else {
                    waitingDialog!!.dismiss()
                }
            }
    }

    private fun addRatingToFood(ratingValue: Double) {
        FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
            .child(Common.currentRestaurant!!.uid!!)
            .child(Common.CATEGORY_REF)//Select category
            .child(Common.categorySelected!!.menu_id!!)//select menu id in category
            .child("foods")//select food array
            .child(Common.foodSelected!!.key!!)// select key
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    waitingDialog!!.dismiss()
                    Toast.makeText(context!!, "" + error, Toast.LENGTH_SHORT).show()

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val foodModel = snapshot.getValue(FoodModel::class.java)
                        foodModel!!.key = Common.foodSelected!!.key
                        //Apply rating
                        val sumRating = foodModel.ratingValue!!.toDouble() + (ratingValue)
                        val ratingCount = foodModel.ratingCount + 1


                        val updateData = HashMap<String, Any>()
                        updateData["ratingValue"] = sumRating
                        updateData["ratingCount"] = ratingCount

                        //Update data in variable
                        foodModel.ratingCount = ratingCount
                        foodModel.ratingValue = sumRating

                        snapshot.ref.updateChildren(updateData)
                            .addOnCompleteListener { task ->
                                waitingDialog!!.dismiss()
                                if (task.isSuccessful) {
                                    Common.foodSelected = foodModel
                                    foodDetailViewModel!!.setFoodModel(foodModel)
                                    Toast.makeText(context!!, "Thank you", Toast.LENGTH_SHORT)
                                        .show()

                                }
                            }
                    } else
                        waitingDialog!!.dismiss()
                }

            })

    }

    private fun displayInfo(it: FoodModel?) {
        Glide.with(requireContext()).load(it!!.image).into(img_food!!)
        food_name!!.text = StringBuilder(it!!.name!!)
        food_description!!.text = StringBuilder(it!!.description!!)
        food_price!!.text = StringBuilder(it!!.price!!.toString())

        ratingBar!!.rating = it!!.ratingValue.toFloat() / it!!.ratingCount

        //Set size
        for (sizeModel in it!!.size) {
            val radioButton = RadioButton(context)
            radioButton.setOnCheckedChangeListener { compoundButton, b ->
                if (b)
                    Common.foodSelected!!.userSelectedSize = sizeModel
                calculateTotalPrice()
            }
            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f
            )
            radioButton.layoutParams = params
            radioButton.text = sizeModel.name
            radioButton.tag = sizeModel.price

            rdi_group_size!!.addView(radioButton)
        }

        // Default first radio button select
        if (rdi_group_size!!.childCount > 0) {
            val radioButton = rdi_group_size!!.getChildAt(0) as RadioButton
            radioButton.isChecked = true
        }

    }

    private fun calculateTotalPrice() {
        var totalPrice = Common.foodSelected!!.price.toDouble()
        var displayPrice = 0.0

        //Addon
        if (Common.foodSelected!!.userSelectedAddon != null && Common.foodSelected!!.userSelectedAddon!!.size > 0) {
            for (addonModel in Common.foodSelected!!.userSelectedAddon!!)
                totalPrice += (1000 * addonModel.price!!.toDouble())
        }

        //Size
        totalPrice += (1000 * Common.foodSelected!!.userSelectedSize!!.price!!.toDouble())
        Log.e("Giá tiền =>>>>>", totalPrice!!.toString())
        displayPrice = totalPrice * number_button!!.number.toInt()
        displayPrice = Math.round(displayPrice * 100.0) / 100.0

        food_price!!.text = StringBuilder("").append(Common.formatPrice(displayPrice)).toString()
    }

    private fun initViews(root: View?) {
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context!!).cartDAO())
        addonBottomSheetDialog = BottomSheetDialog(context!!, R.style.DialogStyle)
        val layout_user_selected_addon = layoutInflater.inflate(R.layout.layout_addon_display, null)
        chip_group_addon =
            layout_user_selected_addon.findViewById(R.id.chip_group_addon) as ChipGroup
        edt_search_addon = layout_user_selected_addon.findViewById(R.id.edt_search) as EditText
        addonBottomSheetDialog.setContentView(layout_user_selected_addon)

        addonBottomSheetDialog.setOnDismissListener { dialogInterface ->
            displayUserSelectedAddon()
            calculateTotalPrice()
        }


        waitingDialog = SpotsDialog.Builder().setContext(context!!).setCancelable(false).build()
        btnCart = root!!.findViewById(R.id.btnCart) as CounterFab
        img_food = root!!.findViewById(R.id.img_food) as ImageView
        btnRating = root!!.findViewById(R.id.btn_rating) as FloatingActionButton
        food_name = root!!.findViewById(R.id.food_name) as TextView
        food_description = root!!.findViewById(R.id.food_description) as TextView
        food_price = root!!.findViewById(R.id.food_price) as TextView
        number_button = root!!.findViewById(R.id.number_button) as ElegantNumberButton
        ratingBar = root!!.findViewById(R.id.ratingBar) as RatingBar
        btnShowComment = root!!.findViewById(R.id.btnShowComment) as Button
        rdi_group_size = root!!.findViewById(R.id.rdi_group_size) as RadioGroup
        img_add_on = root!!.findViewById(R.id.img_add_addon) as ImageView
        chip_group_user_selected_addon =
            root!!.findViewById(R.id.chip_group_user_selected_addon) as ChipGroup

        //Event
        img_add_on!!.setOnClickListener {
            if (Common.foodSelected!!.addon != null) {
                displayAlladdon()
                addonBottomSheetDialog.show()
            }
        }
        btnRating!!.setOnClickListener {
            showDialogRating()

        }
        btnShowComment!!.setOnClickListener {
            val commentFragment = CommentFragment.getInstance()
            commentFragment.show(activity!!.supportFragmentManager, "CommentFragment")
        }
        fun addItemToCart(cartItem: CartItem) {
            cartItem.restaurantId = Common.currentRestaurant!!.uid
            cartItem.uid = Common.currentUser!!.uid
            cartItem.userPhone = Common.currentUser!!.phone
            cartItem.foodId = Common.foodSelected!!.id!!
            cartItem.foodName = Common.foodSelected!!.name!!
            cartItem.foodImage = Common.foodSelected!!.image!!
            cartItem.foodPrice = Common.foodSelected!!.price!!.toDouble()
            cartItem.foodQuantity = number_button!!.number.toInt()
            cartItem.foodExtraPrice = Common.calcutaleExtraPrice(
                Common.foodSelected!!.userSelectedSize,
                Common.foodSelected!!.userSelectedAddon
            )

            if (Common.foodSelected!!.userSelectedAddon != null)
                cartItem.foodAddon = Gson().toJson(Common.foodSelected!!.userSelectedAddon)
            else
                cartItem.foodAddon = "Default"

            if (Common.foodSelected!!.userSelectedSize != null)
                cartItem.foodSize = Gson().toJson(Common.foodSelected!!.userSelectedSize)
            else {
                cartItem.foodSize = "Default"
            }
        }

        btnCart!!.setOnClickListener {
            val cartItem = CartItem()
            addItemToCart(cartItem)
            cartDataSource.getItemWithAllOptionsInCart(
                Common.currentUser!!.uid!!,
                cartItem.foodId,
                cartItem.foodSize!!,
                cartItem.foodAddon!!,
                Common.currentRestaurant!!.uid
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<CartItem> {
                    override fun onSuccess(cartItemFromDB: CartItem) {
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
                                .subscribe(object : SingleObserver<Int> {
                                    override fun onSuccess(t: Int) {
                                        Log.d("CHECKKK", "updateCart onSuccess")
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
                                        Log.d("CHECKKK", "updateCart onError")
                                        Toast.makeText(
                                            context,
                                            "Cập nhật giỏ hàng" + e.message!!,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                })
                        } else {
                            //if item not available in databse , just insert
                            compositeDisposable.add(
                                cartDataSource.insertOrReplaceAll(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        Toast.makeText(context, "Thêm hàng thành công", Toast.LENGTH_SHORT).show()
                                        //Here we will send a notify to HomeActivity to update CounterFab
                                        EventBus.getDefault().postSticky(CountCartEvent(true))
                                    }, { t: Throwable? ->
                                        Toast.makeText(context, "(Thêm hàng)" + t!!.message, Toast.LENGTH_SHORT).show()
                                    })
                            )
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.d("CHECKKK", "onError1")
                        if (e.message!!.contains("empty")) {
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
                                        )
                                            .show()
                                    })

                            )
                        } else {
                            Toast.makeText(
                                context,
                                "[Giỏ hàng lỗi]" + e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.d("CHECKKK", "onSubscribe")
                    }

                })


        }

    }


    private fun displayAlladdon() {
        if (Common.foodSelected!!.addon!!.size > 0) {
            chip_group_addon!!.clearCheck()
            chip_group_addon!!.removeAllViews()

            edt_search_addon!!.addTextChangedListener(this)

            for (addonModel in Common.foodSelected!!.addon!!) {
                val chip = layoutInflater.inflate(R.layout.layout_chip, null, false) as Chip
                chip.text =
                    StringBuilder(addonModel!!.name!!).append("(+$").append(addonModel.price)
                        .append("000)").toString()
                chip.setOnCheckedChangeListener { compoundButton, b ->
                    if (b) {
                        if (Common.foodSelected!!.userSelectedAddon == null)
                            Common.foodSelected!!.userSelectedAddon = ArrayList()
                        Common.foodSelected!!.userSelectedAddon!!.add(addonModel)

                    }
                }
                chip_group_addon!!.addView(chip)
            }
        }
    }

    private fun displayUserSelectedAddon() {
        if (Common.foodSelected!!.userSelectedAddon != null && Common.foodSelected!!.userSelectedAddon!!.size > 0) {
            chip_group_user_selected_addon!!.removeAllViews()
            for (addonModel in Common.foodSelected!!.userSelectedAddon!!) {
                val chip =
                    layoutInflater.inflate(
                        R.layout.layout_chip_with_delete,
                        null,
                        false
                    ) as Chip
                chip.text =
                    StringBuilder(addonModel.name).append("(+$").append(addonModel.price)
                        .append(",000)").toString()
                chip.isCheckable = false
                chip.setOnCloseIconClickListener { view ->
                    chip_group_user_selected_addon!!.removeView(view)
                    Common.foodSelected!!.userSelectedAddon!!.remove(addonModel)
                    calculateTotalPrice()
                }
                chip_group_user_selected_addon!!.addView(chip)
            }
        } else
            chip_group_user_selected_addon!!.removeAllViews()
    }

    private fun showDialogRating() {
        // show dialog rating food
        var builder = AlertDialog.Builder(context!!)
        builder.setTitle("Rating Food")
        builder.setMessage("Please fill information")

        val itemView =
            LayoutInflater.from(context).inflate(R.layout.layout_rating_comment, null)
        val ratingBar = itemView.findViewById<RatingBar>(R.id.rating_bar)
        val edt_comment = itemView.findViewById<EditText>(R.id.edt_comment)

        builder.setView(itemView)

        builder.setNegativeButton("CANCEL") { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.setPositiveButton("OK") { dialogInterface, i ->
            //
            val commentModel = CommentModel()
            commentModel.name = Common.currentUser!!.name
            commentModel.uid = Common.currentUser!!.uid
            commentModel.comment = edt_comment.text.toString()
            commentModel.ratingValue = ratingBar.rating
            val serverTimeStamp = HashMap<String, Any>()
            serverTimeStamp["timeStamp"] = ServerValue.TIMESTAMP
            commentModel.commentTimeStamp = (serverTimeStamp)

            foodDetailViewModel!!.setCommentModel(commentModel)
        }
        val dialog = builder.create()
        dialog.show()
    }
    override fun onDestroy() {
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()

    }

}