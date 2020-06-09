package com.giggle.prototype

import android.app.Activity
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class SignInActivity1 : AppCompatActivity() {

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container1, fragment).addToBackStack(null).commit()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.menu_current_loc -> {
                val currentLocSeekerfragment=CurLocSeekerFragment()
                loadFragment(currentLocSeekerfragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_listAD -> {
                // addAD
                // Toast.makeText(this, "addAD", Toast.LENGTH_LONG).show()
                val listADFragment = ListADFragment.newInstance()
                loadFragment(listADFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_myPage -> {
                // myPage
                // Toast.makeText(this, "myPage", Toast.LENGTH_LONG).show()
                val mypageFragment1 = MypageFragment1.newInstance()
                loadFragment(mypageFragment1)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)

        val currentLocFragmentSeeker = CurLocSeekerFragment()
        loadFragment(currentLocFragmentSeeker)

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_nav1)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val auto=getSharedPreferences("auto", Activity.MODE_PRIVATE)
        val autologin: SharedPreferences.Editor=auto.edit()
        autologin.putString("UserMode","1")
        autologin.apply()
    }



    override fun onResume() {//잠깐 쉴 때
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

}

