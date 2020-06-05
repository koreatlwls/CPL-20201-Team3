package com.giggle.prototype

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.ing_ad.*

class CompleteAdActivity : AppCompatActivity() {
    var item = arrayListOf<ad>()
    var name ="0"
    var sttime ="0"
    var fntime ="0"
    var time = "0"
    var photo ="0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.complete_ad)

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        val db = FirebaseFirestore.getInstance()

        //광고 DB 읽어오기
        db.collection("jobads")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //내가 올린 광고 && 완료된 광고
                    if(uid==document.data["uid"].toString()&&document.data["state"].toString()=="1") {
                        name = document.data["shopname"].toString() //가게이름
                        sttime = document.data["st"].toString() //시작시간
                        fntime = document.data["fn"].toString() //종료시간
                        photo = document.data["photouri"].toString() //이미지
                        time = sttime + "~" + fntime  //시간 세팅
                        item.add(ad(name, time, photo))
                    }
                }
                //리스트뷰 어댑터 연결
                val adadapter = Adadapter(this, item)
                listView.adapter = adadapter

                //리스트뷰 아이템 클릭시 상세보기 화면 이동
                listView.onItemClickListener = AdapterView.OnItemClickListener {parent,view,position,id ->
                    val ingshopname = item[position].adname
                    val nextIntent = Intent(this,CompleteAdDetailActivity::class.java)
                    nextIntent.putExtra("name",ingshopname)
                    startActivity(nextIntent)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG).show()
            }

    }
}

