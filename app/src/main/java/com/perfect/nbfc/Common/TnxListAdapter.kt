package com.perfect.nbfc.Common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.perfect.nbfc.R

import java.util.ArrayList

class TnxListAdapter(internal var mContext: Context, private val arraylist: ArrayList<TnxModel>) :
    BaseAdapter() {
    internal var inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(mContext)
    }

    inner class ViewHolder {
        internal var tvTime: TextView? = null
        internal var tvAmount: TextView? = null
        internal var tvReferenceNumber: TextView? = null
        internal var tvSlno: TextView? = null
    }

    override fun getCount(): Int {
        return arraylist.size
    }

    override fun getItem(position: Int): TnxModel {
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
            view = inflater.inflate(R.layout.tnx_list, null)
            holder.tvSlno = view!!.findViewById<View>(R.id.tvSlno) as TextView
            holder.tvTime = view!!.findViewById<View>(R.id.tvTime) as TextView
            holder.tvAmount = view!!.findViewById<View>(R.id.tvAmount) as TextView
            holder.tvReferenceNumber = view!!.findViewById<View>(R.id.tvReferenceNumber) as TextView
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        holder.tvSlno!!.text =""+(position+1)
        holder.tvTime!!.text = arraylist[position].Time
        holder.tvAmount!!.text = "₹ " +arraylist[position].Amount
        holder.tvReferenceNumber!!.text = arraylist[position].ReferenceNumber
        return view
    }

}