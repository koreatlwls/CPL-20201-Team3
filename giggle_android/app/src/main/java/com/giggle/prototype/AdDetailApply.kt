package com.giggle.prototype

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_ad_detail_apply.*

class AdDetailApply : AppCompatActivity() {
    var touid =""
    var adtoken = ""
    var memname = ""
    var memphone = ""
    var memuid = ""
    var shopname = ""
    var shopposition = ""
    val user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_detail_apply)
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
                    txname.text = document.data["shopname"].toString()
                    txposition.text = document.data["shopposition"].toString()
                    txinfo.text = document.data["businessinfo"].toString()
                    txnum.text = document.data["numofperson"].toString()
                    txpay.text = document.data["hourlypay"].toString()
                    txtime.text = document.data["st"].toString()+"~"+document.data["fn"].toString()

                    val age_1 = Integer.parseInt(document.data["age1"].toString())
                    val age_2 = Integer.parseInt(document.data["age2"].toString())

                    //출력값 검사
                    if(document.data["sex"].toString().isEmpty()){
                        txsex.text="무관"
                    }
                    else{
                        txsex.text = document.data["sex"].toString()
                    }
                    if(age_1==0&&age_2==0){
                        txage.text = "무관"
                    }
                    else if(age_1==0&&age_2>0){
                        txage.text = document.data["age2"].toString() + "이하"
                    }
                    else if(age_1>0&&age_2==0){
                        txage.text = document.data["age1"].toString() + "이상"
                    }
                    else{
                        txage.text = document.data["age1"].toString() + "~" + document.data["age2"].toString()
                    }
                    if(document.data["priorityreq"].toString().isEmpty()){
                        txpriority.text = "무관"
                    }
                    else{
                        txpriority.text = document.data["priorityreq"].toString()
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
