package com.giggle.prototype

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_processing_ad.*
import kotlin.reflect.typeOf

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
                            resList.add(ProcessingAd(it.shopname, it.shopposition))
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
