package com.perfect.nbfc.Demandlist

import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.perfect.nbfc.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class DemandLsitAdaptor (internal val mContext: Context, internal val jsInfo: JSONArray): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var progressDialog: ProgressDialog? = null
    internal var jsonObject: JSONObject? = null
    private var destination: File? = null
    var jsonArray: JSONArray? = null
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.rv_item_dimand_listing, parent, false
        )
        vh = MainViewHolder(v)
        return vh
    }

    override fun getItemCount(): Int {
        return jsInfo.length()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            jsonObject = jsInfo.getJSONObject(position)
            if (holder is MainViewHolder) {

                holder.customer_name!!.setText(jsonObject!!.getString("CustomerName")+"\nLoan Number : "+jsonObject!!.getString("LoanNumber")+"\nMobile No : "+jsonObject!!.getString("MObileNo")+"\nScheme Name : "+jsonObject!!.getString("SchemeName"))
                holder.customer_number!!.setText("SanctionDate : " + jsonObject!!.getString("SanctionDate")+"\nDue date : "+jsonObject!!.getString("DueDate")+"\nLoan Amount : "+jsonObject!!.getString("LoanAmount")+"\nPaid Amt : "+jsonObject!!.getString("PaidAmt")+"\nOutstand : "+jsonObject!!.getString("Outstand")+"\nTransdate : "+jsonObject!!.getString("Transdate")+"\nArrearAmt : "+jsonObject!!.getString("ArrearAmt")+"\nDemandAmt : "+jsonObject!!.getString("DemandAmt")+"\nAdvance : "+jsonObject!!.getString("Advance"))
//                holder.amount!!.setText("₹ "+jsonObject!!.getString("LoanAmount"))

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    inner class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {


        var customer_name: TextView? = null
        var customer_number: TextView? = null
        var amount: TextView? = null



        init {

            customer_name = v.findViewById<View>(R.id.customer_name) as TextView
            customer_number = v.findViewById<View>(R.id.customer_number) as TextView
            amount = v.findViewById<View>(R.id.amount) as TextView


        }
    }
}