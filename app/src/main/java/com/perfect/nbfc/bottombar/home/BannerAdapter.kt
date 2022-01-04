package com.perfect.nbfc.bottombar.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import androidx.viewpager.widget.PagerAdapter

import com.perfect.nbfc.R

import java.util.ArrayList

class BannerAdapter(private val context: Context?, private val images: ArrayList<Int>) : PagerAdapter() {

    private val inflater: LayoutInflater
    init {
        inflater = LayoutInflater.from(context)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val myImageLayout = inflater.inflate(R.layout.slide, view, false)
        val myImage = myImageLayout
            .findViewById(R.id.image) as ImageView
        myImage.setImageResource(images[position])
        view.addView(myImageLayout, 0)
        return myImageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

}