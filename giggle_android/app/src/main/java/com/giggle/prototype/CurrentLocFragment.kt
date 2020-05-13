package com.giggle.prototype

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class CurrentLocFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_current_loc, container, false)

    companion object {
        fun newInstance(): CurrentLocFragment = CurrentLocFragment()
    }
}