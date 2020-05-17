package com.giggle.prototype

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class ModifyUserBeforeActivity : AppCompatActivity() {
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_user_before)

        // TODO: add handler, value check, intent to ModifyUserActivity
    }
}
