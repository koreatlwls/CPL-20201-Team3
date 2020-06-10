package com.giggle.prototype

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_mypage.*

class MypageFragment : Fragment() {
    private val user = FirebaseAuth.getInstance().currentUser
    private val storage = FirebaseStorage.getInstance()
    val PICK_IMAGE_FROM_ALBUM = 0
    var photoUri5: Uri? =null //프로필 사진
    var shopname ="0"
    var shopphoto ="0"
    var shopposition="0"
    val db = FirebaseFirestore.getInstance()
    var i:Int=0
    private lateinit var mContext:FragmentActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    companion object {
        fun newInstance(): MypageFragment = MypageFragment()
    }

    // TODO: icon size on fragment_mypage.xml
    override fun onAttach(activity: Activity) {
        mContext = activity as FragmentActivity
        super.onAttach(activity)
    }
    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = user?.email
        val uid = user?.uid
        var storageRef = storage.reference
/*
        db.collection("members").whereEqualTo("uid",uid).get().addOnSuccessListener {
            result->
            for(document in result){
                val score=document.data["score"] as Double
                var numofscore=document.data["num_of_score"] as Double
                val member=members(score,numofscore)
                var Score_result=member.calScore()
                Rating_Bar.numStars=Score_result as Int
                txscore.setText(Score_result.toString())
            }
        }
*/
        db.collection("members").whereEqualTo("uid",uid).get()
            .addOnSuccessListener { result-> for(document in result) {
                nameview.setText(document.data["name"].toString())
                }
            }
        //최근 광고 카드뷰
        db.collection("jobads")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(uid==document.data["uid"].toString()&&document.data["state"].toString()=="0") {
                            shopname = document.data["shopname"].toString() //가게이름
                            shopposition = document.data["shopposition"].toString()
                            shopphoto = document.data["photouri"].toString() //이미지
                            if(shopphoto!="null") {
                                Glide.with(this)
                                    .load(shopphoto)
                                    .centerCrop()
                                    .into(imageview_main_image2)
                            }
                        txshopname.setText(shopname)
                        txshopposition.setText(shopposition)
                        break
                        }
                    }
            }.addOnFailureListener { exception ->
                Toast.makeText(mContext, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG).show()
            }

        // get profile pic
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
        imageview_main_image2.setOnClickListener{
            val nextIntent=Intent(activity,IngAd_DetailActivity::class.java)
            nextIntent.putExtra("name",txshopname.text.toString())
            startActivity(nextIntent)
        }
    }
    fun currentUpload(){
        val uid = user?.uid
        val imageFileName = uid +".png"
        if(photoUri5!=null){
            val storageRef = storage?.reference?.child("profile_image/")?.child(imageFileName)
            storageRef?.putFile(photoUri5!!)?.addOnSuccessListener{taskSnapshot ->
            }
        }
    }
    override fun onActivityResult(requestCode:Int, resultCode: Int, data: Intent?){
        if(requestCode == PICK_IMAGE_FROM_ALBUM)  {
            if(resultCode == Activity.RESULT_OK){
                println(data?.data)
                photoUri5 = data?.data
                img_user.setImageURI(data?.data)
                currentUpload()
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}



