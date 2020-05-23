package com.giggle.prototype

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.ing_ad.*

class IngAdActivity : AppCompatActivity() {
    var item = arrayListOf<ad>()
    var name ="0"
    var position ="0"
    var photo ="0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ing_ad)

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        val db = FirebaseFirestore.getInstance()

        //내가 등록한 광고 DB 읽어오기
        db.collection("jobads")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    name = document.data["shopname"].toString()
                    position = document.data["shopposition"].toString()
                    photo = document.data["photouri"].toString()
                    item.add(ad(name,position,photo))
                }
                //리스트뷰 어댑터 연결
                val adadapter = Adadapter(this, item)
                listView.adapter = adadapter

                //리스트뷰 아이템 클릭시 상세보기 화면 이동
                listView.onItemClickListener = AdapterView.OnItemClickListener {parent,view,position,id ->
                    val ingshopname = item[position].adname
                    val nextIntent = Intent(this,IngAd_DetailActivity::class.java)
                    nextIntent.putExtra("name",ingshopname)
                    startActivity(nextIntent)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG).show()
            }

    }
}
class ad (val adname:String, val adposition:String, val url:String)
