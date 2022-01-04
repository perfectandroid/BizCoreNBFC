package com.perfect.nbfc.Common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView


import androidx.recyclerview.widget.RecyclerView
import com.perfect.nbfc.DB.DBHandler

import com.perfect.nbfc.R

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class TranscationhistoryAdapter(internal var context: Context, internal var jsonArray: JSONArray) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal var jsonObject: JSONObject? = null
    internal var icount: Int? = null
    lateinit var dbHelper : DBHandler

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.tnx_list, parent, false
        )
        vh = MainViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            jsonObject = jsonArray.getJSONObject(position)
            if (holder is MainViewHolder) {
                dbHelper = DBHandler(context!! )
                    holder.tvSlno.text =""+(position+1)
                    holder.tvTime.text = jsonObject!!.getString("Time")
                    holder.tvAmount.text = "₹ " + jsonObject!!.getString("Amount")
                  //  holder.tvAmount.text = "₹ " + jsonObject!!.getString("OpeningAmount")
                  //  holder.tvAvailableBancace.text = "₹ " + jsonObject!!.getString("AvailableBancace")
                    holder.tvReferenceNumber.text = jsonObject!!.getString("ReferenceNumber")
                   // holder.tvChannel.text = "₹ " + jsonObject!!.getString("Channel")


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    private inner class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var tvSlno: TextView
        internal var tvTime: TextView
        internal var tvAmount: TextView
        internal var tvOpeningAmount: TextView
        internal var tvAvailableBancace: TextView
        internal var tvReferenceNumber: TextView
        internal var tvChannel: TextView
       // internal var ll_mod: LinearLayout
        init {
           tvSlno = v.findViewById<View>(R.id.tvSlno) as TextView
           tvTime = v.findViewById<View>(R.id.tvTime) as TextView
           tvAmount = v.findViewById<View>(R.id.tvAmount) as TextView
           tvOpeningAmount = v.findViewById<View>(R.id.tvOpeningAmount) as TextView
           tvAvailableBancace = v.findViewById<View>(R.id.tvAvailableBancace) as TextView
           tvReferenceNumber = v.findViewById<View>(R.id.tvReferenceNumber) as TextView
           tvChannel = v.findViewById<View>(R.id.tvChannel) as TextView
           // ll_mod = v.findViewById<View>(R.id.ll_mod) as LinearLayout
        }
    }

}
