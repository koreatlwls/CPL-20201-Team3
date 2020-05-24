package com.giggle.prototype

import android.app.Activity
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
import kotlinx.android.synthetic.main.fragment_mypage.*

class MypageFragment : Fragment() {
    private val user = FirebaseAuth.getInstance().currentUser
    private val storage = FirebaseStorage.getInstance()
    val PICK_IMAGE_FROM_ALBUM = 0
    var photoUri: Uri? =null //프로필 사진
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    companion object {
        fun newInstance(): MypageFragment = MypageFragment()
    }

    // TODO: icon size on fragment_mypage.xml

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        img_user.setOnClickListener{
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }

        // set fields
        emailText.text = email

        // button listeners
        bt_change.setOnClickListener{
            val nextIntent = Intent(activity, SignInActivity1::class.java)
            startActivity(nextIntent)
        }
        btn_ing.setOnClickListener{
            val nextIntent = Intent(activity, IngAdActivity::class.java)
            startActivity(nextIntent)
        }
        btn_saved_ad.setOnClickListener{
            val nextIntent = Intent(activity, CompleteAdActivity::class.java)
            startActivity(nextIntent)
        }
    }
    fun currentUpload(){
        val uid = user?.uid
        val imageFileName = uid +".png"
        if(photoUri!=null){
            val storageRef = storage?.reference?.child("profile_image/")?.child(imageFileName)
            storageRef?.putFile(photoUri!!)?.addOnSuccessListener{taskSnapshot ->
            }
        }
    }
    override fun onActivityResult(requestCode:Int, resultCode: Int, data: Intent?){

        if(requestCode == PICK_IMAGE_FROM_ALBUM)  {
            if(resultCode == Activity.RESULT_OK){
                println(data?.data)
                photoUri = data?.data
                img_user.setImageURI(data?.data)
                currentUpload()
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}


