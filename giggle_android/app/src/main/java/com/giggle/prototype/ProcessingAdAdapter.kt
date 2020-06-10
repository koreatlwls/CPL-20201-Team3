package com.giggle.prototype

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_jobad.view.*

class ProcessingAdAdapter(val items: ArrayList<ProcessingAd>) : RecyclerView.Adapter<ProcessingAdAdapter.ViewHolder>() {
    private var db: FirebaseFirestore? = null

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val listener = View.OnClickListener { it ->
            // Toast.makeText(it.context, "Clicked: ${item.shopname}", Toast.LENGTH_SHORT).show()

            val nextIntent = Intent(holder.itemView.context, ProcessingAdDetailActivity::class.java)
            nextIntent.putExtra("name", item.shopname)
            nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            holder.itemView.context.startActivity(nextIntent)
        }
        holder.apply {
            bind(listener, item);
            itemView.tag = item;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_jobad, parent, false)
        return ProcessingAdAdapter.ViewHolder(inflatedView)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v;
        fun bind(listener: View.OnClickListener, item: ProcessingAd) {
            view.tvTitle.text = item.shopname
            view.tvContent.text = item.shopposition
            view.setOnClickListener(listener);
        }
    }
}