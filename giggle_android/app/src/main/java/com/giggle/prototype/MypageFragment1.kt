package com.giggle.prototype

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_mypage.*

class MypageFragment1 : Fragment() {
    private val user = FirebaseAuth.getInstance().currentUser
    private val storage = FirebaseStorage.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mypage1, container, false)
    }

    companion object {
        fun newInstance(): MypageFragment1 = MypageFragment1()
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
        bt_change.setOnClickListener{
            val nextIntent = Intent(activity, SignInActivity::class.java)
            startActivity(nextIntent)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}