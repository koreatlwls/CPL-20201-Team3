package com.giggle.prototype

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_modify_user_before.*

class ModifyUserBeforeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_user_before)

        auth = FirebaseAuth.getInstance()

        ModifyUserBeforeOK.setOnClickListener {
            val email = user?.email.toString()
            val passwordInput = ModifyUserBeforePassword.text.toString()

            // try login with user's email, given password
            auth.signInWithEmailAndPassword(email, passwordInput).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    // Toast.makeText(this,"Success", Toast.LENGTH_LONG).show()
                    val nextIntent = Intent(this, ModifyUserActivity::class.java)
                    startActivity(nextIntent)
                } else {
                    // Sign in failed
                    Toast.makeText(this,"비밀번호가 다릅니다.", Toast.LENGTH_LONG).show()
                }
            }

        }
    }
}
