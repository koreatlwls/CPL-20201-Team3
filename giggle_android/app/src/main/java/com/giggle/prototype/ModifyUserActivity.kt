package com.giggle.prototype

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_modify_user.*

class ModifyUserActivity : AppCompatActivity() {
    private val user = FirebaseAuth.getInstance().currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_user)

        // Toast.makeText(this, "toast", Toast.LENGTH_LONG).show()

        // get user's info
        val name = user?.displayName
        val email = user?.email

        // TODO: see docs/auth/android/manage-users, split into two parts, construct linearlayout

        // initialize fields
        NameText.setText(name)
        EmailText.setText(email)
        // PasswordText.setText("")



        ModifyUserOK.setOnClickListener {
            // TODO: work on updateProfile, updateEmail, updatePassword
            // get fields
            val nameInput = NameText.text.toString()
            val emailInput = EmailText.text.toString()
            val passwordInput = PasswordText.text.toString()


            // updateProfile - name, photo
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                // .setPhotoUri(Uri.parse("https://example.com/"))
                .build()
            user?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this, "updateProfile에서 문제가 발생했습니다.", Toast.LENGTH_LONG).show()
                    }
                }

            // updateEmail
            user?.updateEmail(emailInput)
                ?.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this, "updateEmail에서 문제가 발생했습니다.", Toast.LENGTH_LONG).show()
                    }
                }

            // if password is not empty, update password too
            if (passwordInput !== "") {
                user?.updatePassword(passwordInput)
                    ?.addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Toast.makeText(this, "updatePassword에서 문제가 발생했습니다.", Toast.LENGTH_LONG).show()
                        }
                    }
            }

            // completed
            // Toast.makeText(this, "회원정보 수정이 완료되었습니다.", Toast.LENGTH_LONG).show()
        }


    }

}
