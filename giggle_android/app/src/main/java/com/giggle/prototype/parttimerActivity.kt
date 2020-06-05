package com.giggle.prototype

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.ing_ad.*


class parttimerActivity : AppCompatActivity() {
    var member = arrayListOf<mem>()
    var name =""
    var phone =""
    var shopname = ""
    var shopposition = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.finish_ad)

        val db = FirebaseFirestore.getInstance()
        if(intent.hasExtra("shopname")){
            shopname = intent.getStringExtra("shopname")
        }
        db.collection("jobads").document(shopname).get().addOnSuccessListener {result->
            val list = result.data?.get("parttimer") as List<String>
            shopposition = result.data?.get("shopposition").toString()
            var uid =""
            for(uid in list){
                db.collection("members").document(uid).get().addOnSuccessListener { result->
                    name = result.data?.get("name").toString()
                    phone = result.data?.get("phonenumber").toString()
                    member.add(mem(name,phone))
                    val appadapter = Appadapter(this, member)
                    listView.adapter = appadapter
                }
            }

        }
    }
}
