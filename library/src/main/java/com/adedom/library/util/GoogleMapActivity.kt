package com.adedom.library.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.adedom.library.R
import com.adedom.library.extension.dialogNegative
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

abstract class GoogleMapActivity(
    private val mapFragment: Int,
    private val interval: Long
) : PathiphonActivity(),
    OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mHandler: Handler
    private var isCamera: Boolean = true

    companion object {
        lateinit var sContext: Context
        lateinit var sActivity: Activity
        var sGoogleMap: GoogleMap? = null
        var sLatLng = LatLng(13.5238, 100.7519)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        sContext = baseContext
        sActivity = this

        mHandler = Handler()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()

        return super.onOptionsItemSelected(item)
    }

    private fun setMapAndLocation() {
        val mapFragment = fragmentManager.findFragmentById(mapFragment) as MapFragment
        mapFragment.getMapAsync(this@GoogleMapActivity)

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
        mGoogleApiClient.connect()

        mLocationRequest = LocationRequest()
            .setInterval(interval)
            .setFastestInterval(interval)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleApiClient,
            mLocationRequest,
            this
        )
    }

    private fun stopLocationUpdate() =
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)

    override fun onStart() {
        super.onStart()

        setMapAndLocation()

        mGoogleApiClient.connect()
    }

    override fun onStop() {
        if (mGoogleApiClient.isConnected) mGoogleApiClient.disconnect()

        super.onStop()
    }

    override fun onResume() {
        super.onResume()

        mRunnable.run()

        if (mGoogleApiClient.isConnected) startLocationUpdate()
    }

    override fun onPause() {
        super.onPause()

        mHandler.removeCallbacks(mRunnable)

        if (mGoogleApiClient.isConnected) stopLocationUpdate()
    }

    override fun onConnected(p0: Bundle?) = startLocationUpdate()

    override fun onConnectionSuspended(p0: Int) = mGoogleApiClient.connect()

    override fun onConnectionFailed(p0: ConnectionResult) {}

    override fun onLocationChanged(location: Location?) {
        if (sLatLng.latitude == location!!.latitude &&
            sLatLng.longitude == location.longitude
        ) return

        sLatLng = LatLng(location.latitude, location.longitude)

        if (isCamera) {
            isCamera = false
            setCamera(15F, 12F)
        }
    }

    fun setCamera(zoom: Float, minZoom: Float = 1F, maxZoom: Float = 20F) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(sLatLng, zoom)
        sGoogleMap!!.animateCamera(cameraUpdate)
        sGoogleMap!!.setMinZoomPreference(minZoom)
        sGoogleMap!!.setMaxZoomPreference(maxZoom)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        sGoogleMap = googleMap
        sGoogleMap!!.isMyLocationEnabled = true
    }

    override fun onBackPressed() {
        mHandler.removeCallbacks(mRunnable)
        AlertDialog.Builder(this@GoogleMapActivity).dialogNegative(R.string.exit) { finish() }
    }

    open fun onActivityRunning() {}

    private val mRunnable = object : Runnable {
        override fun run() {
            onActivityRunning()
            mHandler.postDelayed(this, 1000)
        }
    }
}
