package com.giggle.prototype

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class SignInActivity1 : AppCompatActivity() {

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

        val currentLocFragment = CurrentLocFragment.newInstance()
        loadFragment(currentLocFragment)

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_nav1)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }


    /*
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    // 권한이 승인 됐다면
                    addLocationListener()
                } else {
                    // 권한이 거부 됐다면
                    toast("권한 거부 됨")
                }
                return
            }
        }
    }

*/
    override fun onResume() {//잠깐 쉴 때
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
/*
    fun removeLocationListener() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }//어플이 종료되면 지도 요청 해제

    @SuppressLint("MissingPermission")
    //위험 권한 사용 시 요청 코드가 호출 되어야 하는데
    //없어서 발생됨. 요청 코드는 따로 처리 했음
    fun addLocationListener() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback, null
        ) //위치 권한을 요청해야 함.
        //액티비티가 잠깐 쉴 때
        //자신의 위치를 확인하고 갱신된 정보를 요청

    }
 */
}

