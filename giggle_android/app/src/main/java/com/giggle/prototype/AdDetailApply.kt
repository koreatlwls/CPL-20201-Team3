package com.giggle.prototype

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_ad_detail_apply.*
import kotlinx.android.synthetic.main.fragment_mypage.*

class AdDetailApply : AppCompatActivity(),OnMapReadyCallback {
    var touid =""
    var adtoken = ""
    var memname = ""
    var memphone = ""
    var memuid = ""
    var shopname = ""
    var shopposition = ""
    private var shopphoto=""
    private lateinit var mMap: GoogleMap
    val user = FirebaseAuth.getInstance().currentUser
    override fun onMapReady(googleMap: GoogleMap) {
        mMap=googleMap
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_detail_apply)
        val mapFragment=supportFragmentManager.findFragmentById(R.id.adMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val db = FirebaseFirestore.getInstance()
        if (intent.hasExtra("name")){
            shopname = intent.getStringExtra("name")
        }

        db.collection("jobads")
            .whereEqualTo("shopname", shopname)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //DB읽은 데이터 출력
                    touid = document.data["uid"].toString()
                    txname.setText(document.data["shopname"].toString())
                    txposition.setText(document.data["shopposition"].toString())
                    txinfo.setText(document.data["businessinfo"].toString())
                    txnum.setText(document.data["numofperson"].toString())
                    txpay.setText(document.data["hourlypay"].toString())
                    txtime.setText(document.data["st"].toString()+"~"+document.data["fn"].toString())
                    shopphoto=document.data["photouri"].toString()
                    val latitude=document.data["latitude"] as Double
                    val longtitude=document.data["longtitude"] as Double
                    val shoplatlng=LatLng(latitude,longtitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shoplatlng,15f))
                    mMap.addMarker(MarkerOptions().position(shoplatlng).title(txname.text.toString()))
                    val age_1 = Integer.parseInt(document.data["age1"].toString())
                    val age_2 = Integer.parseInt(document.data["age2"].toString())
                    if(shopphoto!="null") {
                        Glide.with(this)
                            .load(shopphoto)
                            .centerCrop()
                            .into(shopimageview)
                    }
                    //출력값 검사
                    if(document.data["sex"].toString().isEmpty()){
                        txsex.setText("무관")
                    }
                    else{
                        txsex.setText(document.data["sex"].toString())
                    }
                    if(age_1==0&&age_2==0){
                        txage.setText("무관")
                    }
                    else if(age_1==0&&age_2>0){
                        txage.setText(document.data["age2"].toString() + "이하")
                    }
                    else if(age_1>0&&age_2==0){
                        txage.setText(document.data["age1"].toString() + "이상")
                    }
                    else{
                        txage.setText(document.data["age1"].toString() + "~" + document.data["age2"].toString())
                    }
                    if(document.data["priorityreq"].toString().isEmpty()){
                        txpriority.setText("무관")
                    }
                    else{
                        txpriority.setText(document.data["priorityreq"].toString())
                    }
                }
            }


        btApply.setOnClickListener{
            val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.Theme_AppCompat_Light_Dialog))
            builder.setTitle("지원")
            builder.setMessage("지원하시겠습니까?")
            builder.setPositiveButton("확인") { _, _ ->
                memuid = user?.uid.toString()
                db.collection("pushtokens").document(touid)
                    .get()
                    .addOnSuccessListener {
                        adtoken = it["pushtoken"].toString()
                    }
                db.collection("members")
                    .whereEqualTo("uid",memuid)
                    .get()
                    .addOnSuccessListener {
                        if(it.isEmpty){
                            Toast.makeText(this,"정보를 등록해주십시요.", Toast.LENGTH_LONG).show()
                            val nextIntent = Intent(this,MemberInfoActivity::class.java)
                            startActivity(nextIntent)
                        }
                        else{
                            for(document in it){
                                memname = document["name"].toString()
                                memphone = document["phonenumber"].toString()
                                shopposition = txposition.text.toString()
                                var map = mutableMapOf<String,Any>()
                                map["applicants"] = memuid
                                db.collection("jobads").document(shopname).update("applicants",
                                    FieldValue.arrayUnion(memuid)).addOnCompleteListener{
                                    if(it.isSuccessful){

                                    }
                                }
                                SendNotification.sendNotification(adtoken,memname,memphone,shopname,shopposition)
                            }
                        }
                    }
            }
            builder.setNegativeButton("취소"){_,_->

            }
            builder.show()

        }
    }
}
