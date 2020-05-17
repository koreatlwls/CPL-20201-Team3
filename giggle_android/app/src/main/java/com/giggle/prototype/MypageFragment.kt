package com.giggle.prototype

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        btn_parttime.setOnClickListener {
            // Toast.makeText(activity,"아르바이트 신청", Toast.LENGTH_LONG).show()

        }

        btn_saved_parttime.setOnClickListener {
            // Toast.makeText(activity,"아르바이트 내역", Toast.LENGTH_LONG).show()

        }

        btn_saved_AD.setOnClickListener {
            // Toast.makeText(activity,"채용 내역", Toast.LENGTH_LONG).show()

        }

        btn_request.setOnClickListener {
            // Toast.makeText(activity,"통합회원 신청", Toast.LENGTH_LONG).show()

        }

        btn_ing.setOnClickListener {
            // Toast.makeText(activity,"진행중인 알바", Toast.LENGTH_LONG).show()

        }

        btn_modify.setOnClickListener {
            // Toast.makeText(activity,"회원정보 수정", Toast.LENGTH_LONG).show()
            val nextIntent = Intent(activity, ModifyUserBeforeActivity::class.java)
            startActivity(nextIntent)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}