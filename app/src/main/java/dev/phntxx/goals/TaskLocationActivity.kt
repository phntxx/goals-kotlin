package dev.phntxx.goals

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.phntxx.goals.databinding.ActivityTaskLocationBinding

class TaskLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private var userLocation: Location? = null
    private var markerLocation: LatLng = LatLng(0.0, 0.0)
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityTaskLocationBinding
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding = ActivityTaskLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapFragment = binding.map.getFragment()
        mapFragment.getMapAsync(this)

        binding.saveButton.setOnClickListener {
            val intent = Intent()
                .putExtra("latitude", markerLocation.latitude)
                .putExtra("longitude", markerLocation.longitude)

            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    public override fun onPause() {
        super.onPause()

        this.fusedLocationClient?.removeLocationUpdates(object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val locationList = locationResult.locations
                if (locationList.isNotEmpty()) {

                    userLocation = locationList.last()

                    val latLng = LatLng(userLocation?.latitude ?: 0.0, userLocation?.longitude ?: 0.0)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.0F))
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        var latLng = LatLng(0.0, 0.0)

        if (intent.hasExtra("location")) {
            val location = intent.getDoubleArrayExtra("location")
            latLng = LatLng(location!![0], location[1])
        } else if (this.userLocation != null) {
            latLng = LatLng(this.userLocation!!.latitude, this.userLocation!!.longitude)
        }

        val marker = googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )

        if (granted) {
            googleMap.isMyLocationEnabled = true
            googleMap.isMyLocationEnabled = true
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 1000, null)
            googleMap.setOnCameraMoveListener {
                if (marker != null) {
                    this.markerLocation = googleMap.cameraPosition.target
                    marker.position = this.markerLocation
                }
            }
        } else {
            checkLocationPermission()
        }
    }

    private fun checkLocationPermission() {
        val denied = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val dialog = AlertDialog.Builder(this)
            .setTitle("Location Permission Needed")
            .setMessage("This app needs the Location permission, please accept to use location functionality")
            .setPositiveButton("OK") { _, _ ->

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE
                )
            }
            .create()

        if (denied && showRationale) {
            dialog.show()
        } else if (denied && !showRationale) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    val granted = ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    this.googleMap.isMyLocationEnabled = granted

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 99
    }
}