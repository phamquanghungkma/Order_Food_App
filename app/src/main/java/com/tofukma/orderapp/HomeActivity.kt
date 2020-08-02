package com.tofukma.orderapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.tofukma.orderapp.Common.Common
import com.tofukma.orderapp.Common.Common.currentUser
import com.tofukma.orderapp.Database.CartDataSource
import com.tofukma.orderapp.Database.CartDatabase
import com.tofukma.orderapp.Database.LocalCartDataSource
import com.tofukma.orderapp.EventBus.CategoryClick
import com.tofukma.orderapp.EventBus.CountCartEvent
import com.tofukma.orderapp.EventBus.FoodItemClick
import com.tofukma.orderapp.Model.CategoryModel
import kotlinx.android.synthetic.main.layout_category_item.*
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.app_bar_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.sql.CommonDataSource

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var carDataSource: CommonDataSource
    private lateinit var navController: NavController
    private  var drawer : DrawerLayout?=null
    private lateinit var cartDataSource: CartDataSource

    override fun onResume(){
        super.onResume()
//        countCartItem()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(this).cartDAO())
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        drawer = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_menu, R.id.nav_food_detail,
                R.id.nav_cart
            ), drawer
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        var headerView = navView.getHeaderView(0)
        var txt_user = headerView.findViewById<TextView>(R.id.txt_user)
        Common.setSpanString("Hey, ", Common.currentUser!!.name, txt_user)

        navView.setNavigationItemSelectedListener(object:NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(p0: MenuItem): Boolean {

                p0.isChecked = true
                drawer!!.closeDrawers()
                if (p0.itemId == R.id.nav_sign_out)
                {
                    singOut()
                }
                else if(p0.itemId == R.id.nav_home)
                {
                    navController.navigate(R.id.nav_home)
                }
                else if(p0.itemId == R.id.nav_cart)
                {
                    navController.navigate(R.id.nav_cart)
                }
                else if(p0.itemId == R.id.nav_menu)
                {
                    navController.navigate(R.id.nav_menu)
                }
                return true
            }
        })

//        countCartItem()
    }

    private fun singOut() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        Log.e("Check" , builder.toString() )
        builder.setTitle("Sing Out")
            .setMessage("Do you realy want to exit?")
            .setNegativeButton("CANEL", {dialogInterface, _ -> dialogInterface.dismiss() })
            .setPositiveButton("OKE"){ dialogInterface, _ ->
                Common.foodSelected = null
                Common.categorySelected = null
                Common.currentUser = null
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this@HomeActivity , MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        val dialog = builder.create()
        dialog.show()
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
    fun onCountCartEvent(event: CountCartEvent){
        if(event.isSuccess){

//            countCartItem()

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