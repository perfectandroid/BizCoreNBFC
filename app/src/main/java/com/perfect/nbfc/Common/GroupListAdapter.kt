package com.perfect.nbfc.Common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.perfect.nbfc.R

import java.util.ArrayList

class GroupListAdapter(internal var mContext: Context, private val arraylist: ArrayList<GroupModel>) :
    BaseAdapter() {
    internal var inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(mContext)
    }

    inner class ViewHolder {
        internal var tvgroupName: TextView? = null
        internal var tvSlno: TextView? = null
    }

    override fun getCount(): Int {
        return arraylist.size
    }

    override fun getItem(position: Int): GroupModel {
        return arraylist[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        val holder: ViewHolder
        if (view == null) {
            holder = ViewHolder()
            view = inflater.inflate(R.layout.grouplist, null)
            holder.tvgroupName = view!!.findViewById<View>(R.id.tvgroupName) as TextView
            holder.tvSlno = view!!.findViewById<View>(R.id.tvSlno) as TextView
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        holder.tvgroupName!!.text = arraylist[position].GroupName
        holder.tvSlno!!.text =""+(position+1)
        return view
    }

}