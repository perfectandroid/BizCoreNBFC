package com.perfect.nbfc.AgentReport.Summary.Adapter

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

class SummaryModuleAdapter(internal var context: Context, internal var jsonArray: JSONArray) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal var jsonObject: JSONObject? = null
    internal var icount: Int? = null
    lateinit var dbHelper : DBHandler

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.summary_list, parent, false
        )
        vh = MainViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            jsonObject = jsonArray.getJSONObject(position)
            if (holder is MainViewHolder) {
                dbHelper = DBHandler(context!! )
                if(jsonObject!!.getString("TransType").equals("R")) {
                    holder.ll_mod.visibility= VISIBLE
                    holder.txt_module.text = jsonObject!!.getString("Module")
                    holder.txt_count.text = jsonObject!!.getString("Count")
                    holder.txt_amount.text = "₹ " + jsonObject!!.getString("Amount")
                }else{
                    holder.ll_mod.visibility=GONE
                }
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
        internal var txt_module: TextView
        internal var txt_count: TextView
        internal var txt_amount: TextView
        internal var ll_mod: LinearLayout
        init {
            txt_module = v.findViewById<View>(R.id.txt_module) as TextView
            txt_count = v.findViewById<View>(R.id.txt_count) as TextView
            txt_amount = v.findViewById<View>(R.id.txt_amount) as TextView
            ll_mod = v.findViewById<View>(R.id.ll_mod) as LinearLayout
        }
    }

}
