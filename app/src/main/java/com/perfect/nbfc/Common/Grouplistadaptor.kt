package com.perfect.nbfc.Ministatement

import android.app.ProgressDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File


class Grouplistadaptor(internal val mContext: Context, internal val jsInfo: JSONArray): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var progressDialog: ProgressDialog? = null
    internal var jsonObject: JSONObject? = null
    private var destination: File? = null
    var jsonArray: JSONArray? = null
    var db: DBHandler? = null

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.grpcustlist, parent, false
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
             //   holder.sl!!.setText(""+(position)+1)
                holder.txtDueamount!!.setText(" ₹ " + jsonObject!!.getString("DueAmount"))
                holder.name!!.setText(jsonObject!!.getString("Name") + ", " + jsonObject!!.getString("Address") + "\n[" + jsonObject!!.getString("AccountNumber") + "]"
                )

                holder.etamt!!.setTag(position)
                holder.etamt!!.addTextChangedListener(object : TextWatcher {

                    override fun afterTextChanged(s: Editable) {
                        jsonObject = jsInfo.getJSONObject(position)

                        // tvSample.setText("Text is : "+s)
                        db = DBHandler(mContext)
                        db!!.updateCollection(jsonObject!!.getInt("CustomerId"),""+s)

//                        if (db!!.updateCollection(jsonObject!!.getInt("CustomerId"),""+s)) {
//
//                        } else {
//
//                        }

                    }

                    override fun beforeTextChanged(
                        s: CharSequence, start: Int,
                        count: Int, after: Int
                    ) {
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                    }
                })
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    inner class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {


      //  var sl: TextView? = null
        var name: TextView? = null
        var etamt: EditText? = null
        var txtDueamount: TextView?=null

        init {

        //    sl = v.findViewById<View>(R.id.sl) as TextView
            name = v.findViewById<View>(R.id.name) as TextView
            etamt = v.findViewById<View>(R.id.etamt) as EditText
            txtDueamount = v.findViewById<View>(R.id.txtDueamount) as TextView
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }


}