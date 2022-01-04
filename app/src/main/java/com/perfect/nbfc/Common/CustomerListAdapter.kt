package com.perfect.nbfc.Common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.perfect.nbfc.R

import java.util.ArrayList

class CustomerListAdapter(internal var mContext: Context, private val arraylist: ArrayList<CustomerModel>) :
    BaseAdapter() {
    internal var inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(mContext)
    }

    inner class ViewHolder {
        internal var tvAreaName: TextView? = null
        internal var tvSlno: TextView? = null
        internal var tvPhone: TextView? = null
        internal var tvAccountno: TextView? = null
    }

    override fun getCount(): Int {
        return arraylist.size
    }

    override fun getItem(position: Int): CustomerModel {
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
            view = inflater.inflate(R.layout.custlist, null)
            holder.tvAreaName = view!!.findViewById<View>(R.id.tvAreaName) as TextView
            holder.tvSlno = view!!.findViewById<View>(R.id.tvSlno) as TextView
            holder.tvPhone = view!!.findViewById<View>(R.id.tvPhone) as TextView
            holder.tvAccountno = view!!.findViewById<View>(R.id.tvAccountno) as TextView
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        val pos = position+1
        holder.tvAreaName!!.text = arraylist[position].Name+"\n"+arraylist[position].Address
        holder.tvSlno!!.text =""+pos
        holder.tvPhone!!.text = arraylist[position].MobileNumber
        holder.tvAccountno!!.text = arraylist[position].AccountNumber
        return view
    }

}