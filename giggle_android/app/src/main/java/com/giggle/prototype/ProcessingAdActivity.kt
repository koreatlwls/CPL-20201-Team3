package com.giggle.prototype

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_processing_ad.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProcessingAdActivity : AppCompatActivity() {
    private val user = FirebaseAuth.getInstance().currentUser
    private var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_processing_ad)

        db = FirebaseFirestore.getInstance()

        val uid = user?.uid.toString()
        // val uid = "u2FMQda137fBhll6pBOHE6prRHO2"  // doc exists, shop exists
        // val uid = "WOhnzxlJ9YOYFG0zkX3XuODOcW63"  // doc exists, shop does not exist

        val docRef = db!!.collection("recruit_shop").document(uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val shop = document.data?.get("shop")
                    if (shop != null) {
                        // Toast.makeText(this, shop.toString(), Toast.LENGTH_LONG).show()
                        // Toast.makeText(this, shop::class.qualifiedName, Toast.LENGTH_LONG).show()

                        val resList = ArrayList<ProcessingAd>()
                        val list = document.toObject(ProcessingAdDocument::class.java)!!.shop

                        for (it in list) {
                            if(it.state==0){
                                resList.add(ProcessingAd(it.shopname, it.shopposition,it.state))
                            }
                            db!!.collection("jobads").document(it.shopname).get().addOnSuccessListener { result->
                                val cal = Calendar.getInstance()
                                val df: DateFormat = SimpleDateFormat("ddHHmm")
                                val finish = result.data?.get("time").toString()
                                val server = df.format(cal.time)
                                if(Integer.parseInt(finish)<Integer.parseInt(server)){
                                    val map = mutableMapOf<String,Any>()
                                    map["shopname"]=it.shopname
                                    map["shopposition"]=it.shopposition
                                    map["state"]=0
                                    db!!.collection("recruit_shop").document(uid).update("shop",FieldValue.arrayRemove(map))
                                    map["state"]=1
                                    db!!.collection("recruit_shop").document(uid).update("shop",FieldValue.arrayUnion(map))
                                }
                            }
                        }

                        val adapter = ProcessingAdAdapter(resList)
                        processingAdRecyclerView.adapter = adapter

                    } else {
                        Toast.makeText(this, "진행중인 알바가 없습니다.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "진행중인 알바가 없습니다.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG).show()
            }

    }


    public override fun onStart() {
        super.onStart()
    }

    public override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}