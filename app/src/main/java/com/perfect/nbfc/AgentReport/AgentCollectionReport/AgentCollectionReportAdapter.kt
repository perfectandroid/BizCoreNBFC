package com.perfect.nbfc.AgentCollectionReport

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

class AgentCollectionReportAdapter(internal var context: Context, internal var jsonArray: JSONArray) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal var jsonObject: JSONObject? = null
    internal var icount: Int? = null
    lateinit var dbHelper : DBHandler

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.agent_collection_table, parent, false
        )
        vh = MainViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            jsonObject = jsonArray.getJSONObject(position)
            if (holder is MainViewHolder) {
                dbHelper = DBHandler(context!! )
                holder.txtName.setText(jsonObject!!.getString("PartyName") + "\n" + "(" + jsonObject!!.getString("Account") + ")")
                holder.txtSlno.text = (position + 1).toString() + "."
                val color: Int
                if (jsonObject!!.getString("PaymentOrReceipt").equals("Payment")) {
                    color = context.resources.getColor(R.color.red)
                    holder.txtAmount.setText( "₹ "+jsonObject!!.getString("Amount") /*+" (P)"*/)
                } else {
                    color = context.resources.getColor(R.color.green)
                    holder.txtAmount.setText( "₹ "+jsonObject!!.getString("Amount")  /*+" (R)"*/)
                }
                holder.txttym.setText(jsonObject!!.getString("CollectedTime") )
                holder.txtAmount.setTextColor(color)
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
        var txtName: TextView
        var txtAmount: TextView
        var txtType: TextView
        var txttym: TextView
        var txtSlno: TextView

        init {
            txtName = v.findViewById(R.id.txtName)
            txtAmount = v.findViewById(R.id.txtAmount)
            txttym = v.findViewById(R.id.txttym)
            txtType = v.findViewById(R.id.txtType)
            txtSlno = v.findViewById(R.id.txtSlno)
        }
    }

}
