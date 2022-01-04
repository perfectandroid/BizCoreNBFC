package com.perfect.nbfc.Offline.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager


import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Offline.Model.ArchiveModel
import com.perfect.nbfc.Offline.Model.SyncDateModel
import com.perfect.nbfc.Offline.Model.TransactionModel

import com.perfect.nbfc.R

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ArchiveAdapter(internal var context: Context, internal var jsonArray: JSONArray) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal var jsonObject: JSONObject? = null
    lateinit var dbHelper : DBHandler
    internal var archeivedatalist = ArrayList<ArchiveModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.arch_list, parent, false
        )
        vh = MainViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            jsonObject = jsonArray.getJSONObject(position)
            if (holder is MainViewHolder) {
                dbHelper = DBHandler(context!! )
                holder.txt_date.text = jsonObject!!.getString("archivesynctime")
                archeivedatalist = ArrayList(dbHelper.readAllArchives(jsonObject!!.getString("archivesynctime")))
                val gson = Gson()
                val listString = gson.toJson(archeivedatalist, object : TypeToken<ArrayList<ArchiveModel>>() {}.type)
                val jarray = JSONArray(listString)
                val lLayout = GridLayoutManager(context, 1)
                holder.rvarchdata!!.layoutManager = lLayout as RecyclerView.LayoutManager?
                holder.rvarchdata!!.setHasFixedSize(true)
                val adapter = ArchiveDataAdapter(context!!, jarray)
                holder.rvarchdata!!.adapter = adapter

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
        internal var txt_date: TextView
        internal var rvarchdata: RecyclerView

        init {
            txt_date = v.findViewById<View>(R.id.txt_date) as TextView
            rvarchdata = v.findViewById<View>(R.id.rvarchdata) as RecyclerView
        }
    }

}
