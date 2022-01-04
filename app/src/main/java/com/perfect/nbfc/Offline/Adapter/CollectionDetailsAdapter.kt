package com.perfect.nbfc.Offline.Adapter


import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.perfect.nbfc.Offline.Fragments.AchivesFragment
import com.perfect.nbfc.Offline.Fragments.SyncPendingFragment

class CollectionDetailsAdapter(private val myContext: Context, fm: FragmentManager, internal var totalTabs: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                return SyncPendingFragment()
            }
            1 -> {
                return AchivesFragment()
            }
            else -> return null
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }

}