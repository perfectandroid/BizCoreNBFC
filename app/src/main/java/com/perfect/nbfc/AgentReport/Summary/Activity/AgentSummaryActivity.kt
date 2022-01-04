package com.perfect.nbfc.AgentReport.Summary.Activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.perfect.nbfc.AgentReport.Summary.Adapter.SummaryModuleAdapter
import com.perfect.nbfc.Api.ApiInterface
import com.perfect.nbfc.Api.ApiService
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.Helper.ConnectivityUtils
import com.perfect.nbfc.Helper.CryptoGraphy
import com.perfect.nbfc.R
import com.perfect.nbfc.launchingscreens.Login.LoginActivity
import com.perfect.nbfc.launchingscreens.MPIN.MPINActivity
import kotlinx.android.synthetic.main.activity_agent_summary.*
import kotlinx.android.synthetic.main.activity_agent_summary.tv_date
import kotlinx.android.synthetic.main.activity_agent_summary.tv_holder_name
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


class AgentSummaryActivity : AppCompatActivity(), View.OnClickListener {

    private var mYear: Int =0
    private var mMonth:Int = 0
    private var mDay:Int = 0
    private var progressDialog  : ProgressDialog? = null
    private var simpleDateFormat : SimpleDateFormat? =null
    private var simpleDateFormat1 : SimpleDateFormat? =null
    private var etdate      : TextView?         = null
    private var select_date_search      : ImageView?         = null
    private var imback      : ImageView?         = null
    private var hashString      : String?         = null
    private var dateTime      : String?         = null
    private var iAmount: Int? =null
    val calendar = Calendar.getInstance()
    lateinit var dbHelper : DBHandler
    lateinit var handler: Handler
    lateinit var r: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agent_summary)
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        simpleDateFormat1 = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        dateTime = simpleDateFormat!!.format(calendar.time)
        imback = findViewById(R.id.imback) as ImageView
        select_date_search = findViewById(R.id.select_date_search) as ImageView
        etdate = findViewById(R.id.etdate) as TextView
        etdate!!.setOnClickListener(this)
        select_date_search!!.setOnClickListener(this)
        imback!!.setOnClickListener(this)
        etdate!!.text=simpleDateFormat1!!.format(calendar.time)
        view()
        getSummary(dateTime!!)
        handler = Handler()
        r = Runnable {
           /* val intent= Intent(this, MPINActivity::class.java)
            startActivity(intent)
            finish()*/
        }
        startHandler()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        stopHandler()
        startHandler()
    }

    fun stopHandler() {
        handler.removeCallbacks(r)
    }

    fun startHandler() {
        handler.postDelayed(r, 5 * 60 * 1000)
    }

    private fun view(){
        val AgentName = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF2,0)
        val AgentPhoneNumber = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF3,0)
        tv_holder_name!!.text = AgentName.getString("Agent_Name", null)+" [ "+
                AgentPhoneNumber.getString("CusMobile", null)+" ]"

    }

    private fun getSummary(datetime: String) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@AgentSummaryActivity, R.style.Progress)
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
//                        val calendar = Calendar.getInstance()
                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {
                        }else{
                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                            Imei = DeviceAppDetails1.imei
                        }
                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
                        val token = tokenSP.getString("token", null)
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val hashList = ArrayList<String>()
                        hashList.add( Imei )
                        hashList.add( datetime )
                        hashList.add( randomNumber )
                        hashList.add( AgentIdSP.getString("Agent_ID", null).toString() )
                        hashString = CryptoGraphy.getInstance().hashing( hashList )
                        hashString = "06"+hashString+token
                        requestObject1.put("Processing_Code", BizcoreApplication.encryptMessage("060000"))
                        requestObject1.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("TransDate", BizcoreApplication.encryptMessage(datetime))
                        requestObject1.put("CurrentDate", BizcoreApplication.encryptMessage(datetime))
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                    }
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getAgentSummary(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                val jobjt = jObject.getJSONObject("AgntSummaryInfo")
                                if (jObject.getString("StatusCode") == "0") {
                                    val jarray = jobjt.getJSONArray("AgentsummList")
                                    iAmount=0
                                    for (i in 0..jarray.length()-1) {
                                        val jsonObject=jarray.getJSONObject(i)
                                        if(jsonObject.getString("Module").equals("") || (jsonObject.getString("Module").length<=0)) {

                                            ll_mod.visibility=GONE
                                            ll_sum.visibility=GONE
//                                            tv_openbal.text="₹ " +jobjt.getString("OpBalance")
//                                            tv_curbal.text="₹ " +jobjt.getString("Closing")

                                            val dialogBuilder = AlertDialog.Builder(this@AgentSummaryActivity, R.style.MyDialogTheme)
                                            dialogBuilder.setMessage("No Data Found On Selected Date.")
//                                            dialogBuilder.setMessage(jobjt.getString("ResponseMessage"))
                                                .setCancelable(false)
                                                .setPositiveButton("OK", DialogInterface.OnClickListener {
                                                        dialog, id ->
                                                    dialog.dismiss()
                                                })
                                            val alert = dialogBuilder.create()
                                            alert.show()
                                            val nbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                            nbutton.setTextColor(Color.MAGENTA)

                                        }
                                        else{
                                            ll_mod.visibility= VISIBLE
                                            ll_sum.visibility=VISIBLE
                                        }
                                        if(jsonObject.getString("TransType").equals("R")) {
                                            iAmount = iAmount!! + jsonObject.getInt("Amount")
                                        }
                                        tv_receipt.text = "₹ " +iAmount.toString()+".00 Cr"
                                    }
                                    tv_openbal.text="₹ " +jobjt.getString("OpBalance")
                                    tv_curbal.text="₹ " +jobjt.getString("Closing")
                                    val lLayout = GridLayoutManager(this@AgentSummaryActivity, 1)
                                    rvsummmarymodule.layoutManager = lLayout as RecyclerView.LayoutManager?
                                    rvsummmarymodule.setHasFixedSize(true)
                                    val adapter = SummaryModuleAdapter(this@AgentSummaryActivity, jarray)
                                    rvsummmarymodule.adapter = adapter
                                }
//                                else if (jObject.getString("StatusCode") == "500") {
//                                    ll_mod.visibility=GONE
//                                    ll_rcpt.visibility=GONE
//                                    ll_sum.visibility=VISIBLE
//                                    tv_openbal.text="₹ " +jobjt.getString("OpBalance")
//                                    tv_curbal.text="₹ " +jobjt.getString("Closing")
//                                }
                                else {
                                    val dialogBuilder = AlertDialog.Builder(this@AgentSummaryActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jObject.getString("EXMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", DialogInterface.OnClickListener {
                                                dialog, id -> dialog.dismiss()
                                            doLogout()
                                        })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
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

    private fun doLogout() {
        try {
            val loginSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF,0)
            val loginEditer = loginSP.edit()
            loginEditer.putString("loginsession", "No")
            loginEditer.commit()
            val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
            val AgentIdEditor = AgentIdSP.edit()
            AgentIdEditor.putString("Agent_ID", "")
            AgentIdEditor.commit()
            val Agent_NameSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF2, 0)
            val Agent_NameEditer = Agent_NameSP.edit()
            Agent_NameEditer.putString("Agent_Name", "")
            Agent_NameEditer.commit()
            val CusMobileSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF3, 0)
            val CusMobileEditer = CusMobileSP.edit()
            CusMobileEditer.putString("CusMobile", "")
            CusMobileEditer.commit()
            val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
            val tokenEditer = tokenSP.edit()
            tokenEditer.putString("token", "")
            tokenEditer.commit()
            val UserName = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF5, 0)
            val UserNameEditor = UserName.edit()
            UserNameEditor.putString("username", "")
            UserNameEditor.commit()
            val transactionIDSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF6, 0)
            val transactionIDEditor = transactionIDSP.edit()
            transactionIDEditor.putString("Transaction_ID", "1")
            transactionIDEditor.commit()
            val archiveIDSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF7, 0)
            val archiveIDEditor = archiveIDSP.edit()
            archiveIDEditor.putString("Archive_ID", "1")
            archiveIDEditor.commit()
            val loginTimeSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF8, 0)
            val loginTimeEditer = loginTimeSP.edit()
            loginTimeEditer.putString("logintime", "")
            loginTimeEditer.commit()
            dbHelper = DBHandler(this)
            dbHelper.deleteallAccount()
            dbHelper.deleteallTransaction()
            dbHelper.deleteAllArchieve()
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
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

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.etdate->{
                dateSelector()
            }
            R.id.select_date_search->{
                getSummary(dateTime!!)
            }
            R.id.imback->{
               onBackPressed()
            }
        }
    }

    fun dateSelector() {
        try {
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    etdate!!.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    dateTime = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()
                    getSummary(dateTime!!)
                }, mYear, mMonth, mDay
            )
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

}
