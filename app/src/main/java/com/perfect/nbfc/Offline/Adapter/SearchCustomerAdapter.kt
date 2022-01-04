package com.perfect.nbfc.Offline.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.perfect.nbfc.Offline.Activity.NewCollectionActivity
import com.perfect.nbfc.Offline.Model.SearchCustomerModel
import com.perfect.nbfc.R
import org.json.JSONObject

import java.util.ArrayList

class SearchCustomerAdapter(ctx: Context, private val imageModelArrayList: ArrayList<SearchCustomerModel>) :
    RecyclerView.Adapter<SearchCustomerAdapter.MyViewHolder>() {

    private val inflater: LayoutInflater
    private val arraylist: ArrayList<SearchCustomerModel>

    init {

        inflater = LayoutInflater.from(ctx)
        this.arraylist = ArrayList<SearchCustomerModel>()
        this.arraylist.addAll(NewCollectionActivity.customerNamesArrayList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCustomerAdapter.MyViewHolder {

        val view = inflater.inflate(R.layout.customerlist, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchCustomerAdapter.MyViewHolder, position: Int) {

        var json_objectdetail: String = imageModelArrayList[position].getNames()
        val jobject = JSONObject(json_objectdetail)

        holder.time.setText(jobject.getString("customername"))
        holder.tvAccount.setText("Acc.no: "+jobject.getString("depositno"))
        holder.tvBalance.setText("Available Balance: "+jobject.getString("balance"))
    }

    override fun getItemCount(): Int {
        return imageModelArrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var time: TextView
        var tvAccount: TextView
        var tvBalance: TextView

        init {

            time = itemView.findViewById(R.id.tvPrdName) as TextView
            tvAccount = itemView.findViewById(R.id.tvAccount) as TextView
            tvBalance = itemView.findViewById(R.id.tvBalance) as TextView
        }

    }
}