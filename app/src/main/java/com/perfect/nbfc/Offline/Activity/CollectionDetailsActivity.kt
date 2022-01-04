package com.perfect.nbfc.Offline.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Offline.Adapter.CollectionDetailsAdapter
import com.perfect.nbfc.Offline.Model.TransactionModel
import com.perfect.nbfc.R
import com.perfect.nbfc.launchingscreens.MPIN.MPINActivity
import org.json.JSONArray
import java.util.*

class CollectionDetailsActivity : AppCompatActivity() {

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var imback: ImageView? = null
    lateinit var handler: Handler
    lateinit var r: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_details)

        tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        viewPager = findViewById<ViewPager>(R.id.viewPager)
        imback = findViewById<ImageView>(R.id.imback)

        tabLayout!!.addTab(tabLayout!!.newTab().setText("Sync Pending"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Archives"))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        imback!!.setOnClickListener { onBackPressed() }

        val adapter = CollectionDetailsAdapter(this, supportFragmentManager, tabLayout!!.tabCount)
        viewPager!!.adapter = adapter

        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        handler = Handler()
        r = Runnable {
            val intent= Intent(this, MPINActivity::class.java)
            startActivity(intent)
            finish()
        }
        startHandler()
    }

    override fun onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction()
        stopHandler()//stop first and then start
        startHandler()
    }

    fun stopHandler() {
        handler.removeCallbacks(r)
    }

    fun startHandler() {
        handler.postDelayed(r, 5 * 60 * 1000) //for 5 minutes
    }
}
