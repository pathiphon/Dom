package com.adedom.library.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.widget.TextView
import android.widget.Toast
import com.adedom.library.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.model.LatLng

//context.getLocality(latLng)
fun Context.getLocality(latLng: LatLng): String {
    val list = Geocoder(this).getFromLocation(latLng.latitude, latLng.longitude, 1)
    return if (list[0].locality != null) list[0].locality else getString(R.string.unknown)
}

//context.getLocality(latitude, longitude)
fun Context.getLocality(latitude: Double, longitude: Double): String {
    val list = Geocoder(this).getFromLocation(latitude, longitude, 1)
    return if (list[0].locality != null) list[0].locality else getString(R.string.unknown)
}

//baseContext.setLocality(mTvLocality, sLatLng)
fun Context.setLocality(textView: TextView, latLng: LatLng) {
    textView.text = this.getLocality(latLng)
}

//baseContext.setLocality(mTvLocality, latitude, longitude)
fun Context.setLocality(textView: TextView, latitude: Double, longitude: Double) {
    textView.text = this.getLocality(LatLng(latitude, longitude))
}

//baseContext.toast("text")
fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

//baseContext.toast("text")
fun Context.toast(message: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

//baseContext.failed()
fun Context.failed() = Toast.makeText(this, R.string.failed, Toast.LENGTH_LONG).show()

//baseContext.completed()
fun Context.completed() = Toast.makeText(this, R.string.completed, Toast.LENGTH_SHORT).show()

const val PREF_FILE = "PREF_FILE"
//setPrefFile(PLAYER_ID, playerId)
fun Context.writePrefFile(key: String, values: String) {
    this.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit()
        .putString(key, values)
        .apply()
}

//this.getPrefFile(USERNAME)
fun Context.readPrefFile(key: String): String {
    val preferences = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
    return preferences.getString(key, "")!!
}

//context.imageMarker(R.drawable.ic_egg)
fun Context.resourceBitmap(image: Int): Bitmap {
    return BitmapFactory.decodeResource(this.resources, image)
}

//context.loadBitmap(url, {onResourceReady}, {onLoadCleared})
fun Context.loadBitmap(
    url: String,
    onResourceReady: (Bitmap) -> Unit,
    onLoadCleared: (() -> Unit)? = null
) {
    Glide.with(this)
        .asBitmap()
        .load(url)
        .circleCrop()
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                onResourceReady.invoke(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                onLoadCleared?.invoke()
            }
        })
}
