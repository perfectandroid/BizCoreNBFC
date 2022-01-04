package com.perfect.nbfc.Offline.Fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Offline.Adapter.ArchiveAdapter
import com.perfect.nbfc.Offline.Adapter.SyncPendingAdapter
import com.perfect.nbfc.Offline.Model.ArchiveModel
import com.perfect.nbfc.Offline.Model.SyncDateModel
import com.perfect.nbfc.Offline.Model.TransactionModel
import com.perfect.nbfc.R
import org.json.JSONArray

class AchivesFragment : Fragment() {


    internal var archeivelist = ArrayList<SyncDateModel>()
    lateinit var dbHelper : DBHandler
    private var rvArch: RecyclerView? = null
    private var lnrReport: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_achives, container, false)
        rvArch =root.findViewById(R.id.rvArch)
        lnrReport =root.findViewById(R.id.lnrReport)

        dbHelper = DBHandler(context!! )
        if(dbHelper.selectAchiveCount()>0){
            lnrReport!!.visibility=(View.VISIBLE)
        }
        else{
            lnrReport!!.visibility=(View.GONE)
            val dialogBuilder = AlertDialog.Builder(context, R.style.MyDialogTheme)
            dialogBuilder.setMessage("No data found in Archives.")
                .setCancelable(false)
                .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, id -> dialog.dismiss()
                })
            val alert = dialogBuilder.create()
            alert.show()
            val nbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
            nbutton.setTextColor(Color.MAGENTA)
        }


        archData()
        return root
    }

    private fun archData() {
        dbHelper = DBHandler(context!! )
        //archeivelist = ArrayList(dbHelper.readAllArchives())
        archeivelist = ArrayList(dbHelper.readArchivesSyncdate())
        val gson = Gson()
        val listString = gson.toJson(archeivelist, object : TypeToken<ArrayList<TransactionModel>>() {}.type)
        val jarray = JSONArray(listString)
        val lLayout = GridLayoutManager(context, 1)
        rvArch!!.layoutManager = lLayout
        rvArch!!.setHasFixedSize(true)
        val adapter = ArchiveAdapter(context!!, jarray)
        rvArch!!.adapter = adapter
    }

}