package com.giggle.prototype

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.completeaddetail.*

class CompleteAdDetailActivity : AppCompatActivity() {
    private var isFabOpen=false
    private var shopphoto=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.completeaddetail)

        var shname = ""
        val db = FirebaseFirestore.getInstance()

        fabMain.setOnClickListener{toggleFab()}

        if(intent.hasExtra("name")){
            shname = intent.getStringExtra("name")
        }
        db.collection("jobads")
            .whereEqualTo("shopname",shname )
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //DB읽은 데이터 출력
                    txname.text = document.data["shopname"].toString()
                    txposition.text = document.data["shopposition"].toString()
                    txinfo.text = document.data["businessinfo"].toString()
                    txnum.text = document.data["numofperson"].toString()
                    txpay.text = document.data["hourlypay"].toString()
                    txtime.text = document.data["st"].toString()+"~"+document.data["fn"].toString()
                    shopphoto=document.data["photouri"].toString()
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
        btdelete.setOnClickListener{
            val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.Theme_AppCompat_Light_Dialog))
            builder.setTitle("삭제")
            builder.setMessage("삭제하시겠습니까?")
            builder.setPositiveButton("확인") { _, _ ->
                db.collection("jobads")?.document(shname)?.delete()?.addOnCompleteListener{
                        task->
                    if(task.isSuccessful()) {
                        Toast.makeText(this, "삭제완료.", Toast.LENGTH_LONG).show()
                        val nextIntent = Intent(this, IngAdActivity::class.java)
                        startActivity(nextIntent)
                    }
                }
            }
            builder.setNegativeButton("취소"){_,_->

            }
            builder.show()
        }
        btapp.setOnClickListener{
            val nextIntent = Intent(this, parttimerActivity::class.java)
            nextIntent.putExtra("shopname", shname)
            startActivity(nextIntent)
        }
    }
    private fun toggleFab(){
        if(isFabOpen){
            ObjectAnimator.ofFloat(btapp, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(btdelete, "translationY", 0f).apply { start() }
            fabMain.setImageResource(R.drawable.ic_action_addwhite)
        } else {
            ObjectAnimator.ofFloat(btapp, "translationY", -200f).apply { start() }
            ObjectAnimator.ofFloat(btdelete, "translationY", -400f).apply { start() }
            fabMain.setImageResource(R.drawable.ic_action_closewhite)
        }
        isFabOpen=!isFabOpen
    }
}


