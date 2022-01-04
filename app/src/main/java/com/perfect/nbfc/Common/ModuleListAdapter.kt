package com.perfect.nbfc.Common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.perfect.nbfc.R

import java.util.ArrayList

class ModuleListAdapter(internal var mContext: Context, private val arraylist: ArrayList<ModuleModel>) :
    BaseAdapter() {
    internal var inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(mContext)
    }

    inner class ViewHolder {
        internal var tvAreaName: TextView? = null
    }

    override fun getCount(): Int {
        return arraylist.size
    }

    override fun getItem(position: Int): ModuleModel {
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
            view = inflater.inflate(R.layout.arealist, null)
            holder.tvAreaName = view!!.findViewById<View>(R.id.tvAreaName) as TextView
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        holder.tvAreaName!!.text = arraylist[position].ModuleName
        return view
    }

}