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
    var touid=""
    var shopname = ""
    var shopposition = ""
   // var member = mem("",0,"","","")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.memdetail)

        var memname = ""

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
                }
            }
        btrecruit.setOnClickListener(){
            val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.Theme_AppCompat_Light_Dialog))
            builder.setTitle("채용")
            builder.setMessage("채용하시겠습니까?")
            builder.setPositiveButton("확인") { _, _ ->
                val db = FirebaseFirestore.getInstance()
                val db1 = FirebaseFirestore.getInstance()
                mname= txname.text.toString()
                mage= Integer.parseInt(txage.text.toString())
                msex= txsex.text.toString()
                mphonenumber= phone.text.toString()
                mposition= txposition.text.toString()
                mage = Integer.parseInt(txage.text.toString())
                val member = members(mname,mage,msex,mposition,mphonenumber,touid)
                db.collection("recruit_members").document(mname).set(member)
                val title = "채용"
                val message = shopname + "에 채용되었습니다."
                var token =""
                db.collection("pushtokens").document(touid).get().addOnSuccessListener { result->
                    token = result.data?.get("pushtoken").toString()
                    SendNotification.sendNotification(token,title,message,shopname,shopposition)
        }
   }
            builder.setNegativeButton("취소"){_,_->

            }
            builder.show()
        }
    }
}


