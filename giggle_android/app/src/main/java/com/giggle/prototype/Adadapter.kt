package com.giggle.prototype

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class Adadapter (val context: Context, val adlist: ArrayList<ad>) : BaseAdapter(){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.ing_ad_listview,parent,false)

        val adname = view.findViewById<TextView>(R.id.txshname)
        val adposition = view.findViewById<TextView>(R.id.txshposition)
        val image = view.findViewById<ImageView>(R.id.shopimage)
        val ingad = adlist[position]

        val imageurl = ingad.url

        adname.text = ingad.adname
        adposition.text = ingad.adposition
        Glide.with(view)
            .load(imageurl)
            .centerCrop()
            .into(image)
        var params = view.layoutParams
        params.height = 200
        view.layoutParams = params

        return view
    }
    override fun getItem(position:Int):Any{
        return adlist[position]
    }
    override fun getItemId(position:Int):Long{
        return 0
    }
    override fun getCount():Int{
        return adlist.size
    }
}