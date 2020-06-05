package com.giggle.prototype

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide

class Appadapter (val context: Context, val memlist: ArrayList<mem>) : BaseAdapter(){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.app_listview,parent,false)

        val memname = view.findViewById<TextView>(R.id.txmemname)
        val memphone = view.findViewById<TextView>(R.id.txmemphone)
        val member = memlist[position]

        //리스트뷰 텍스트 이미지 세팅
        memname.text = member.memname
        memphone.text = member.memphone

        //리스트뷰 높이 조절
        var params = view.layoutParams
        params.height = 200
        view.layoutParams = params

        return view
    }
    override fun getItem(position:Int):Any{
        return memlist[position]
    }
    override fun getItemId(position:Int):Long{
        return 0
    }
    override fun getCount():Int{
        return memlist.size
    }
}