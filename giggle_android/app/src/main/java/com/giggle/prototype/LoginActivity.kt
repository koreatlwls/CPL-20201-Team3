package com.giggle.prototype

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var auth : FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        login.setOnClickListener {
            if(email.text.toString().isEmpty() || password.text.toString().isEmpty()){
                Toast.makeText(this,"email 혹은 password를 반드시 입력하세요.",Toast.LENGTH_SHORT).show()
            }else{
                auth?.signInWithEmailAndPassword(email.text.toString(),password.text.toString())?.addOnCompleteListener(this) { task ->
                    if(task.isSuccessful){
                        val nextIntent = Intent(this, SignInActivity::class.java)
                        startActivity(nextIntent)
                    }else{
                        Toast.makeText(this, "email 혹은 password를 확인 하십시요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        signup.setOnClickListener {
            val nextIntent = Intent(this, SignUpActivity1::class.java)
            startActivity(nextIntent)
        }
        password_reset.setOnClickListener { reset_password() }

    }
    fun reset_password() {

    }
}
