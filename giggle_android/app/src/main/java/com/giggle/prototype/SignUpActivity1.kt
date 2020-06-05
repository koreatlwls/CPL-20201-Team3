package com.giggle.prototype

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_signup_1.*

class SignUpActivity1 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_1)
        signup_cancel.setOnClickListener{
            val nextIntent = Intent(this, LoginActivity::class.java)
            startActivity(nextIntent)
        }
        condition_consent.setOnClickListener{
            val nextIntent = Intent(this, SignUpActivity2::class.java)
            startActivity(nextIntent)
        }
    }
}