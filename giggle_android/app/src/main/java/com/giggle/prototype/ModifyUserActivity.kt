package com.giggle.prototype

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
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
        val photoUrl = user?.photoUrl

        // TODO: see docs/auth/web/manage-users, split into two parts, construct linearlayout

        // initialize fields
        NameText.setText(name)
        EmailText.setText(name)
        // PasswordText.setText("")



        ModifyUserOK.setOnClickListener {
            // TODO: work on updateProfile, updateEmail, updatePassword


            // if password is not empty, update password too


            // Toast.makeText(this, "회원정보 수정이 완료되었습니다.", Toast.LENGTH_LONG).show()
        }


    }

}
