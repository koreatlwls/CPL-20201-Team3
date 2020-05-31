package com.giggle.prototype

import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.memdetail.*


class MemDetail : AppCompatActivity(){
    var mname =""
    var mage =0
    var msex=""
    var mphonenumber=""
    var mposition=""
   // var member = mem("",0,"","","")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.memdetail)

        var memname = ""
        val db = FirebaseFirestore.getInstance()

        if(intent.hasExtra("name")){
            memname = intent.getStringExtra("name")
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
                }
            }
        btrecruit.setOnClickListener(){
            val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.Theme_AppCompat_Light_Dialog))
            builder.setTitle("채용")
            builder.setMessage("채용하시겠습니까?")
            builder.setPositiveButton("확인") { _, _ ->
                val db = FirebaseFirestore.getInstance()
                mname= txname.text.toString()
                mage= Integer.parseInt(txage.text.toString())
                msex= txsex.text.toString()
                mphonenumber= phone.text.toString()
                mposition= txposition.text.toString()
                val member = members(mname,mage,msex,mposition,mphonenumber)
                db.collection("recruit_members").document(mname).set(member)
            }
            builder.setNegativeButton("취소"){_,_->

            }
            builder.show()
        }
    }
}


