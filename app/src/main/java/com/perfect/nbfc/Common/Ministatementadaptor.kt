package com.perfect.nbfc.Ministatement

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
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

class Ministatementadaptor (internal val mContext: Context, internal val jsInfo: JSONArray): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var progressDialog: ProgressDialog? = null
    internal var jsonObject: JSONObject? = null
    private var destination: File? = null
    var jsonArray: JSONArray? = null
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.rv_item_ministatement, parent, false
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


                //demo
                holder.date!!.setText(jsonObject!!.getString("TransDate"))
                holder.amount!!.setText(" ₹ "+jsonObject!!.getString("Amount"))

                if(jsonObject!!.getString("Mode").equals("CR")){
                    holder.cr!!.setTextColor(Color.parseColor("#4CAF50"))
                    holder.cr!!.setText("Cr")
                }else if (jsonObject!!.getString("Mode").equals("DR")){
                    holder.cr!!.setTextColor(Color.parseColor("#FC0303"))
                    holder.cr!!.setText("Dr")
                }

                holder.Interest_Amount!!.setText(" ₹ "+jsonObject!!.getString("InterestAmount"))
                holder.principle_balance!!.setText( " ₹ "+jsonObject!!.getString("Principal"))

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    inner class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {


        var date: TextView? = null
        var amount: TextView? = null
        var cr: TextView? = null
        var principle_balance:TextView? = null
        var Interest_Amount:TextView? = null

        init {

            date = v.findViewById<View>(R.id.date) as TextView
            amount = v.findViewById<View>(R.id.amount) as TextView
            cr = v.findViewById<View>(R.id.cr) as TextView
            principle_balance = v.findViewById<View>(R.id.principle_balance)as TextView
            Interest_Amount = v.findViewById<View>(R.id.Interest_Amount)as TextView
        }
    }
}