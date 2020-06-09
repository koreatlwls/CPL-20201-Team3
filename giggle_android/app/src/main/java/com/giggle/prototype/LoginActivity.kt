package com.giggle.prototype

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
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

        val load=getSharedPreferences("auto",Activity.MODE_PRIVATE)
        val id=load.getString("InputID",null)
        val pw=load.getString("InputPW",null)
        val mode=load.getString("UserMode","0")
        if(id!=null&&pw!=null) {
            email.setText(id)
            password.setText(pw)
            autoLogin.isChecked = true
            auth?.signInWithEmailAndPassword(id, pw)?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val emailVerified = auth?.currentUser?.isEmailVerified
                    if (emailVerified == true) {
                        if(mode=="0") {
                            val nextIntent = Intent(this, SignInActivity::class.java)
                            startActivity(nextIntent)
                        }
                        else if(mode=="1"){
                            val nextIntent=Intent(this,SignInActivity1::class.java)
                            startActivity(nextIntent)
                        }
                    } else {
                        Toast.makeText(this, "이메일 인증을 완료해주세요.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "이메일 혹은 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        //로그인
        login.setOnClickListener {
            val emailStr: String = email.text.toString()
            val passwordStr: String = password.text.toString()
            if (emailStr.isEmpty()) {
                Toast.makeText(this,"이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            } else if (passwordStr.isEmpty()) {
                Toast.makeText(this,"비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            } else {
                if(autoLogin.isChecked){
                    val auto=getSharedPreferences("auto",Activity.MODE_PRIVATE)
                    val autologin:SharedPreferences.Editor=auto.edit()
                    autologin.putString("InputID",emailStr.toString())
                    autologin.putString("InputPW",passwordStr.toString())
                    autologin.apply()
                }
                auth?.signInWithEmailAndPassword(emailStr, passwordStr)?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val emailVerified = auth?.currentUser?.isEmailVerified
                        if (emailVerified == true){
                            if(mode=="0") {
                                val nextIntent = Intent(this, SignInActivity::class.java)
                                startActivity(nextIntent)
                            }
                            else if(mode=="1"){
                                val nextIntent=Intent(this,SignInActivity1::class.java)
                                startActivity(nextIntent)
                            }
                        } else {
                            Toast.makeText(this, "이메일 인증을 완료해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "이메일 혹은 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        //회원가입
        signup.setOnClickListener {
            val nextIntent = Intent(this, SignUpActivity1::class.java)
            startActivity(nextIntent)
        }
        //비밀번호 재설정
        resetpassword.setOnClickListener {
            val nextIntent = Intent(this, ResetActivity::class.java)
            startActivity(nextIntent)
        }
    }
}
