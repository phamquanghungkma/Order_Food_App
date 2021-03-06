package com.tofukma.orderapp

import android.accounts.Account
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AlertDialogLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.tofukma.orderapp.Common.Common
import com.tofukma.orderapp.Model.UserModel
import com.tofukma.orderapp.Remote.ICloudFuntions
import com.tofukma.orderapp.Remote.RetrofitCloudClient
import dmax.dialog.SpotsDialog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import kotlinx.android.synthetic.main.layout_register.*
import org.w3c.dom.Text
import retrofit2.Retrofit
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener
    private lateinit var dialog: android.app.AlertDialog
    private val compositeDisposable = CompositeDisposable()
    private lateinit var cloudFunctions: ICloudFuntions


    private lateinit var userRef: DatabaseReference
    private var providers: List<AuthUI.IdpConfig>? = null

    companion object {
        private val APP_REQUEST_CODE = 7171 // any number

    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(listener)
    }

    override fun onStop() {
        if (listener != null)
            firebaseAuth.removeAuthStateListener(listener)
        compositeDisposable.clear()
        super.onStop()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()


    }

    private fun init() {
        providers = Arrays.asList<AuthUI.IdpConfig>(AuthUI.IdpConfig.PhoneBuilder().build())

        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE)
        firebaseAuth = FirebaseAuth.getInstance()
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
        cloudFunctions = RetrofitCloudClient.getInstance().create(ICloudFuntions::class.java)
        listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // Already login
                checkUserFromFirebase(user)
            } else {
                phoneLogin()

            }
        }

    }

    private fun checkUserFromFirebase(user: FirebaseUser) {
        dialog!!.show()
        userRef!!.child(user!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "" + error.message, Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userModel = snapshot.getValue(UserModel::class.java)
                        gotoHomeActivity(userModel)
                    } else {
                        showRegisterDialog(user)
                    }
                    dialog!!.dismiss()
                }


            })
    }

    private fun showRegisterDialog(user: FirebaseUser) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("REGISTER")
        builder.setMessage("Please fill information")

        val itemView = LayoutInflater.from(this@MainActivity)
            .inflate(R.layout.layout_register, null)

        val edt_name = itemView.findViewById<EditText>(R.id.edt_name)
        val edt_address = itemView.findViewById<EditText>(R.id.edt_address)
        val edt_phone = itemView.findViewById<EditText>(R.id.edt_phone)

        // set
        edt_phone.setText(user!!.phoneNumber)
        builder.setView(itemView)
        builder.setNegativeButton("CANCEL", { dialogInterface, i -> dialogInterface.dismiss() })
        builder.setPositiveButton("REGISTER", { dialogInterface, i ->
            if (TextUtils.isDigitsOnly(edt_name.text.toString())) {
                Toast.makeText(this@MainActivity, "Please enter your name ", Toast.LENGTH_LONG)
                    .show()
                return@setPositiveButton
            } else if (TextUtils.isDigitsOnly(edt_address.text.toString())) {
                Toast.makeText(this@MainActivity, "Please enter your address ", Toast.LENGTH_LONG)
                    .show()
                return@setPositiveButton
            }
            val userModel = UserModel()
            userModel.uid = user!!.uid
            userModel.name = edt_name.text.toString()
            userModel.addrss = edt_address.text.toString()
            userModel.phone = edt_phone.text.toString()

            userRef!!.child(user!!.uid)
                .setValue(userModel).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        dialogInterface.dismiss()
                        Toast.makeText(
                            this@MainActivity,
                            "Congratulation! Register Success!",
                            Toast.LENGTH_LONG
                        ).show()

                        gotoHomeActivity(userModel)
                    }
                }
        })
        //Importan please show dialog
        val dialog = builder.create()
        dialog.show()
}

    private fun gotoHomeActivity(userModel: UserModel?) {
        Common.currentUser = userModel!!
        startActivity(Intent(this@MainActivity,HomeActivity::class.java))
        finish()

    }

    private fun phoneLogin() {

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers!!)
                .build(), APP_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
            } else {
                Toast.makeText(this, "Failed to sign in ", Toast.LENGTH_LONG).show()

            }
        }
    }
}


