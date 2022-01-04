package com.perfect.nbfc.AgentCollectionReport

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.perfect.nbfc.AgentCollectionReport.AgentCollectionReportAdapter
import com.perfect.nbfc.Api.ApiInterface
import com.perfect.nbfc.Api.ApiService
import com.perfect.nbfc.Common.ModuleListAdapter
import com.perfect.nbfc.Common.ModuleModel
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.Helper.ConnectivityUtils
import com.perfect.nbfc.Helper.CryptoGraphy
import com.perfect.nbfc.R
import kotlinx.android.synthetic.main.activity_agent_collection_list.*
import kotlinx.android.synthetic.main.module_selection_layout.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*

class AgentCollectionReportActivity : AppCompatActivity(),
    View.OnClickListener, OnItemSelectedListener {
    var txtFrom: TextView? = null
    var btnsubmit: Button? = null
    private var mRcvAgentColectn: RecyclerView? = null
    val calendar = Calendar.getInstance()
    var year = calendar[Calendar.YEAR]
    var month = calendar[Calendar.MONTH]
    var modules: String? = null
    var type: String? = null
    private var progressDialog: ProgressDialog? = null
    var day = calendar[Calendar.DAY_OF_MONTH]
    var fromdate: String? = null
    var arrayForSpinner = arrayOf<String?>("ALL", "DAILY DEPOSIT", "SAVINGS BANK", "RECURRING DEPOSIT", "GROUP DEPOSIT SCHEME")
    var module: ArrayAdapter<*>? = null
    var dateTime: String? = null
    var lnr_layout1: LinearLayout? = null
    var crdView1: CardView? = null
    var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
    var dateForSearch = ""
    var from1: String? = null
    var s1: String? = null
    var s2: String? = null
    var s3: String? = null
    private var list_view: ListView?=null
    private var etxtsearch: EditText? =null
    private var array_sort =ArrayList<ModuleModel>()
    private var searchModuleArrayList = ArrayList<ModuleModel>()
    private var sadapter: ModuleListAdapter? = null
    private var strSubModule:String?=""

    protected lateinit var builder: AlertDialog.Builder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agent_collection_list)
        initiateViews()
        setRegViews()
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_MONTH]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]
        val date = day.toString() + "/" + (month + 1) + "/" + year
        dateForSearch = year.toString() + "-" + (month + 1) + "-" + day.toString()
        txtFrom!!.text = date
        modules = ""


    }

    private fun setRegViews() {
        imback!!.setOnClickListener(this)
        btnsubmit!!.setOnClickListener(this)
        lnr2!!.setOnClickListener(this)
        edt_txt_selecmdl!!.text=null
        edt_txt_selecmdl!!.setOnClickListener(this)
        edt_txt_selecmdl!!.keyListener=null

    }

    private fun initiateViews() {
        txtFrom = findViewById<View>(R.id.txtFrom) as TextView
        btnsubmit = findViewById<View>(R.id.btn_submit) as Button
        mRcvAgentColectn = findViewById<View>(R.id.rcv_agent_colectn) as RecyclerView
        lnr_layout1 = findViewById<View>(R.id.lnr_layout1) as LinearLayout
//        crdView1 = findViewById<View>(R.id.crdView1) as CardView
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imback -> {
                finish()
            }
            R.id.btn_submit -> {
                if(edt_txt_selecmdl.getText().toString().isEmpty())
                {
                    edt_txt_selecmdl.setError("Please select account")
                }
                else{
                    from1 = "main"
                    getAgentcollection(from1!!, s1, s2, s3)
                }
            }
            R.id.lnr2 -> {
                dateSelector()
            }
            R.id.txtFrom -> {
                dateSelector()
            }
            R.id.edt_txt_selecmdl ->{
                getModule()
            }
        }
    }

    private fun getModule() {
        try {
            val builder = AlertDialog.Builder(this)
            val inflater1 =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater1.inflate(R.layout.modulelist_popup, null)
            list_view = layout.findViewById(R.id.list_view)
            etxtsearch  = layout.findViewById(R.id.etsearch)
            val tv_popuptitle = layout.findViewById(R.id.tv_popuptitle) as TextView
            tv_popuptitle.setText("Choose Module")
            builder.setView(layout)
            val alertDialog = builder.create()
            getModuleList(alertDialog)
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getModuleList(dialog: AlertDialog) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@AgentCollectionReportActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
                try {
                    val client = OkHttpClient.Builder()
                        .sslSocketFactory(getSSLSocketFactory())
                        .hostnameVerifier(getHostnameVerifier())
                        .build()
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()
                    val retrofit = Retrofit.Builder()
                        .baseUrl(ApiService.BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build()

                    val apiService = retrofit.create(ApiInterface::class.java!!)
                    val requestObject1 = JSONObject()
                    try {


                        val agentId = "0000"
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss.SSS",
                            Locale.ENGLISH
                        )
                        val dateTime = simpleDateFormat.format(calendar.time)


//                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(
//                            this
//                        )
//                        var Imei = DeviceAppDetails.imei
//                        if (Imei != null && !Imei.isEmpty()) {
//                        } else {
//                            val DeviceAppDetails1 =
//                                BizcoreApplication.getInstance().getDeviceAppDetails1(
//                                    this
//                                )
//                            Imei = DeviceAppDetails1.imei
//                        }


                        val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                        var Imei = DeviceAppDetails1.imei


                        requestObject1.put("LoginMode", BizcoreApplication.encryptMessage("0"))
                        requestObject1.put(
                            "Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(
                                Imei
                            )
                        )
                        requestObject1.put(
                            "BankKey", BizcoreApplication.encryptMessage(
                                getResources().getString(
                                    R.string.BankKey
                                )
                            )
                        )
                        requestObject1.put(
                            "BankHeader", BizcoreApplication.encryptMessage(
                                getResources().getString(
                                    R.string.BankHeader
                                )
                            )
                        )
                        requestObject1.put(
                            "BankVerified",
                            "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg="
                        )
                        requestObject1.put("Module", BizcoreApplication.encryptMessage("0"))
                        requestObject1.put("Groupid", BizcoreApplication.encryptMessage("0"))
                        requestObject1.put("TransType", BizcoreApplication.encryptMessage("R"))


                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(
                            findViewById(R.id.rl_main),
                            " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }

                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        requestObject1.toString()
                    )
                    val call = apiService.getLoginLogin(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                            call: retrofit2.Call<String>, response:
                            Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {
                                    val jobjt = jObject.getJSONObject("ModuleList")
                                    if (jobjt.getString("ModuleListDetails") == "null") {
                                    } else {
                                        val jarray = jobjt.getJSONArray("ModuleListDetails")
                                        array_sort = java.util.ArrayList<ModuleModel>()
                                        searchModuleArrayList = ArrayList<ModuleModel>()
                                        for (k in 0 until jarray.length()) {
                                            val jsonObject = jarray.getJSONObject(k)

                                            searchModuleArrayList.add(
                                                ModuleModel(
                                                    jsonObject.getString("ModuleName"),
                                                    jsonObject.getString("SubModule"),
                                                    jsonObject.getString("TransType"),
                                                    jsonObject.getString("Groupid"),
                                                    jsonObject.getString("ID_ModuleSettings")
                                                )
                                            )
                                            array_sort.add(
                                                ModuleModel(
                                                    jsonObject.getString("ModuleName"),
                                                    jsonObject.getString("SubModule"),
                                                    jsonObject.getString("TransType"),
                                                    jsonObject.getString("Groupid"),
                                                    jsonObject.getString("ID_ModuleSettings")
                                                )
                                            )
                                        }

                                        sadapter = ModuleListAdapter(
                                            this@AgentCollectionReportActivity,
                                            array_sort
                                        )
                                        list_view!!.setAdapter(sadapter)
                                        list_view!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                                            array_sort.get(position).ModuleName
                                            edt_txt_selecmdl!!.setText(array_sort[position].ModuleName)
                                            strSubModule = array_sort[position].SubModule
//                                            strID_ModuleSettings = array_sort[position].ID_ModuleSettings
                                            //strBranchTypecode=array_sort[position].BranchCode
                                            dialog.dismiss()
                                        })
                                    }
                                    etxtsearch!!.addTextChangedListener(object : TextWatcher {
                                        override fun afterTextChanged(p0: Editable?) {
                                        }

                                        override fun beforeTextChanged(
                                            p0: CharSequence?,
                                            p1: Int,
                                            p2: Int,
                                            p3: Int
                                        ) {
                                        }

                                        override fun onTextChanged(
                                            p0: CharSequence?,
                                            p1: Int,
                                            p2: Int,
                                            p3: Int
                                        ) {

                                            list_view!!.setVisibility(View.VISIBLE)
                                            var textlength = etxtsearch!!.text.length
                                            array_sort.clear()
                                            for (i in searchModuleArrayList.indices) {
                                                if (textlength <= searchModuleArrayList[i].ModuleName!!.length) {
                                                    if (searchModuleArrayList[i].ModuleName!!.toLowerCase()
                                                            .trim().contains(
                                                                etxtsearch!!.text.toString()
                                                                    .toLowerCase()
                                                                    .trim { it <= ' ' })
                                                    ) {
                                                        array_sort.add(searchModuleArrayList[i])
                                                    }
                                                }
                                            }
                                            sadapter = ModuleListAdapter(
                                                this@AgentCollectionReportActivity,
                                                array_sort
                                            )
                                            list_view!!.adapter = sadapter
                                        }
                                    })


                                } else if (jObject.getString("StatusCode") == "-12") {
                                    val jobjt = jObject.getJSONObject("ModuleList")
                                    val dialogBuilder = AlertDialog.Builder(
                                        this@AgentCollectionReportActivity,
                                        R.style.MyDialogTheme
                                    )
                                    dialogBuilder.setMessage(jobjt.getString("ResponseMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "OK",
                                            DialogInterface.OnClickListener { dialog, id ->
                                                dialog.dismiss()

                                            })
                                        .setCancelable(false);
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                } else {
                                    // val jobjt = jObject.getJSONObject("LogInfo")
                                    val dialogBuilder = AlertDialog.Builder(
                                        this@AgentCollectionReportActivity,
                                        R.style.MyDialogTheme
                                    )
                                    dialogBuilder.setMessage(jObject.getString("EXMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "OK",
                                            DialogInterface.OnClickListener { dialog, id ->
                                                dialog.dismiss()

                                            })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                val mySnackbar = Snackbar.make(
                                    findViewById(R.id.rl_main),
                                    " Some technical issues.", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })

                } catch (e: Exception) {
                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(
                        findViewById(R.id.rl_main),
                        " Some technical issues.", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }
            }
            false -> {
                val mySnackbar = Snackbar.make(
                    findViewById(R.id.rl_main),
                    "No Internet Connection!!",
                    Snackbar.LENGTH_SHORT
                )
                mySnackbar.show()
            }
        }

    }





    fun dateSelector() {
        try {
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        fromdate = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                        dateForSearch = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()

                        txtFrom!!.text = fromdate
                    },
                    year,
                    month,
                    day
            )
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

//    private fun getMdls() {
//        try {
//            val builder = AlertDialog.Builder(this)
//            val inflater1 = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            val layout = inflater1.inflate(R.layout.module_selection_layout, null)
//            val listView = layout.findViewById<ListView>(R.id.listViewmdl)
//            builder.setView(layout)
//            val alertDialog = builder.create()
//            val adapter = ArrayAdapter<String>(this, R.layout.list_account, R.id.tvtitle,
//                    arrayForSpinner!!
//            )
//            listView.adapter = adapter
//            listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
//                        // TODO Auto-generated method stub
//                        val value = adapter.getItem(position)
//                        txtv_selecmdl.text = value
//                        if (position == 0) {
//                            modules = "A"
//                        }
//                        if (position == 1) {
//                            modules = "DD"
//                        }
//                        if (position == 2) {
//                            modules = "SB"
//                        }
//
//                        if (position == 3) {
//                            modules = "RD"
//                        }
//                        if (position == 4) {
//                            modules = "GD"
//                        }
//                        alertDialog.dismiss()
//                    }
//            alertDialog.show()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }


    private fun getAgentcollection( from1: String, s1: String?, s2: String?, s3: String?){
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@AgentCollectionReportActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
                try{
                    val client = OkHttpClient.Builder()
                        .sslSocketFactory(getSSLSocketFactory())
                        .hostnameVerifier(getHostnameVerifier())
                        .build()
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()
                    val retrofit = Retrofit.Builder()
                        .baseUrl(ApiService.BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build()

                    val apiService = retrofit.create(ApiInterface::class.java!!)
                    val requestObject1 = JSONObject()
                    try {
//                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
//                        var Imei = DeviceAppDetails.imei
//                        if (Imei != null && !Imei.isEmpty()) {
//                        }else{
//                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
//                            Imei = DeviceAppDetails1.imei
//                        }

                        val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                        var Imei = DeviceAppDetails1.imei

                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val agentId = AgentIdSP.getString("Agent_ID", null)
//                        val from = simpleDateFormat.format(calendar.time)
//
//                        val day = calendar3[Calendar.DAY_OF_MONTH]
//                        val month = calendar3[Calendar.MONTH]
//                        val year = calendar3[Calendar.YEAR]
//                        val date = day.toString() + "-" + (month + 1) + "-" + year
//                        val to = simpleDateFormat.format(calendar3.time)
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("Module", BizcoreApplication.encryptMessage(strSubModule))
                        requestObject1.put("FromDate", BizcoreApplication.encryptMessage(dateForSearch))
                        requestObject1.put("ToDate", BizcoreApplication.encryptMessage(dateForSearch))
                        requestObject1.put("TransType", BizcoreApplication.encryptMessage("R"))
                        if (from1 == "sort") {
                            requestObject1.put("VoucherNumber", null)
                            requestObject1.put("AccountNumber", null)
                            requestObject1.put("Name", null)
                            requestObject1.put("MinAmount", BizcoreApplication.encryptMessage(s1))
                            requestObject1.put("MaxAmount", BizcoreApplication.encryptMessage(s2))
                        }
                        else if (from1 == "filter") {
                            if (s1 == "") {
                                requestObject1.put("VoucherNumber", null)
                                requestObject1.put("AccountNumber", BizcoreApplication.encryptMessage(s2))
                                requestObject1.put("Name", BizcoreApplication.encryptMessage(s3))
                            }
                            else if (s2 == "") {
                                requestObject1.put("VoucherNumber", BizcoreApplication.encryptMessage(s1))
                                requestObject1.put("AccountNumber", null)
                                requestObject1.put("Name", BizcoreApplication.encryptMessage(s3))
                            }
                            else if (s3 == "") {
                                requestObject1.put("VoucherNumber", BizcoreApplication.encryptMessage(s1))
                                requestObject1.put("AccountNumber", BizcoreApplication.encryptMessage(s2))
                                requestObject1.put("Name", null)
                            }
                            else if (s1 == "" && s2 == "") {
                                requestObject1.put("VoucherNumber", null)
                                requestObject1.put("AccountNumber", null)
                                requestObject1.put("Name", BizcoreApplication.encryptMessage(s3))
                            }
                            else if (s1 == "" && s3 == "") {
                                requestObject1.put("VoucherNumber", null)
                                requestObject1.put("AccountNumber", BizcoreApplication.encryptMessage(s2))
                                requestObject1.put("Name", null)
                            }
                            else if (s2 == "" && s3 == "") {
                                requestObject1.put("VoucherNumber", BizcoreApplication.encryptMessage(s1))
                                requestObject1.put("AccountNumber", null)
                                requestObject1.put("Name", null)
                            }
                            else {
                                requestObject1.put("VoucherNumber", BizcoreApplication.encryptMessage(s1))
                                requestObject1.put("AccountNumber", BizcoreApplication.encryptMessage(s2))
                                requestObject1.put("Name", BizcoreApplication.encryptMessage(s3))
                            }

                            requestObject1.put("MinAmount", BizcoreApplication.encryptMessage("0"))
                            requestObject1.put("MaxAmount", BizcoreApplication.encryptMessage("0"))
                        }
                        else if (from1 == "main") {
                            requestObject1.put("VoucherNumber", null)
                            requestObject1.put("AccountNumber", null)
                            requestObject1.put("Name", null)
                            requestObject1.put("MinAmount", BizcoreApplication.encryptMessage("0"))
                            requestObject1.put("MaxAmount", BizcoreApplication.encryptMessage("0"))
                        }
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                    }
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getAgentCollectionList(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                val jmember = jObject.getJSONObject("AgentCollectionList")
                                val statuscode = jObject.getString("StatusCode")

                                if (statuscode == "0") {
                                    val array = jmember.getJSONArray("AgentCollectionListDetails")
                                    val lLayout = GridLayoutManager(this@AgentCollectionReportActivity, 1)
                                    mRcvAgentColectn!!.layoutManager = lLayout as RecyclerView.LayoutManager?
                                    mRcvAgentColectn!!.setHasFixedSize(true)
                                    val adapter = AgentCollectionReportAdapter(this@AgentCollectionReportActivity, array)
                                    mRcvAgentColectn!!.adapter = adapter


                                    lnr_layout1!!.visibility = View.VISIBLE
//                                    crdView1!!.visibility = View.VISIBLE
                                    rcv_agent_colectn!!.visibility = View.VISIBLE
                                }
                                else {
                                    lnr_layout1!!.visibility = View.GONE
//                                    crdView1!!.visibility = View.GONE
                                    rcv_agent_colectn!!.visibility = View.GONE
                                    if (statuscode == "-1"){
                                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main), jObject.getString("EXMessage"), Snackbar.LENGTH_SHORT)
                                        mySnackbar.show()
                                    }
                                    else{
                                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main), jObject.getString("EXMessage"), Snackbar.LENGTH_SHORT)
                                        mySnackbar.show()
                                    }

                                }
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                e.printStackTrace()
                            }
                        }
                        override fun onFailure(call: retrofit2.Call<String>, t:Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })
                } catch (e: Exception) {
                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                        " Some technical issues.", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }
            }
            false -> {
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),"No Internet Connection!!", Snackbar.LENGTH_SHORT)
                mySnackbar.show()
            }
        }
    }


    private fun getHostnameVerifier(): HostnameVerifier {
        return HostnameVerifier { hostname, session -> true }
    }

    private fun getWrappedTrustManagers(trustManagers: Array<TrustManager>): Array<TrustManager> {
        val originalTrustManager = trustManagers[0] as X509TrustManager
        return arrayOf(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return originalTrustManager.acceptedIssuers
            }
            override fun checkClientTrusted(certs: Array<X509Certificate>?, authType: String) {
                try {
                    if (certs != null && certs.size > 0) {
                        certs[0].checkValidity()
                    } else {
                        originalTrustManager.checkClientTrusted(certs, authType)
                    }
                } catch (e: CertificateException) {
                    Log.w("checkClientTrusted", e.toString())
                }
            }
            override fun checkServerTrusted(certs: Array<X509Certificate>?, authType: String) {
                try {
                    if (certs != null && certs.size > 0) {
                        certs[0].checkValidity()
                    } else {
                        originalTrustManager.checkServerTrusted(certs, authType)
                    }
                } catch (e: CertificateException) {
                    Log.w("checkServerTrusted", e.toString())
                }
            }
        })
    }
    @Throws(
        CertificateException::class,
        KeyStoreException::class,
        IOException::class,
        NoSuchAlgorithmException::class,
        KeyManagementException::class
    )
    private fun getSSLSocketFactory(): SSLSocketFactory {
        val cf = CertificateFactory.getInstance("X.509")
        val caInput = applicationContext.assets.open(ApiService.CERT_NAME)
        val ca = cf.generateCertificate(caInput)
        caInput.close()
        val keyStore = KeyStore.getInstance("BKS")
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", ca)
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)
        val wrappedTrustManagers = getWrappedTrustManagers(tmf.trustManagers)
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, wrappedTrustManagers, null)
        return sslContext.socketFactory
    }


    override fun onItemSelected(
        adapterView: AdapterView<*>?,
        view: View?,
        i: Int,
        l: Long
    ) {
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
}