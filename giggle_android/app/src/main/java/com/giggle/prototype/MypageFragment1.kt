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
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_mypage.*
import kotlinx.android.synthetic.main.fragment_mypage1.*
import kotlinx.android.synthetic.main.fragment_mypage1.bt_change
import kotlinx.android.synthetic.main.fragment_mypage1.btn_ing
import kotlinx.android.synthetic.main.fragment_mypage1.btn_saved_ad
import kotlinx.android.synthetic.main.fragment_mypage1.emailText
import kotlinx.android.synthetic.main.fragment_mypage1.imageview_main_image2
import kotlinx.android.synthetic.main.fragment_mypage1.img_user
import kotlinx.android.synthetic.main.fragment_mypage1.txshopname
import kotlinx.android.synthetic.main.fragment_mypage1.txshopposition

class MypageFragment1 : Fragment() {
    private val user = FirebaseAuth.getInstance().currentUser
    private val storage = FirebaseStorage.getInstance()
    val PICK_IMAGE_FROM_ALBUM = 0
    var photoUri6: Uri? =null //프로필 사진
    val db = FirebaseFirestore.getInstance()
    var shopname ="0"
    var shopphoto ="0"
    var shopposition="0"
    var i:Int=0
    private lateinit var mContext: FragmentActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mypage1, container, false)
    }

    companion object {
        fun newInstance(): MypageFragment1 = MypageFragment1()
    }

    // TODO: icon size on fragment_mypage.xml

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = user?.email
        val uid = user?.uid

        //최근 광고 카드뷰
        db.collection("jobads")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(uid==document.data["uid"].toString()&&document.data["state"].toString()=="0") {
                        if (i == 0) {
                            shopname = document.data["shopname"].toString() //가게이름
                            shopposition = document.data["shopposition"].toString()
                            shopphoto = document.data["photouri"].toString() //이미지
                            i = 1
                            Glide.with(this)
                                .load(shopphoto)
                                .centerCrop()
                                .into(imageview_main_image2)
                            txshopname.setText(shopname)
                            txshopposition.setText(shopposition)
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(mContext, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG).show()
            }

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
        btn_ing.setOnClickListener{  // 진행중인 알바
            // val nextIntent = Intent(activity, SignInActivity::class.java)
            // startActivity(nextIntent)
        }
        btn_saved_ad.setOnClickListener{  // 아르바이트 이력
            val nextIntent = Intent(activity, SavedAdActivity::class.java)
            startActivity(nextIntent)
        }
        btn_member.setOnClickListener{  // 구인 정보 등록
            val nextIntent = Intent(activity, MemberInfoActivity::class.java)
            startActivity(nextIntent)
        }
        bt_change.setOnClickListener{  // 구인자 전환
            val nextIntent = Intent(activity, SignInActivity::class.java)
            startActivity(nextIntent)
        }

    }
    fun currentUpload(){
        val uid = user?.uid
        val imageFileName = uid +".png"
        if(photoUri6!=null){
            val storageRef = storage?.reference?.child("profile_image/")?.child(imageFileName)
            storageRef?.putFile(photoUri6!!)?.addOnSuccessListener{taskSnapshot ->
            }
        }
    }
    override fun onActivityResult(requestCode:Int, resultCode: Int, data: Intent?){

        if(requestCode == PICK_IMAGE_FROM_ALBUM)  {
            if(resultCode == Activity.RESULT_OK){
                photoUri6 = data?.data
                img_user.setImageURI(data?.data)
                currentUpload()
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}
