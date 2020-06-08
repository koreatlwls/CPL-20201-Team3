package com.giggle.prototype

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_processing_ad.*

class ProcessingAdActivity : AppCompatActivity() {
    private var db: FirebaseFirestore? = null

    private var adapter: FirestoreRecyclerAdapter<JobAd, JobADViewHolder>? = null
    private var firestoreListener: ListenerRegistration? = null
    private var adsList = mutableListOf<JobAd>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_processing_ad)

        db = FirebaseFirestore.getInstance()

        val mLayoutManager = LinearLayoutManager(this)
        processingAdRecyclerView.layoutManager = mLayoutManager
        processingAdRecyclerView.itemAnimator = DefaultItemAnimator()

        // starts from here
        loadProcessingAdsList()

        firestoreListener = db!!.collection("recruit_shop")
            // FIXME: timestamp field does not exist
            // .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    return@EventListener
                }

                adsList = mutableListOf()

                if (documentSnapshots != null) {
                    for (doc in documentSnapshots) {
                        val ad = doc.toObject(JobAd::class.java)
                        ad.shopname = doc.id
                        // FIXME: fix to shopname here
                        adsList.add(ad)
                    }
                }

                adapter!!.notifyDataSetChanged()
                processingAdRecyclerView.adapter = adapter
            })

    }

    private fun loadProcessingAdsList() {
        val query = db!!.collection("recruit_shop")

        val response = FirestoreRecyclerOptions.Builder<JobAd>()
            .setQuery(query, JobAd::class.java)
            .build()

        adapter = object : FirestoreRecyclerAdapter<JobAd, JobADViewHolder>(response) {
            override fun onBindViewHolder(holder: JobADViewHolder, position: Int, model: JobAd) {
                val ad = adsList[position]
                holder.title.text = ad.shopname
                holder.content.text = ad.shopposition
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobADViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_jobad, parent, false)
                return JobADViewHolder(view)
            }
        }

        adapter!!.notifyDataSetChanged()
        processingAdRecyclerView.adapter = adapter
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
}
