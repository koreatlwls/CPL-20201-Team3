package com.giggle.prototype

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_mypage.*

class MypageFragment : Fragment() {
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    companion object {
        fun newInstance(): MypageFragment = MypageFragment()
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = user?.displayName
        val email = user?.email
        val photoUrl = user?.photoUrl
        // val emailVerified = user.isEmailVerified
        // val uid = user.uid

        nameText.text = name
        emailText.text = email
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}