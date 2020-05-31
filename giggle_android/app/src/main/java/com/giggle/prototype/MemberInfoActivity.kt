package com.giggle.prototype

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_meminfo.*

class MemberInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meminfo)

        btregister.setOnClickListener{
            val name = edMemName.text.toString()
            var sex = ""
            var age = 0
            val position = edMemPosition.text.toString()
            var number = ""
            if(edMemPhone1.text.isNotEmpty()&&edMemPhone2.text.isNotEmpty()&&edMemPhone3.text.isNotEmpty()){
                number = edMemPhone1.text.toString()+"-"+edMemPhone2.text.toString()+"-"+edMemPhone3.text.toString()
                println(number)
            }
            if(edMemAge.text.isNotEmpty()){
                age = Integer.parseInt(edMemAge.text.toString())
            }
            if (radioMan.isChecked){
                sex = radioMan.text.toString()
            }
            else if(radioWomen.isChecked){
                sex = radioWomen.text.toString()
            }
            if(name.isEmpty()||position.isEmpty()||age<1||sex.isEmpty()||number.isEmpty()){
                edMemName.setBackgroundResource(R.drawable.red_edittext)
                edMemAge.setBackgroundResource(R.drawable.red_edittext)
                edMemPosition.setBackgroundResource(R.drawable.red_edittext)
                edMemPhone1.setBackgroundResource(R.drawable.red_edittext)
                edMemPhone2.setBackgroundResource(R.drawable.red_edittext)
                edMemPhone3.setBackgroundResource(R.drawable.red_edittext)
                Toast.makeText(this, "모든 항목을 채워주세요.", Toast.LENGTH_LONG).show()
            }
            if(name.isNotEmpty()&&position.isNotEmpty()&&age>0&&sex.isNotEmpty()){
                val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.Theme_AppCompat_Light_Dialog))
                builder.setTitle("등록")
                builder.setMessage("등록하시겠습니까?")
                builder.setPositiveButton("확인"){_,_->
                    val user = FirebaseAuth.getInstance().currentUser
                    val db = FirebaseFirestore.getInstance()
                    val member = members(name,age,sex,position, user?.uid,number)
                    if (user != null) {
                        db.collection("members").document(user.uid).set(member)
                    } //DB에 uid기준으로 멤버정보 저장
                    Toast.makeText(this,"등록 성공",Toast.LENGTH_SHORT).show()
                    resetText()//초기화
                }
                builder.setNegativeButton("취소"){_,_->

                }
                builder.show()
            }
        }
    }
    fun resetText(){
        edMemAge.setText(null)
        edMemName.setText(null)
        edMemPosition.setText(null)
        radioMan.setChecked(false)
        radioWomen.setChecked(false)
        edMemPhone1.setText(null)
        edMemPhone2.setText(null)
        edMemPhone3.setText(null)
        edMemName.setBackgroundResource(R.drawable.border_dark)
        edMemAge.setBackgroundResource(R.drawable.border_dark)
        edMemPosition.setBackgroundResource(R.drawable.border_dark)
        edMemPhone1.setBackgroundResource(R.drawable.border_dark)
        edMemPhone2.setBackgroundResource(R.drawable.border_dark)
        edMemPhone3.setBackgroundResource(R.drawable.border_dark)
    }

}