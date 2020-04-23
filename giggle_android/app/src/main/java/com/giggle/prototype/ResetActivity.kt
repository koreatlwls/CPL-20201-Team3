package com.giggle.prototype

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_resetpassword.*

class ResetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpassword)

        sendBt.setOnClickListener{
            findPassword()
        }
    }
    fun findPassword(){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email.text.toString()).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this,"비밀번호 변경 메일을 전송했습니다", Toast.LENGTH_LONG).show()
                val nextIntent = Intent(this, LoginActivity::class.java)
                startActivity(nextIntent)
            }else{
                Toast.makeText(this,"등록되지 않은 이메일입니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
}