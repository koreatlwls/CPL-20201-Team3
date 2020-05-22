package com.giggle.prototype

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JobADViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var title: TextView
    var content: TextView

    init {
        title = view.findViewById(R.id.tvTitle)
        content = view.findViewById(R.id.tvContent)
    }
}