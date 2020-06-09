package com.giggle.prototype

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.ing_ad.*


class applicantsActivity : AppCompatActivity() {
    var member = arrayListOf<mem>()
    var name =""
    var phone =""
    var shopname = ""
    var shopposition = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ing_ad)

        val db = FirebaseFirestore.getInstance()
        if(intent.hasExtra("shopname")){
            shopname = intent.getStringExtra("shopname")
        }
        db.collection("jobads").document(shopname).get().addOnSuccessListener {result->
            if(result.data?.get("applicants") !=null){
                val list = result.data?.get("applicants") as List<String>
                for(uid in list){
                    db.collection("members").document(uid).get().addOnSuccessListener { result->
                        name = result.data?.get("name").toString()
                        phone = result.data?.get("phonenumber").toString()
                        member.add(mem(name,phone))
                        val appadapter = Appadapter(this, member)
                        listView.adapter = appadapter
                        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                            val mname = member[position].memname
                            val nextIntent = Intent(this,MemDetail::class.java)
                            nextIntent.putExtra("name",mname)
                            nextIntent.putExtra("shopname",shopname)
                            nextIntent.putExtra("shopposition",shopposition)
                            startActivity(nextIntent)
                        }
                    }
                }
            }
            shopposition = result.data?.get("shopposition").toString()
        }
    }
}
class mem (val memname:String, val memphone:String)