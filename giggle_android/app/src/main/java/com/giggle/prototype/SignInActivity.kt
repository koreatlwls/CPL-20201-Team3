package com.giggle.prototype

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.NavigationMenu
import java.io.IOException
import com.google.firebase.auth.FirebaseAuth


class SignInActivity : AppCompatActivity() {

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.menu_current_loc -> {
                // current_loc
                // Toast.makeText(this, "currentLoc", Toast.LENGTH_LONG).show()
                val currentLocFragment = CurrentLocFragment.newInstance()
                loadFragment(currentLocFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_addAD -> {
                // addAD
                // Toast.makeText(this, "addAD", Toast.LENGTH_LONG).show()
                val addADFragment = AddADFragment.newInstance()
                loadFragment(addADFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_myPage -> {
                // myPage
                // Toast.makeText(this, "myPage", Toast.LENGTH_LONG).show()
                val mypageFragment = MypageFragment.newInstance()
                loadFragment(mypageFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentLocFragment = CurrentLocFragment.newInstance()
        loadFragment(currentLocFragment)

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
    override fun onResume() {//잠깐 쉴 때
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
    }
}

