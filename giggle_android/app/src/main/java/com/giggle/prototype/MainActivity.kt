package com.giggle.prototype


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(MypageFragment())

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_current_loc -> {
                    // current_loc
                    // Toast.makeText(this, "currentLoc", Toast.LENGTH_LONG).show()
                    val currentLocFragment = currentLocFragment.newInstance()
                    loadFragment(currentLocFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_addAD -> {
                    // addAD
                    // Toast.makeText(this, "addAD", Toast.LENGTH_LONG).show()
                    val addADFragment = addADFragment.newInstance()
                    loadFragment(addADFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_myPage -> {
                    // myPage
                    // Toast.makeText(this, "myPage", Toast.LENGTH_LONG).show()
                    val mypageFragment = MypageFragment.newInstance()
                    loadFragment(mypageFragment)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        // mapFragment.getMapAsync(this)
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

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }


}

