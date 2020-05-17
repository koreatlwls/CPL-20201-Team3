package com.giggle.prototype

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_mypage.*

class MypageFragment : Fragment() {
    private val user = FirebaseAuth.getInstance().currentUser
    private val storage = FirebaseStorage.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    companion object {
        fun newInstance(): MypageFragment = MypageFragment()
    }

    // TODO: icon size on fragment_mypage.xml

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = user?.displayName
        val email = user?.email
        val uid = user?.uid

        // get profile pic
        var storageRef = storage.reference
        var imageRef = storageRef.child("profile_image/${uid}.png")
        imageRef.downloadUrl.addOnSuccessListener {
            // Got the download URL for 'users/me/profile.png'
            Glide.with(this)
                .load(it)
                .centerCrop()
                .into(img_user)
        }.addOnFailureListener {
            Toast.makeText(activity, "프로필 사진이 없습니다.", Toast.LENGTH_LONG).show()
        }

        // set fields
        nameText.text = name
        emailText.text = email

        // button listeners
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