package com.perfect.nbfc.bottombar.aboutus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.perfect.nbfc.R

class AboutUsFragment : Fragment() {

    private lateinit var dashboardViewModel: AboutUsViewModel

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dashboardViewModel =ViewModelProviders.of(this).get(AboutUsViewModel::class.java)
        (activity as AppCompatActivity).supportActionBar!!.hide()
        val root = inflater.inflate(R.layout.fragment_aboutus, container, false)
        return root
    }

}