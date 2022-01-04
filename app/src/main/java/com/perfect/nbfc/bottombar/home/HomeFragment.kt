package com.perfect.nbfc.bottombar.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.perfect.nbfc.AgentCollectionReport.AgentCollectionReportActivity
import com.perfect.nbfc.AgentReport.Balance.AgentBalanceActivity
import com.perfect.nbfc.AgentReport.Summary.Activity.AgentSummaryActivity
import com.perfect.nbfc.Api.ApiInterface
import com.perfect.nbfc.Api.ApiService
import com.perfect.nbfc.Common.CustomerSearchActivity
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Demandlist.Demandlistactivity
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.Helper.ConnectivityUtils
import com.perfect.nbfc.Helper.CryptoGraphy
import com.perfect.nbfc.Helper.DeviceAppDetails
import com.perfect.nbfc.Offline.Activity.CollectionDetailsActivity
import com.perfect.nbfc.Offline.Activity.NewCollectionActivity
import com.perfect.nbfc.Offline.Model.ArchiveModel
import com.perfect.nbfc.Offline.Model.TransactionModel
import com.perfect.nbfc.R
import com.perfect.nbfc.Todolist.Activity.TodoListActivity
import kotlinx.android.synthetic.main.fragment_home.*
import me.relex.circleindicator.CircleIndicator
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
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
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), View.OnClickListener {

    private var result:Boolean? = null
    private var hashString      : String?         = null
    private var uniquerefid      : String?         = null
    internal var transactionlist = ArrayList<TransactionModel>()
    lateinit var dbHelper : DBHandler
    lateinit var agent:String
    lateinit var loginTime:String
    private lateinit var homeViewModel: HomeViewModel
    private var tvUsername: TextView? = null
    private var tvtime: TextView? = null
    private var llCollection: LinearLayout? = null
    private var llCusStatement: LinearLayout? = null
    private var llBalEnq: LinearLayout? = null
    private var llTodolist: LinearLayout? = null
    private var llDemandlist: LinearLayout? = null
    private var llAgentCollection: LinearLayout? = null
    private var mPager: ViewPager? = null
    private var indicator: CircleIndicator? = null
    private var currentPage = 0
    private val XMEN = arrayOf<Int>(R.drawable.ban1, R.drawable.ban2, R.drawable.ban3, R.drawable.ban4)
    private val XMENArray = ArrayList<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =ViewModelProviders.of(this).get(HomeViewModel::class.java)
        (activity as AppCompatActivity).supportActionBar!!.hide()
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        mPager = root.findViewById(R.id.pager)
        indicator =root.findViewById(R.id.indicator)
        llCollection =root.findViewById(R.id.llCollection)
        llCusStatement =root.findViewById(R.id.llCusStatement)
        llBalEnq =root.findViewById(R.id.llBalEnq)
        llTodolist =root.findViewById(R.id.llTodolist)
        tvUsername =root.findViewById(R.id.tvUsername)
        tvtime =root.findViewById(R.id.tvtime)
        llAgentCollection =root.findViewById(R.id.llAgentCollection)
        llDemandlist =root.findViewById(R.id.llDemandlist)

        llCollection!!.setOnClickListener(this)
        llCusStatement!!.setOnClickListener(this)
        llBalEnq!!.setOnClickListener(this)
        llAgentCollection!!.setOnClickListener(this)
        llTodolist!!.setOnClickListener(this)
        llDemandlist!!.setOnClickListener(this)
        init()
        val AgentName    = context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF2,0)
        agent   = AgentName.getString("Agent_Name", null)!!
        if(agent!=null){
            tvUsername!!.text = "Welcome "+agent+" !"
        }
        val Lastlogintime    = context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF8,0)
        loginTime   = Lastlogintime.getString("logintime", null)!!
        if(loginTime!=null){
            tvtime!!.text = "Last Login Time: "+loginTime
        }
        dbHelper = DBHandler(context!! )
        syncData()
        return root
    }
    override fun onClick(v: View) {
        when(v.id){
            R.id.llCollection->{
                val intent= Intent(context, CustomerSearchActivity::class.java)
                intent.putExtra("from", "Collection")
                startActivity(intent)
            }
            R.id.llTodolist->{
                val intent= Intent(context, TodoListActivity::class.java)
                startActivity(intent)
            }
            R.id.llDemandlist->{
                val intent= Intent(context, CustomerSearchActivity::class.java)
                intent.putExtra("from", "DemandList")
                startActivity(intent)
            }
//            {
//                val intent= Intent(context, Demandlistactivity::class.java)
//                startActivity(intent)
//            }
            R.id.llCusStatement->{
                val intent= Intent(context, CustomerSearchActivity::class.java)
                intent.putExtra("from", "CusStatement")
                startActivity(intent)
            }
            R.id.llAgentCollection->{
                agentBalance()
            }
            R.id.llBalEnq-> {
                val intent = Intent(context, CustomerSearchActivity::class.java)
                intent.putExtra("from", "BalanceEnq")
                startActivity(intent)
            }
        }
    }


    private fun agentBalance() {
        val dialog = Dialog(activity!!)
        dialog.setCancelable(true)
        val view = activity!!.layoutInflater.inflate(R.layout.agent_balance_popup, null)
        dialog.setContentView(view)
        val llagentsummary = view.findViewById(R.id.llagentsummary) as LinearLayout
        val llagentcollectionreport = view.findViewById(R.id.llagentcollectionreport) as LinearLayout
        llagentsummary.setOnClickListener {
            val intent= Intent(context, AgentSummaryActivity::class.java)
            startActivity(intent)
        }
        llagentcollectionreport.setOnClickListener {
            val intent= Intent(context, AgentCollectionReportActivity::class.java)
            startActivity(intent)
        }
        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }


    private fun init() {
        for (i in 0 until 4)
            XMENArray.add(XMEN[i])
        mPager!!.adapter = BannerAdapter(context, XMENArray)
        indicator!!.setViewPager(mPager)
        val handler = Handler()
        val Update = Runnable {
            if (currentPage === 4) {
                currentPage = 0
            }
            mPager!!.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 2500, 2500)
    }

    private fun offlineMode() {
        val dialog = Dialog(activity!!)
        dialog.setCancelable(true)
        val view = activity!!.layoutInflater.inflate(R.layout.offlinepopup, null)
        dialog.setContentView(view)
        val llnewcollection = view.findViewById(R.id.llnewcollection) as LinearLayout
        val llcollectiondetails = view.findViewById(R.id.llcollectiondetails) as LinearLayout
        llnewcollection.setOnClickListener {
            val intent= Intent(context, NewCollectionActivity::class.java)
            startActivity(intent)
        }
        llcollectiondetails.setOnClickListener {
            val intent= Intent(context, CollectionDetailsActivity::class.java)
            startActivity(intent)
        }
        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun syncData(){
        when(ConnectivityUtils.isConnected(context!!)) {
            true -> {
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

                        val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(context)
                        var Imei = DeviceAppDetails1.imei

                        val AgentIdSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val  agentId = AgentIdSP.getString("Agent_ID", null)
                        var  deviceAppDetails : DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails( context )
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                        val dateTime = simpleDateFormat.format(calendar.time)
                        val hashList = java.util.ArrayList<String>()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(agentId!!)
                        hashString = CryptoGraphy.getInstance().hashing(hashList)
                        val tokenSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
                        val  token = tokenSP.getString("token", null)
                        hashString += token
                        val hashToken = "06"+hashString/*+token*/
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashToken))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("From_Module", BizcoreApplication.encryptMessage("DD"))
                        requestObject1.put("Version_code", BizcoreApplication.encryptMessage(Integer.toString(deviceAppDetails.getAppVersion())))
                        requestObject1.put( BizcoreApplication.SYSTEM_TRACE_AUDIT_NO, BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put(  BizcoreApplication.CURRENT_DATE, BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                        val jsonArray = JSONArray()
                        val db = DBHandler(context!!)
                        val cursor = db.select("transactiontable")
                        var i = 0
                        if (cursor.moveToFirst()) {
                            do {
                                val jsonObject1 = JSONObject()
                                try {
                                    val custname = db.getCusName(cursor.getString(cursor.getColumnIndex("masterid")))
                                    jsonObject1.put(
                                        "DepositNumber",BizcoreApplication.encryptMessage(""+custname?.depositno)
                                    )
                                    jsonObject1.put(
                                        "DepositType",BizcoreApplication.encryptMessage(""+custname?.deposittype)
                                    )
                                    jsonObject1.put(
                                        "ShortName",BizcoreApplication.encryptMessage(""+custname?.shortname)
                                    )
                                    jsonObject1.put(
                                        "Amount",
                                        BizcoreApplication.encryptMessage(cursor.getString(cursor.getColumnIndex("depositamount")))
                                    )
                                    jsonObject1.put(
                                        "CollectionDate",BizcoreApplication.encryptMessage( cursor.getString(cursor.getColumnIndex("depositdate"))
                                        )
                                    )
                                    jsonObject1.put(
                                        "UniqueRefNo",
                                        BizcoreApplication.encryptMessage(cursor.getString(cursor.getColumnIndex("uniqueid")))
                                    )

                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                                try {
                                    jsonArray.put(i, jsonObject1)
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                                i++
                            } while (cursor.moveToNext())
                        }
                        cursor.close()
                        requestObject1.put("jsondata", jsonArray.toString())
                    } catch (e: Exception) {e.printStackTrace() }
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getTransactionSync(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                val calendar = Calendar.getInstance()
                                val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {
                                    val jarray = jObject.getJSONArray("TrnsSyncInfo")
                                    var i:Int = 0
                                    var size:Int = jarray.length()
                                    for (i in 0..size-1) {
                                        val jsonObject=jarray.getJSONObject(i)
                                        val dateTime = simpleDateFormat.format(calendar.time)
                                        uniquerefid=jsonObject.getString("UniqueRefNo")
                                        transactionlist = ArrayList(dbHelper.readTransactions(uniquerefid!!))
                                        val gson = Gson()
                                        val listString = gson.toJson(transactionlist, object : TypeToken<ArrayList<TransactionModel>>() {}.type)
                                        val jsnarray = JSONArray(listString)
                                        val jObject = jsnarray.getJSONObject(0)
                                        result = dbHelper.insertarcheives(
                                            ArchiveModel(/*acheiveid!!,*/jObject.getString("customername"),
                                                jObject.getString("depositno"),
                                                jObject.getString("depositamount"),
                                                jObject.getString("depositdate"),
                                                dateTime,
                                                jObject.getString("uniqueid"),
                                                jsonObject.getString("ResponseMessage")
                                            )
                                        )
                                    }
                                    dbHelper.deleteallTransaction()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()}
                        }
                        override fun onFailure(call: retrofit2.Call<String>, t:Throwable) {}
                    })
                } catch (e: Exception) {e.printStackTrace()}
            }
            false -> {
                val dialogBuilder = AlertDialog.Builder(context, R.style.MyDialogTheme)
                dialogBuilder.setMessage("No internet connection.")
                        .setCancelable(false)
//                        .setPositiveButton("OK", DialogInterface.OnClickListener {
//                            dialog, id -> doCollection()
//                        })
                        .setNegativeButton("OK", DialogInterface.OnClickListener {
                            dialog, id -> dialog.dismiss()
                        })
                val alert = dialogBuilder.create()
                alert.show()
                val nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                nbutton.setTextColor(Color.MAGENTA)
                val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                pbutton.setTextColor(Color.MAGENTA)
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
        val caInput =context!!.applicationContext.assets.open(ApiService.CERT_NAME)
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

    fun refreshcollectiondata(){
        startActivity(Intent(context, CollectionDetailsActivity::class.java))
        getActivity()!!.finish()
    }

    private fun AgentReport() {
        val dialog = Dialog(activity!!)
        dialog.setCancelable(true)
        val view = activity!!.layoutInflater.inflate(R.layout.offlinepopup, null)
        dialog.setContentView(view)
        val llagentSumm = view.findViewById(R.id.llnewcollection) as LinearLayout
        val agentSumm = view.findViewById(R.id.tvyes) as TextView
        agentSumm.text = "Agent Summary"
        val llagentRepo = view.findViewById(R.id.llcollectiondetails) as LinearLayout
        val agentRepo = view.findViewById(R.id.tvno) as TextView
        agentRepo.text = "Agent Balance"
        llagentSumm.setOnClickListener {
            val intent= Intent(context, AgentSummaryActivity::class.java)
            startActivity(intent)
            dialog.hide()
        }
        llagentRepo.setOnClickListener {
            val intent= Intent(context, AgentBalanceActivity::class.java)
            startActivity(intent)
            dialog.hide()
        }
        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

}