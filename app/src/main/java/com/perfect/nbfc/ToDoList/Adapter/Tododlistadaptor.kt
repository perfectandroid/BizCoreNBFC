package com.perfect.nbfc.Todolist.Adaptor

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.perfect.nbfc.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class Tododlistadaptor  (internal val mContext: Context, internal val jsInfo: JSONArray): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var progressDialog: ProgressDialog? = null
    internal var jsonObject: JSONObject? = null
    private var destination: File? = null
    var jsonArray: JSONArray? = null
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.rv_item_todolisting, parent, false
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

                holder.name!!.setText(jsonObject!!.getString("CusName"))
                holder.area!!.setText(jsonObject!!.getString("AreName"))
                if(jsonObject!!.getString("ColStatus").equals("Collected")){
                    holder.status!!.setText("C")}
                else if(jsonObject!!.getString("ColStatus").equals("Pending")){
                    holder.status!!.setText("P")}
                holder.number!!.setText(jsonObject!!.getString("TltShortName")+"\n"+jsonObject!!.getString("TlNumber"))
                val no = position+1;
                holder.sno!!.setText(""+no)

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    inner class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {


        var name: TextView? = null
        var area: TextView? = null
        var status: TextView? = null
        var number: TextView? = null
        var sno:TextView? = null


        init {

            name = v.findViewById<View>(R.id.name) as TextView
            area = v.findViewById<View>(R.id.area) as TextView
            status = v.findViewById<View>(R.id.status) as TextView
            number = v.findViewById<View>(R.id.number) as TextView
            sno = v.findViewById(R.id.sno)as TextView


        }
    }
}