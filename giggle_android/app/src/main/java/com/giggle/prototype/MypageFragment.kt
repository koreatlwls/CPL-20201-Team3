package com.giggle.prototype

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MypageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_mypage, container, false)

    companion object {
        fun newInstance(): MypageFragment = MypageFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
}