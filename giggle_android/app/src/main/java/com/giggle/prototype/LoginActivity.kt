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
            if(email.text.toString().isEmpty() || password.text.toString().isEmpty()){
                Toast.makeText(this,"email 혹은 password를 반드시 입력하세요.",Toast.LENGTH_SHORT).show()
            } else{
                auth?.signInWithEmailAndPassword(email.text.toString(),password.text.toString())?.addOnCompleteListener(this) { task ->
                    if(task.isSuccessful){
                        var useremailveri =auth?.currentUser?.isEmailVerified
                        if(useremailveri == true){
                            val nextIntent = Intent(this, SignInActivity::class.java)
                            startActivity(nextIntent)
                        }else {
                            Toast.makeText(this, "이메일 인증을 완료해주세요", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, "email 혹은 password를 확인 하십시요.", Toast.LENGTH_SHORT).show()
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
