package com.giggle.prototype

import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import java.util.*


class SignInActivity : AppCompatActivity() {

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }
    private fun registerPushToken() {
        var pushToken: String?
        var uid = FirebaseAuth.getInstance().currentUser!!.uid
        var map = mutableMapOf<String, Any>()
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            pushToken = instanceIdResult.token
            map["pushtoken"] = pushToken!!
            FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
        }
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

        //접속시 서버에게 푸쉬토큰 등록
        registerPushToken()
    }

    override fun onResume() {//잠깐 쉴 때
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
    }
}

