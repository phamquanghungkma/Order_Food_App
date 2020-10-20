package com.tofukma.orderapp

import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.tofukma.orderapp.Model.ShippingOrderModel
import com.tofukma.orderapp.Remote.IGoogleAPI
import com.tofukma.orderapp.Remote.RetrofitGoogleAPIClient
import com.tofukma.orderapp.Utils.Common
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder

class TrackingOrderActivity : AppCompatActivity(), OnMapReadyCallback, ValueEventListener {

    private var shipperMarket:Marker?=null
    private var polylineOptions:PolylineOptions?=null
    private var blackPolylineOptions:PolylineOptions?=null
    private var redPolyline:Polyline?=null
    private lateinit var iGoogleAPI:IGoogleAPI
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mMap: GoogleMap
    private var polylineList:List<LatLng > = ArrayList()
    private lateinit var shipperRef:DatabaseReference
    private  var blackPolyline:Polyline?=null
    private  var grayPolyline:Polyline?=null
    private var isInit = false

    //MoveMarker
    private var handler:Handler?=null
    private var index=0
    private var next:Int=0
    private var v=0f
    private var lat=0.0
    private var lng=0.0
    private var startPosition=LatLng(0.0,0.0)
    private var endPosition = LatLng(0.0,0.0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_order)
        iGoogleAPI = RetrofitGoogleAPIClient.instance!!.create(IGoogleAPI::class.java)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        subscribeShipperMove()
    }

    private fun subscribeShipperMove() {
    shipperRef = FirebaseDatabase.getInstance()
        .getReference(Common.SHIPPING_ORDER_REF)
        .child(Common.currentShippingOrder!!.key!!)
        shipperRef.addValueEventListener(this)

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

    override fun onDestroy() {
        shipperRef.removeEventListener(this)
        isInit=false
        super.onDestroy()
    }
    override fun onDataChange(dataSnapshot: DataSnapshot) {
        //save old position
        val from = StringBuilder()
            .append(Common.currentShippingOrder!!.currentLat)
            .append(",")
            .append(Common.currentShippingOrder!!.currentLng)
            .toString()
        //Update position
        Common.currentShippingOrder = dataSnapshot.getValue(ShippingOrderModel::class.java)
        Common.currentShippingOrder!!.key = dataSnapshot.key
        //save new position
        val to = StringBuilder()
            .append(Common.currentShippingOrder!!.currentLat)
            .append(",")
            .append(Common.currentShippingOrder!!.currentLng)
            .toString()
        if(dataSnapshot.exists())
            if (isInit) moveMarkerAnimation(shipperMarket,from,to) else isInit=true
    }

    private fun moveMarkerAnimation(shipperMarket: Marker?, from: String, to: String) {
        compositeDisposable.add(iGoogleAPI!!.getDirections("driving",
            "less_driving",
            from,
            to,
            getString(R.string.google_maps_key))!!
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ s->
                Log.d("DEBUG",s.toString() )
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
                    polylineOptions!!.color(Color.GRAY)
                    polylineOptions!!.width(5.0f)
                    polylineOptions!!.startCap(SquareCap())
                    polylineOptions!!.endCap(SquareCap())
                    polylineOptions!!.jointType(JointType.ROUND)
                    polylineOptions!!.addAll(polylineList)
                    grayPolyline = mMap.addPolyline(polylineOptions)

                    blackPolylineOptions = PolylineOptions()
                    blackPolylineOptions!!.color(Color.BLACK)
                    blackPolylineOptions!!.width(5.0f)
                    blackPolylineOptions!!.startCap(SquareCap())
                    blackPolylineOptions!!.endCap(SquareCap())
                    blackPolylineOptions!!.jointType(JointType.ROUND)
                    blackPolylineOptions!!.addAll(polylineList)
                    blackPolyline = mMap.addPolyline(blackPolylineOptions)

                    //Animator
                    val polylineAnimation = ValueAnimator.ofInt(0,100)
                    polylineAnimation.setDuration(2000)
                    polylineAnimation.setInterpolator(LinearInterpolator())
                    polylineAnimation.addUpdateListener { valueAnimator ->
                        val points = grayPolyline!!.points
                        val percenValue = Integer.parseInt(valueAnimator.animatedValue.toString())
                        val size = points.size
                        val newPoints = (size *(percenValue / 100.0f).toInt())
                        val p = points.subList(0,newPoints)
                        blackPolyline!!.points = p
                    }
                    polylineAnimation.start()

                    //Car moving
                    index = -1
                    next = 1
                    val r = object :Runnable {
                        override fun run() {
                            if( index < polylineList.size -1)
                            {
                                index++
                                next = index + 1
                                startPosition = polylineList[index]
                                endPosition =  polylineList[next]
                            }

                            val valueAnimator = ValueAnimator.ofInt(0,1)
                            valueAnimator.setDuration(1500)
                            valueAnimator.setInterpolator(LinearInterpolator())
                            valueAnimator.addUpdateListener { valueAnimator ->
                                v = valueAnimator.animatedFraction
                                lat = v * endPosition!!.latitude + (1-v) * startPosition!!.latitude
                                lng = v * endPosition!!.longitude + (1-v) * startPosition!!.longitude

                                val newPos = LatLng(lat,lng)
                                shipperMarket!!.position = newPos
                                shipperMarket!!.setAnchor(0.5f,0.5f)
                                shipperMarket!!.rotation = Common.getBearing(startPosition!!,newPos)

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(shipperMarket.position))
                            }

                            valueAnimator.start()
                            if(index < polylineList.size - 2 )
                                handler!!.postDelayed(this, 1500)
                        }

                    }

                    handler = Handler()
                    handler!!.postDelayed( r , 1500)

                }catch (e: Exception){
                    Log.d("DEBUG",e.message.toString())
                }
            },{ throwable ->
                Toast.makeText(this@TrackingOrderActivity,"Loi"+throwable.message,Toast.LENGTH_SHORT).show()
                throwable.printStackTrace()
            }))
    }

    override fun onCancelled(p0: DatabaseError) {

    }
}