package com.perfect.nbfc.launchingscreens.MainHome

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.perfect.nbfc.AgentReport.Summary.Activity.AgentSummaryActivity
import com.perfect.nbfc.Api.ApiInterface
import com.perfect.nbfc.Api.ApiService
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.Helper.ConnectivityUtils
import com.perfect.nbfc.Helper.CryptoGraphy
import com.perfect.nbfc.Helper.DeviceAppDetails
import com.perfect.nbfc.Offline.Model.AccountModel
import com.perfect.nbfc.Offline.Model.TransactionModel
import com.perfect.nbfc.R
import com.perfect.nbfc.launchingscreens.MPIN.MPINActivity
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONArray
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
import android.os.CountDownTimer as CountDownTimer1

class HomeActivity : AppCompatActivity() {

    internal var accountlist = ArrayList<AccountModel>()
    private var result:Boolean? = null
    private var hashString:String? = null
    lateinit var dbHelper : DBHandler
    private var uniquerefid      : String?         = null
    internal var transactionlist = ArrayList<TransactionModel>()
    lateinit var handler: Handler
    lateinit var r: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = DBHandler(this)
        //loadData()
        //dbHelper.deleteallAccount()
        accountlist = ArrayList(dbHelper.readAllUsers())
        accountlist.toString()
        try {
            val gson = Gson()
            val listString = gson.toJson(accountlist, object : TypeToken<ArrayList<AccountModel>>() {

            }.type)
            val jarray = JSONArray(listString)
        } catch (e: Exception) {
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home,
            R.id.navigation_about,
            R.id.navigation_reset,
            R.id.navigation_logout,
            R.id.navigation_quit
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        handler = Handler()
        r = Runnable {
           /* val intent= Intent(this, MPINActivity::class.java)
            startActivity(intent)
            finish()*/
        }
        startHandler()
    }

    override fun onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction()
        stopHandler()//stop first and then start
        startHandler()
    }

    fun stopHandler() {
        handler.removeCallbacks(r)
    }

    fun startHandler() {
        handler.postDelayed(r, 5 * 60 * 1000) //for 5 minutes
    }

    override fun onBackPressed() {
        quitApp()
    }

    private fun quitApp() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.quit_app_popup, null)
        dialogBuilder.setView(dialogView)

       val quit_app_ok = dialogView.findViewById<LinearLayout>(R.id.quit_app_ok)
       val quit_app_cancel = dialogView.findViewById<LinearLayout>(R.id.quit_app_cancel)

        val b = dialogBuilder.create()
        quit_app_ok.setOnClickListener {
            finish()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity()
            }
        }
        quit_app_cancel.setOnClickListener {
        b.dismiss()
        }
        b.setCancelable(false)
        b.show()
    }


    private fun loadData(){
        when(ConnectivityUtils.isConnected(this)) {
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

                        val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                        var Imei = DeviceAppDetails1.imei
                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val  agentId = AgentIdSP.getString("Agent_ID", null)
                        var  deviceAppDetails : DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails( this )
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
                        val dateTime = simpleDateFormat.format(calendar.time)
                        val hashList = ArrayList<String>()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(AgentIdSP.getString("Agent_ID", null).toString())
                        hashString = CryptoGraphy.getInstance().hashing(hashList)
                        val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
                        val  token = tokenSP.getString("token", null)
                        hashString += token
                        val hashToken = "06"+hashString/*+token*/
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashToken))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("From_Module", BizcoreApplication.encryptMessage("PDDD"))
                        requestObject1.put("Version_code", BizcoreApplication.encryptMessage(Integer.toString(deviceAppDetails.getAppVersion())))
                        requestObject1.put( BizcoreApplication.SYSTEM_TRACE_AUDIT_NO, BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put(  BizcoreApplication.CURRENT_DATE, BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                    } catch (e: Exception) {e.printStackTrace() }
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getOfflineAccounts(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {
                                    dbHelper.deleteallAccount()
                                    val jarray = jObject.getJSONArray("CusSyncInfo")
                                    for (i in 0..jarray.length()) {
                                        print(i)
                                        val jsonObject=jarray.getJSONObject(i)
                                         result =  dbHelper.insertUser(AccountModel(accountid = (i+1).toString(),
                                            shortname = jsonObject.getString("ShortName"),depositno = jsonObject.getString("DepositNumber"),
                                            customername = jsonObject.getString("CusName"),balance = jsonObject.getString("BalanceAmount"),
                                            deposittype = jsonObject.getString("DepositType"),module = jsonObject.getString("Module"),
                                            depositdate ="29-11-2019"/* jsonObject.getString("DepositDate") */))
//                                        if(result==true){
//                                            Toast.makeText(applicationContext,"Success",Toast.LENGTH_LONG).show()
//                                        }else{
//                                            Toast.makeText(applicationContext,"Fail",Toast.LENGTH_LONG).show()}
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace() }
                        }
                        override fun onFailure(call: retrofit2.Call<String>, t:Throwable) {}
                    })
                } catch (e: Exception) {e.printStackTrace()}
            }
            false -> {}
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
        val caInput =
            applicationContext.assets.open(ApiService.CERT_NAME)
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

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)

    }




}
