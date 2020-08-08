package com.tofukma.orderapp

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.tofukma.orderapp.Database.CartDataSource
import com.tofukma.orderapp.Database.CartDatabase
import com.tofukma.orderapp.Database.LocalCartDataSource
import com.tofukma.orderapp.EventBus.CategoryClick
import com.tofukma.orderapp.EventBus.CountCartEvent
import com.tofukma.orderapp.EventBus.FoodItemClick
import com.tofukma.orderapp.EventBus.HideFABCart
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var cartDataSource: CartDataSource
    private lateinit var navController: NavController

    override fun onResume(){
        super.onResume()
        countCartItem()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(this).cartDAO())

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            navController.navigate(R.id.nav_cart)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_menu, R.id.nav_food_detail,
                R.id.nav_cart
//                ,R.id.nav_send
           ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        countCartItem()



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)

    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onCategorySelected(event:CategoryClick){
        if(event.isSuccess){
//            Toast.makeText(this,"Click to " +event.category.name,Toast.LENGTH_SHORT).show()

            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_food_list)
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onFoodSelected(event: FoodItemClick){
        if(event.isSuccess){
//            Toast.makeText(this,"Click to " +event.category.name,Toast.LENGTH_SHORT).show()

            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_food_detail)
        }
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onHideFABEvent(event: HideFABCart){
        if(event.isHide){

            fab.hide()

        }else{
            fab.show()
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onCountCartEvent(event: CountCartEvent){
        if(event.isSuccess){

            countCartItem()

        }
    }

    private fun countCartItem() {
        cartDataSource.countItemInCart(com.tofukma.orderapp.Common.Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<Int>{
                override fun onSuccess(t: Int) {
                    fab.count = t
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                    Toast.makeText(this@HomeActivity,"[COUNT CART]"+e.message,Toast.LENGTH_SHORT).show()
                }
            })

            }
}