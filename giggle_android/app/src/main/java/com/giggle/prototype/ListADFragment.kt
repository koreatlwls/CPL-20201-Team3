package com.giggle.prototype

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_list_ad.*
import kotlinx.android.synthetic.main.fragment_mypage.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_main.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException

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

        firestoreListener = db!!.collection("jobads")
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


                /*
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
*/
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
                // holder.edit.setOnClickListener { updateNote(note) }
                // holder.delete.setOnClickListener { deleteNote(note.id!!) }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobADViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_jobad, parent, false)
                return JobADViewHolder(view)
            }

            /*
            override fun onError(e: FirebaseFirestoreException?) {

            } */
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


    /*
    private fun updateNote(note: Note) {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra("UpdateNoteId", note.id)
        intent.putExtra("UpdateNoteTitle", note.title)
        intent.putExtra("UpdateNoteContent", note.content)
        startActivity(intent)
    }

    private fun deleteNote(id: String) {
        firestoreDB!!.collection("notes")
                .document(id)
                .delete()
                .addOnCompleteListener {
                    Toast.makeText(applicationContext, "Note has been deleted!", Toast.LENGTH_SHORT).show()
                }
    }
     */

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}