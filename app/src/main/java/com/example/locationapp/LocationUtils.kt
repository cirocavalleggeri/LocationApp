package com.example.locationapp
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale


class LocationUtils(val context:Context) {
    private val  _fuseLocationClient: FusedLocationProviderClient=
        LocationServices.getFusedLocationProviderClient(context)
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(viewModel: LocationViewModel){
        val locationCallBack= object :LocationCallback(){
            override fun onLocationResult(locationResult:  LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    val location=LocationData(it.latitude,it.longitude)
                    viewModel.updateLocation(location)
                }
            }
        }
        val locationRequest=LocationRequest.
                                           Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).
                                           build()
        _fuseLocationClient.requestLocationUpdates(
                            locationRequest,locationCallBack,Looper.getMainLooper())
    }
    fun hasLocationPermission(context: Context):Boolean{
        return (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)==
                        PackageManager.PERMISSION_GRANTED)

    }
    fun reverseGeoCodeLocation(Location:LocationData):String{
         val geocoder=Geocoder(context, Locale.getDefault())
         val coordinate=LatLng(Location.latitude,Location.longitude)
         val addresses: MutableList<Address>? = geocoder.
                                              getFromLocation(coordinate.latitude,
                                                  coordinate.longitude,1)
        return if(addresses?.isNotEmpty()==true){
            addresses[0].getAddressLine(0)
        }else{

            "Indirizzo non è stato trovato"
        }

    }
}
