package com.giggle.prototype

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signup_2.*

class SignUpActivity2 : AppCompatActivity() {

    var auth : FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_2)

        auth = FirebaseAuth.getInstance()

        signup_button.setOnClickListener {
            if(email_edittext.text.toString().isEmpty() || password_edittext1.text.toString()
                    .isEmpty() || password_edittext2.text.toString().isEmpty()
            ){
                Toast.makeText(this, "모두 채워주십시요.", Toast.LENGTH_SHORT).show()
            } else if(password_edittext1.text.toString().length<8){
                Toast.makeText(this, "비밀번호의 최소길이는 8글자입니다.", Toast.LENGTH_SHORT).show()
            } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email_edittext.text.toString()).matches()){
                Toast.makeText(this, "이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show()
            }else{
                if(password_edittext1.text.toString()==password_edittext2.text.toString()){
                    auth?.createUserWithEmailAndPassword(email_edittext.text.toString(),password_edittext1.text.toString())?.addOnCompleteListener(this) { task ->
                        if(task.isSuccessful) {
                            Toast.makeText(this, "생성완료.", Toast.LENGTH_SHORT).show()
                            var user = auth?.currentUser
                            user?.sendEmailVerification()?.addOnCompleteListener(OnCompleteListener { task ->
                                if(task.isSuccessful){
                                    Toast.makeText(this, "인증 메일을 보냈습니다. 이메일을 확인해주세요.", Toast.LENGTH_LONG).show()
                                    val nextIntent = Intent(this, LoginActivity::class.java)
                                    startActivity(nextIntent)
                                }else{
                                    Toast.makeText(this, "실패하였습니다.", Toast.LENGTH_LONG).show()
                                }
                            })
                        }else{
                            Toast.makeText(this, "이미 등록된 회원입니다.", Toast.LENGTH_SHORT).show()
                            email_edittext.text = null
                            password_edittext1.text = null
                            password_edittext2.text = null
                        }
                    }
                } else{
                    Toast.makeText(this, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    password_edittext1.text = null
                    password_edittext2.text = null
                }
            }

        }
    }
}