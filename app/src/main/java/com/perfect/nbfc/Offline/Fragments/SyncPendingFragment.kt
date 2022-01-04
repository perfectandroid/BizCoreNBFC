package com.perfect.nbfc.Offline.Fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.perfect.nbfc.Api.ApiInterface
import com.perfect.nbfc.Api.ApiService
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.Helper.ConnectivityUtils
import com.perfect.nbfc.Helper.CryptoGraphy
import com.perfect.nbfc.Helper.DeviceAppDetails
import com.perfect.nbfc.Offline.Activity.CollectionDetailsActivity
import com.perfect.nbfc.Offline.Adapter.SyncPendingAdapter
import com.perfect.nbfc.Offline.Model.AccountModel
import com.perfect.nbfc.Offline.Model.ArchiveModel
import com.perfect.nbfc.Offline.Model.TransactionModel
import com.perfect.nbfc.R
import com.perfect.nbfc.balanceEnquiry.SelectAccountActivity
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

class SyncPendingFragment : Fragment(), View.OnClickListener {

    private var progressDialog  : ProgressDialog? = null
    private var hashString      : String?         = null
    private var uniquerefid      : String?         = null
    internal var transactionlist = ArrayList<TransactionModel>()
    lateinit var dbHelper : DBHandler
    private var rvSync: RecyclerView? = null
    private var ll_syncnow: LinearLayout? = null
    private var lnrReport: LinearLayout? = null
    private var result:Boolean? = null

    private var acheiveid:String? = null
    private var intId: Int?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        val root = inflater.inflate(R.layout.fragment_syncpending, container, false)
        rvSync =root.findViewById(R.id.rvSync)
        ll_syncnow =root.findViewById(R.id.ll_syncnow)
        lnrReport =root.findViewById(R.id.lnrReport)
        ll_syncnow!!.setOnClickListener(this)
        dbHelper = DBHandler(context!! )
        if(dbHelper.selectTransactionCount()>0){
            ll_syncnow!!.visibility=(VISIBLE)
            lnrReport!!.visibility=(VISIBLE)
        }
        else{
            ll_syncnow!!.visibility=(GONE)
            lnrReport!!.visibility=(GONE)
            val dialogBuilder = AlertDialog.Builder(context, R.style.MyDialogTheme)
            dialogBuilder.setMessage("No data found in Sync Pending.")
                .setCancelable(false)
                .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, id -> dialog.dismiss()
                })
            val alert = dialogBuilder.create()
            alert.show()
            val nbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
            nbutton.setTextColor(Color.MAGENTA)
        }
        syncPendingdata()
        return root
    }

    private fun syncPendingdata() {
        transactionlist = ArrayList(dbHelper.readAllTransactions())
        val gson = Gson()
        val listString = gson.toJson(transactionlist, object : TypeToken<ArrayList<TransactionModel>>() {}.type)
        val jarray = JSONArray(listString)
        val lLayout = GridLayoutManager(context, 1)
        rvSync!!.layoutManager = lLayout
        rvSync!!.setHasFixedSize(true)
        val adapter = SyncPendingAdapter(context!!, jarray)
        rvSync!!.adapter = adapter
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ll_syncnow->{
                syncData()
            }
        }
    }

    private fun syncData(){
        when(ConnectivityUtils.isConnected(context!!)) {
            true -> {
                try{
                    progressDialog = ProgressDialog(context, R.style.Progress)
                    progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                    progressDialog!!.setCancelable(false)
                    progressDialog!!.setIndeterminate(true)
                    progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                    progressDialog!!.show()
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
                                        "CollectionDate",BizcoreApplication.encryptMessage("2019-11-27 00:00:00")
                                       /* cursor.getString(cursor.getColumnIndex("depositdate"))*/
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
                                        dbHelper = DBHandler(context!! )
                                        uniquerefid=jsonObject.getString("UniqueRefNo")
                                        transactionlist = ArrayList(dbHelper.readTransactions(uniquerefid!!))
                                        val gson = Gson()
                                        val listString = gson.toJson(transactionlist, object : TypeToken<ArrayList<TransactionModel>>() {}.type)
                                        val jsnarray = JSONArray(listString)
                                     //   for (j in 0..jsnarray.length()) {
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
                                          /*  if (result == true) {
                                                Toast.makeText(
                                                    context,
                                                    "Success",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                                Toast.makeText(context, "Fail", Toast.LENGTH_LONG)
                                                    .show()
                                            }*/
                                       // }

                                    }

                                    dbHelper.deleteallTransaction()
                                    progressDialog!!.dismiss()


                                    val dialogBuilder = AlertDialog.Builder(context, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage("Date Synchronised  Successfully.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", DialogInterface.OnClickListener {
                                                dialog, id -> refreshcollectiondata()
                                        })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val nbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    nbutton.setTextColor(Color.MAGENTA)

                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                progressDialog!!.dismiss()}
                        }
                        override fun onFailure(call: retrofit2.Call<String>, t:Throwable) {progressDialog!!.dismiss()}
                    })
                } catch (e: Exception) {e.printStackTrace()}
            }
            false -> {
                val dialogBuilder = AlertDialog.Builder(context, R.style.MyDialogTheme)
                dialogBuilder.setMessage("No Internet Connection, Please try later.")
                    .setCancelable(false)
                    .setPositiveButton("OK", DialogInterface.OnClickListener {
                            dialog, id -> dialog.dismiss()
                    })
                val alert = dialogBuilder.create()
                alert.show()
                val nbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                nbutton.setTextColor(Color.MAGENTA)
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
        //  InputStream caInput = getResources().openRawResource(Common.getCertificateAssetName());
        // File path: app\src\main\res\raw\your_cert.cer
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

}