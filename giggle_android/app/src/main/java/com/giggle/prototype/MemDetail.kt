package com.giggle.prototype

import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_mypage.*
import kotlinx.android.synthetic.main.memdetail.*


class MemDetail : AppCompatActivity(),OnMapReadyCallback{
    var mname =""
    var mage =0
    var msex=""
    var mphonenumber=""
    var mposition=""
    var touid=""
    var shopname = ""
    var shopposition = ""
    val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private lateinit var mMap: GoogleMap
    override fun onMapReady(googleMap: GoogleMap) {
        mMap=googleMap
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.memdetail)
        val mapFragment=supportFragmentManager.findFragmentById(R.id.memdetailMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        var memname = ""
        var storageRef = storage.reference
        val db = FirebaseFirestore.getInstance()



        if(intent.hasExtra("name")){
            memname = intent.getStringExtra("name")
            shopname = intent.getStringExtra("shopname")
            shopposition = intent.getStringExtra("shopposition")
        }
        db.collection("members")
            .whereEqualTo("name",memname)
            .get()
            .addOnSuccessListener { result->
                for(document in result){
                    txname.text = document.data["name"].toString()
                    txage.text = document.data["age"].toString()
                    txposition.text = document.data["position"].toString()
                    txsex.text = document.data["sex"].toString()
                    phone.text = document.data["phonenumber"].toString()
                    touid = document.data["uid"].toString()
                    val latitude=document.data["latitude"] as Double
                    val longtitude=document.data["longtitude"] as Double
                    mMap.addMarker(MarkerOptions().position(LatLng(latitude,longtitude)).title(memname))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude,longtitude),14f))
                    val uid=document.data["uid"].toString()
                    var imageRef=storageRef.child("profile_image/${uid}.png")
                    imageRef.downloadUrl.addOnSuccessListener {
                        // Got the download URL for 'users/me/profile.png'
                        Glide.with(this)
                            .load(it)
                            .centerCrop()
                            .into(profileimg)
                    }.addOnFailureListener {
                        Toast.makeText(this, "프로필 사진이 없습니다.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        btrecruit.setOnClickListener(){
            val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.Theme_AppCompat_Light_Dialog))
            builder.setTitle("채용")
            builder.setMessage("채용하시겠습니까??")
            builder.setPositiveButton("확인") { _, _ ->
                val db = FirebaseFirestore.getInstance()
                mname= txname.text.toString()
                mage= Integer.parseInt(txage.text.toString())
                msex= txsex.text.toString()
                mphonenumber= phone.text.toString()
                mposition= txposition.text.toString()
                mage = Integer.parseInt(txage.text.toString())
                db.collection("jobads").document(shopname).update("parttimer",FieldValue.arrayUnion(touid)).addOnCompleteListener{
                    if(it.isSuccessful){
                    }
                }
                val title = "채용"
                val message = shopname + "에 채용되었습니다."
                var token =""
                db.collection("pushtokens").document(touid).get().addOnSuccessListener { result->
                    token = result.data?.get("pushtoken").toString()
                    SendNotification.sendNotification(token,title,message,shopname,shopposition)
                 }
                db.collection("jobads").document(shopname).update("recruitnum", FieldValue.increment(1))
                db.collection("jobads").document(shopname).get().addOnSuccessListener { result->
                    if(Integer.parseInt(result.data?.get("recruitnum").toString())==Integer.parseInt(result.data?.get("numofperson").toString())){
                        db.collection("jobads").document(shopname).update("state", FieldValue.increment(1))
                    }
                }
             }
            builder.setNegativeButton("취소"){_,_->

            }
            builder.show()
        }
    }
}


