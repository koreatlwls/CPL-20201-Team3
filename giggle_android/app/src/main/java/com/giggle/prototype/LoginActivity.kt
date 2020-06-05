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

        //로그인
        login.setOnClickListener {
            val emailStr: String = email.text.toString()
            val passwordStr: String = password.text.toString()

            if (emailStr.isEmpty()) {
                Toast.makeText(this,"이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            } else if (passwordStr.isEmpty()) {
                Toast.makeText(this,"비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            } else {
                auth?.signInWithEmailAndPassword(emailStr, passwordStr)?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val emailVerified = auth?.currentUser?.isEmailVerified
                        if (emailVerified == true){
                            val nextIntent = Intent(this, SignInActivity::class.java)
                            startActivity(nextIntent)
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
