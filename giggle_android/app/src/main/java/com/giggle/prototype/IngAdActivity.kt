package com.giggle.prototype

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.ing_ad.*

class IngAdActivity : AppCompatActivity() {
    private var item = ArrayList<String>()
    private var name ="0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ing_ad)

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        val db = FirebaseFirestore.getInstance()

        db.collection("jobads")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    name = document.data["shopname"].toString()
                    item.add(name)
                }
                listView.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,item)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG).show()
            }

    }


}
