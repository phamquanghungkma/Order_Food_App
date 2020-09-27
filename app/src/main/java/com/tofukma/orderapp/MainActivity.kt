package com.tofukma.orderapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.textfield.TextInputLayout

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.tofukma.orderapp.Utils.Common
import com.tofukma.orderapp.Model.UserModel
import dmax.dialog.SpotsDialog
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class MainActivity : AppCompatActivity() {

    private var placeSelected: Place?=null
    private var places_fragment: AutocompleteSupportFragment?=null
    private lateinit var placeClient: PlacesClient
    private val placeFields = Arrays.asList(Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS,
        Place.Field.LAT_LNG
    )

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener
    private lateinit var dialog: android.app.AlertDialog
    private val compositeDisposable = CompositeDisposable()


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
//        setContentView(R.layout.activity_main)

        init()


    }

    private fun init() {
        Places.initialize(this,getString(R.string.google_maps_key))
        placeClient = Places.createClient(this)

        providers = Arrays.asList<AuthUI.IdpConfig>(AuthUI.IdpConfig.PhoneBuilder().build(),
        AuthUI.IdpConfig.EmailBuilder().build()
            )

        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE)
        firebaseAuth = FirebaseAuth.getInstance()
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
        listener = FirebaseAuth.AuthStateListener { firebaseAuth ->

            Dexter.withActivity(this@MainActivity)
                .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object:PermissionListener{
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            // Already login
                            checkUserFromFirebase(user)
                        } else {
                            phoneLogin()

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {

                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                       Toast.makeText(this@MainActivity,"Ban Phai Cap Quyen De Su Dung Ung Dung",Toast.LENGTH_SHORT).show()
                    }
                }).check()

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
        val phone_input_layout = itemView.findViewById<TextInputLayout>(R.id.phone_input_layout)
        val edt_name = itemView.findViewById<EditText>(R.id.edt_name)
        val edt_phone = itemView.findViewById<EditText>(R.id.edt_phone)

        val txt_address = itemView.findViewById<TextView>(R.id.txt_address_detail)


        places_fragment = supportFragmentManager.findFragmentById(R.id.places_autocomplete_fragment)
                as AutocompleteSupportFragment
        places_fragment!!.setPlaceFields(placeFields)
        places_fragment!!.setOnPlaceSelectedListener(object: PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                placeSelected = p0
                txt_address.text = placeSelected!!.address
            }

            override fun onError(p0: Status) {
                Toast.makeText(this@MainActivity,""+p0.statusMessage,Toast.LENGTH_SHORT).show()            }


        })
        // set
        if(user.phoneNumber == null || TextUtils.isEmpty(user.phoneNumber)){
            phone_input_layout.hint="Email"
            edt_phone.setText(user.email)
            edt_name.setText(user.displayName)
        }
        else{
            edt_phone.setText(user!!.phoneNumber)
        }




        edt_phone.setText(user!!.phoneNumber)
        builder.setView(itemView)
        builder.setNegativeButton("CANCEL", { dialogInterface, i -> dialogInterface.dismiss() })
        builder.setPositiveButton("REGISTER", { dialogInterface, i ->
            if(placeSelected != null ) {
                if (TextUtils.isDigitsOnly(edt_name.text.toString())) {
                    Toast.makeText(this@MainActivity, "Please enter your name ", Toast.LENGTH_LONG)
                        .show()
                    return@setPositiveButton
                }
                val userModel = UserModel()
                userModel.uid = user!!.uid
                userModel.name = edt_name.text.toString()
                userModel.addrss = txt_address.text.toString()
                userModel.phone = edt_phone.text.toString()
                userModel.lat = placeSelected!!.latLng!!.latitude
                userModel.lng = placeSelected!!.latLng!!.longitude

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
            } else {
                Toast.makeText(this@MainActivity,"Please select address",Toast.LENGTH_SHORT).show()

            }
        })
        //Importan please show dialog
        val dialog = builder.create()
        dialog.setOnDismissListener {
            val fragmentTransaction  = supportFragmentManager.beginTransaction()
            fragmentTransaction.remove(places_fragment!!)
            fragmentTransaction.commit()
        }
        dialog.show()
}

    private fun gotoHomeActivity(userModel: UserModel?) {

        FirebaseInstanceId.getInstance().instanceId.addOnFailureListener { e -> Toast.makeText(this@MainActivity,""+e.message,Toast.LENGTH_SHORT).show()

            Common.currentUser = userModel!!
//            Common.currentToken = token!!

            startActivity(Intent(this@MainActivity,HomeActivity::class.java))
            finish()
        }
            .addOnCompleteListener { task -> if (task.isSuccessful){


                Common.currentUser = userModel!!
//                Common.currentToken = token!!
                Common.updateToken(this@MainActivity,task.result!!.token)

                startActivity(Intent(this@MainActivity,HomeActivity::class.java))
                finish()

            }
            }



    }

    private fun phoneLogin() {

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setLogo(R.drawable.logo)
                .setAvailableProviders(providers!!).build(), APP_REQUEST_CODE)


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


