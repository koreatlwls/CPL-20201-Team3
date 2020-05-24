package com.giggle.prototype

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class Adadapter (val context: Context, val adlist: ArrayList<ad>) : BaseAdapter(){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.ing_ad_listview,parent,false)

        val adname = view.findViewById<TextView>(R.id.txshname)
        val adtime = view.findViewById<TextView>(R.id.txtime)
        val image = view.findViewById<ImageView>(R.id.shopimage)
        val ingad = adlist[position]

        val imageurl = ingad.url

        //리스트뷰 텍스트 이미지 세팅
        adname.text = ingad.adname
        adtime.text = ingad.adtime
        Glide.with(view)
            .load(imageurl)
            .centerCrop()
            .into(image)
        //리스트뷰 높이 조절
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