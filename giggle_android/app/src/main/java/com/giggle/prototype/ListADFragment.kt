package com.giggle.prototype

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_list_ad.*
import kotlinx.android.synthetic.main.fragment_mypage.*

class ListADFragment : Fragment() {
    // private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_ad, container, false)
    }

    companion object {
        fun newInstance(): ListADFragment = ListADFragment()
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager






        searchBtn.setOnClickListener{
            if (searchText.text.isEmpty()) {
                Toast.makeText(context, "검색어를 입력해주세요.", Toast.LENGTH_LONG).show()
            } else {
                val text = searchText.text.toString()

                db.collection("jobads")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            println(document.data)
                            // Toast.makeText(context, "${}", Toast.LENGTH_LONG).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG).show()
                    }

                db.collection("jobads")
                    // .whereArrayContains("shopposition", text)
                    .whereEqualTo("shopposition", text)
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            println(document.data["shopname"])
                            // Toast.makeText(context, "${}", Toast.LENGTH_LONG).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG).show()
                    }


            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}