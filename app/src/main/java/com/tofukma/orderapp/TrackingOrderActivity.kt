package com.tofukma.orderapp

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.tofukma.orderapp.Remote.IGoogleAPI
import com.tofukma.orderapp.Remote.RetrofitGoogleAPIClient
import com.tofukma.orderapp.Utils.Common
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder

class TrackingOrderActivity : AppCompatActivity(), OnMapReadyCallback {

    private var shipperMarket:Marker?=null
    private var polylineOptions:PolylineOptions?=null
    private var blackPolylineOptions:PolylineOptions?=null
    private var redPolyline:Polyline?=null
    private lateinit var iGoogleAPI:IGoogleAPI
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mMap: GoogleMap
    private var polylineList:List<LatLng > = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_order)
        iGoogleAPI = RetrofitGoogleAPIClient.instance!!.create(IGoogleAPI::class.java)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.uiSettings.isZoomControlsEnabled = true
        try {

            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this,
              R.raw.uber_light_with_label))
            if(!success){
                Log.d("ToFuKMA","Failed to load map style")
            }
        } catch (ex: Resources.NotFoundException){
            Log.d("ToFuKMA","Not Found json string for map style")

        }
        drawRoutes()
    }

    private fun drawRoutes() {
        val locationOrder = LatLng(Common.currentShippingOrder!!.orderModel!!.lat,
            Common.currentShippingOrder!!.orderModel!!.lng)
        val locationShipper = LatLng(Common.currentShippingOrder!!.currentLat,Common.currentShippingOrder!!.currentLng)
        //addbox
        mMap.addMarker(MarkerOptions()
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.box))
                .title(Common.currentShippingOrder!!.orderModel!!.userName)
            .snippet(Common.currentShippingOrder!!.orderModel!!.shippingAddress)
            .position(locationOrder))
        //add shipper
        if(shipperMarket == null){
            val height = 80
            val width = 80
            val bitmapDrawable = ContextCompat.getDrawable(this@TrackingOrderActivity,R.drawable.shippernew)
            as BitmapDrawable
            val resizer = Bitmap.createScaledBitmap(bitmapDrawable.bitmap,width,height,false)

            shipperMarket = mMap.addMarker(MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(resizer))
                .title(Common.currentShippingOrder!!.shipperName)
                .snippet(Common.currentShippingOrder!!.shipperPhone)
                .position(locationShipper))

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationShipper,18.0f))

        }
        else{
            shipperMarket!!.position = locationShipper
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationShipper,18.0f))
        }
        val to = StringBuilder().append(Common.currentShippingOrder!!.orderModel!!.lat)
            .append(",")
            .append(Common.currentShippingOrder!!.orderModel!!.lng)
            .toString()
        val from = StringBuilder().append(Common.currentShippingOrder!!.currentLat)
            .append(",")
            .append(Common.currentShippingOrder!!.currentLng)
            .toString()
        compositeDisposable.add(iGoogleAPI!!.getDirections("driving", "less_driving",
            from, to ,
            getString(R.string.google_maps_key))!!
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ s->
                try{

                    val jsonObjects = JSONObject(s)
                    val jsonArray = jsonObjects.getJSONArray("routes")
                    for(i in 0 until jsonArray.length())
                    {
                        val route = jsonArray.getJSONObject(i)
                        val poly = route.getJSONObject("overview_polyline")
                        val polyline = poly.getString("points")
                        polylineList = Common.decodePoly(polyline)
                    }

                    polylineOptions = PolylineOptions()
                    polylineOptions!!.color(Color.RED)
                    polylineOptions!!.width(12.0f)
                    polylineOptions!!.startCap(SquareCap())
                    polylineOptions!!.endCap(SquareCap())
                    polylineOptions!!.jointType(JointType.ROUND)
                    polylineOptions!!.addAll(polylineList)
               redPolyline = mMap.addPolyline(polylineOptions)


                }catch (e: Exception){
                    Log.d("DEBUG",e.message.toString())
                }
            },{ throwable ->
                Toast.makeText(this@TrackingOrderActivity,"Loi"+throwable.message, Toast.LENGTH_SHORT).show()
                throwable.printStackTrace()
            }))
    }

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }
}