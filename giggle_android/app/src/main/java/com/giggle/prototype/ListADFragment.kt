package com.giggle.prototype

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_list_ad.*
import kotlinx.android.synthetic.main.fragment_mypage.*
import kotlinx.android.synthetic.main.activity_main.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.*

class ListADFragment : Fragment() {
    // private val user = FirebaseAuth.getInstance().currentUser
    private var db: FirebaseFirestore? = null

    private var adapter: FirestoreRecyclerAdapter<JobAd, JobADViewHolder>? = null
    private var firestoreListener: ListenerRegistration? = null
    private var adsList = mutableListOf<JobAd>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_ad, container, false)
    }

    companion object {
        fun newInstance(): ListADFragment = ListADFragment()
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val mLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        // starts from here
        loadAdsList("")

        firestoreListener = db!!.collection("jobads").whereEqualTo("state",0).orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    return@EventListener
                }

                adsList = mutableListOf()

                if (documentSnapshots != null) {
                    for (doc in documentSnapshots) {
                        val ad = doc.toObject(JobAd::class.java)
                        ad.shopname = doc.id
                        adsList.add(ad)
                    }
                }

                adapter!!.notifyDataSetChanged()
                recyclerView.adapter = adapter
            })

        searchBtn.setOnClickListener{
            if (searchText.text.isEmpty()) {
                Toast.makeText(context, "검색어를 입력해주세요.", Toast.LENGTH_LONG).show()
            } else {
                val text = searchText.text.toString()

                // FIXME: calling loadAdsList multiple times does not working
                loadAdsList(text)

            }
        }

    }

    private fun loadAdsList(text: String) {
        val query = db!!.collection("jobads") //.whereEqualTo("", text)

        val response = FirestoreRecyclerOptions.Builder<JobAd>()
            .setQuery(query, JobAd::class.java)
            .build()

        adapter = object : FirestoreRecyclerAdapter<JobAd, JobADViewHolder>(response) {
            override fun onBindViewHolder(holder: JobADViewHolder, position: Int, model: JobAd) {
                val ad = adsList[position]
                holder.title.text = ad.shopname
                holder.content.text = ad.shopposition
                // for test
                // holder.content.text = ad.timestamp.toString()
                holder.itemLayout.setOnClickListener { adDetail(ad) }  // OnClickListener on each Vertical itemLayout

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobADViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_jobad, parent, false)
                return JobADViewHolder(view)
            }

        }

        adapter!!.notifyDataSetChanged()
        recyclerView.adapter = adapter
    }

    public override fun onStart() {
        super.onStart()

        adapter!!.startListening()
    }

    public override fun onStop() {
        super.onStop()

        adapter!!.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

    private fun adDetail(ad: JobAd) {
        // Toast.makeText(context, "Clicked " + ad.shopname, Toast.LENGTH_LONG).show()

        val nextIntent = Intent(context, AdDetailApply::class.java)
        nextIntent.putExtra("name", ad.shopname)
        startActivity(nextIntent)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}