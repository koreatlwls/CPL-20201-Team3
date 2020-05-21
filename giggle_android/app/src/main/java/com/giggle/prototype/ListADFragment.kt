package com.giggle.prototype

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_mypage.*

class ListADFragment : Fragment() {
    // private val user = FirebaseAuth.getInstance().currentUser
    // private val storage = FirebaseStorage.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_ad, container, false)
    }

    companion object {
        fun newInstance(): ListADFragment = ListADFragment()
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)







    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}