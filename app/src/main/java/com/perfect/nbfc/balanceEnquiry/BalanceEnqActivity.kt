package com.perfect.nbfc.balanceEnquiry

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.perfect.nbfc.Api.ApiInterface
import com.perfect.nbfc.Api.ApiService
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.Helper.ConnectivityUtils
import com.perfect.nbfc.Helper.CryptoGraphy
import com.perfect.nbfc.R
import com.perfect.nbfc.launchingscreens.Login.LoginActivity
import com.perfect.nbfc.launchingscreens.MPIN.MPINActivity
import kotlinx.android.synthetic.main.activity_balance_enq.*
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
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*
import kotlin.collections.ArrayList

class BalanceEnqActivity : AppCompatActivity(),View.OnClickListener{

    private var progressDialog: ProgressDialog? = null
    private var hashString: String? = null
    private var response: String? = null
    private var strModuleValue: String? = null
    private var accountno: String? = null
    private var accounts: String? = null
    private var accountName: String? = null
    private var authid: String? = null
    private var strAmount: String? = null
    private var strMsg: String? = null
    private var  accno:String? = null
    private var strModule: String? = null
    private var strfrom: String? = null
    private var value:String?=null
    var items: ArrayList<String> = ArrayList()
    private var layt_selctacc: LinearLayout? = null
    private var imback: ImageView? = null
    private var ll_dueamnt:LinearLayout?=null
    private var name: String? = null
    var count = 0
    var c=0
    lateinit var dbHelper : DBHandler
    lateinit var handler: Handler
    lateinit var r: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balance_enq)
        val bundle:Bundle = intent.extras!!
        accountName = bundle.get("accountname") as String?
        response = bundle.get("Response") as String?
        authid = bundle.get("AuthID") as String?
        accounts = bundle.get("accountno") as String?
        if(accountName?.contains("|")!!)
        {
            name = accountName?.split("|")?.get(0)
        }else
        {
            name=accountName
        }
     txtv_custname.setText(name);
     initiateViews()
     setRegViews()
     val root = JSONObject(response)
     val jobjt = root.getJSONObject("AccInfo")
     val jobj1 = jobjt.getJSONArray("SourcePrivList")
     for (i in 0 until jobj1.length()) {
         val item = jobj1.getJSONObject(i)
         if ("AC" in item.toString() ) {
         } else if("GL" in item.toString())
         {
         }else if( "OD" in item.toString())
         {
         }else if( "GD" in item.toString())
         {
         }else {
             accno = item.getString("Module") + "-" + item.getString("AccountIdentification")
             items.add(accno!!);
         }
     }
        handler = Handler()
        r = Runnable {
            /*val intent= Intent(this, MPINActivity::class.java)
            startActivity(intent)
            finish()*/
        }
        startHandler()
    }

    override fun onUserInteraction() {
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

    private fun initiateViews() {
        layt_selctacc   = findViewById(R.id.layt_selctacc)
        ll_dueamnt      = findViewById(R.id.ll_dueamnt)
        imback          = findViewById(R.id.imback)
    }

    private fun setRegViews() {
         layt_selctacc?.setOnClickListener(this)
         txtv_viewbal.setOnClickListener(this)
         imback?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
         when (v.id) {
             R.id.layt_selctacc -> {
                 getAccounts()
             }
             R.id.imback -> {
                 finish()
             }
             R.id.txtv_viewbal -> {
                 imgv_viewbal.visibility = View.INVISIBLE
                 if (txtv_selecacc.text.toString().equals("Select Account")){
                     // Toast.makeText(applicationContext,"please select account before proceed.",Toast.LENGTH_LONG).show()
                     val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
                     dialogBuilder.setMessage("please select account before proceed.")
                         .setCancelable(false)
                         .setPositiveButton("OK", DialogInterface.OnClickListener {
                                 dialog, id -> dialog.dismiss()
                         })
                     val alert = dialogBuilder.create()
                     alert.show()
                     val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                     pbutton.setTextColor(Color.MAGENTA)
                 } else {
                     layt_selctacc!!.isEnabled=false
                     if ("SB" in txtv_selecacc.text.toString()) {
                         strModule = "SB"
                         strModuleValue = "10"
                     }
                     if ("DD" in txtv_selecacc.text.toString()) {
                         strModule = "DD"
                         strModuleValue = "21"
                     }
                     if ("RD" in txtv_selecacc.text.toString()) {
                         strModule = "RD"
                         strModuleValue = "22"
                     }
                     if ("GC" in txtv_selecacc.text.toString()) {
                         strModule = "GS"
                         strModuleValue = "23"
                     }
                 }
                 if(!txtv_selecacc.text.toString().equals("Select Account"))
                 {
                     c++
                     if (c==1)
                        getAccountDetails()
                 }
             }
         }
     }

    private fun getAccountDetails() {
     when(ConnectivityUtils.isConnected(this)) {
         true -> {
             progressDialog = ProgressDialog(this@BalanceEnqActivity, R.style.Progress)
             progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
             progressDialog!!.setCancelable(false)
             progressDialog!!.setIndeterminate(true)
             progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
             progressDialog!!.show()
             try{
                 val client1 = OkHttpClient.Builder()
                     .sslSocketFactory(getSSLSocketFactory())
                     .hostnameVerifier(getHostnameVerifier())
                     .build()
                 val gson1 = GsonBuilder()
                     .setLenient()
                     .create()
                 val retrofit1 = Retrofit.Builder()
                     .baseUrl(ApiService.BASE_URL)
                     .addConverterFactory(ScalarsConverterFactory.create())
                     .addConverterFactory(GsonConverterFactory.create(gson1))
                     .client(client1)
                     .build()
                 val apiService1 = retrofit1.create(ApiInterface::class.java!!)
                 val requestObject2 = JSONObject()
                 try {
                     val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                     val agentId = AgentIdSP.getString("Agent_ID", null)
                     val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                     val calendar = Calendar.getInstance()
                     val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
                     val dateTime = simpleDateFormat.format(calendar.time)
                     val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
                     val token = tokenSP.getString("token", null)
//                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
//                        var Imei = DeviceAppDetails.imei
//                        if (Imei != null && !Imei.isEmpty()) {
//                        }else{
//                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
//                            Imei = DeviceAppDetails1.imei
//                        }

                     val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                     var Imei = DeviceAppDetails1.imei
                     val cardNo = BizcoreApplication.TEMP_CARD_NO
                     val customNo = BizcoreApplication.TEMP_CUST_NO
                     var acc = value?.split("-")?.get(1)
                     if(acc.equals(""))
                     {
                        acc="000000000000"
                     }
                     val hashList = ArrayList<String>()
                     hashList.add(Imei)
                     hashList.add(dateTime)
                     hashList.add(randomNumber)
                     hashList.add(agentId!!)
                     hashList.add(cardNo)
                     if (acc != null) {
                         hashList.add(acc)
                     }
                     if (acc != null) {
                         hashList.add(acc)
                     }
                     val hashString ="31"+ CryptoGraphy.getInstance().hashing(hashList)+token
                     requestObject2.put("Processing_Code", BizcoreApplication.encryptMessage("311011"))
                     requestObject2.put("Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage(cardNo))
                     requestObject2.put("Customer_Number", BizcoreApplication.encryptMessage(acc))
                     requestObject2.put("AccountIdentification1", BizcoreApplication.encryptMessage(acc))
                     requestObject2.put("From_Module", BizcoreApplication.encryptMessage(strModule))
                     requestObject2.put("RequestMessage", BizcoreApplication.encryptMessage("hloooo"))
                     requestObject2.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                     requestObject2.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                     requestObject2.put("ResponseType", BizcoreApplication.encryptMessage("1"))
                     requestObject2.put("Token", BizcoreApplication.encryptMessage(hashString))
                     requestObject2.put("CardLess", BizcoreApplication.encryptMessage("1"))
                     requestObject2.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
                     requestObject2.put("Auth_ID", BizcoreApplication.encryptMessage(authid))
                     requestObject2.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                     requestObject2.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                     requestObject2.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                     requestObject2.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                 } catch (e: Exception) {
                     progressDialog!!.dismiss()
                     e.printStackTrace()
                     val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                         " Some technical issues.", Snackbar.LENGTH_SHORT
                     )
                     mySnackbar.show()
                 }
                 val body1 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject2.toString())
                 val call1 = apiService1.getBalenq(body1)
                 call1.enqueue(object: retrofit2.Callback<String> {
                     override fun onResponse(call: retrofit2.Call<String>, response1:
                     Response<String>
                     ) {
                         Log.i("ResponseEnquiry",response1.body())
                        try {
                             progressDialog!!.dismiss()
                             val jObject1 = JSONObject(response1.body())
                             val jobjt = jObject1.getJSONObject("BalInfo")
                            if (jObject1.getString("StatusCode") == "0") {
                                val amount =jobjt.getString("BalanceAmount")
                                val due =jobjt.getString("DueAmount")
                                if("C" in amount ){
                                    txtv_viewbal.setText("Rs "+amount+"(Cr)")
                                }else if(amount.contains("null"))
                                { }else
                                {
                                    txtv_viewbal.setText("Rs "+amount)
                                }
                                if(due.contains("null"))
                                {
                                    ll_dueamnt?.visibility  = View.INVISIBLE
                                }
                                else if ("C" in due && !due.startsWith("0") )
                                {
                                    ll_dueamnt?.visibility  = View.VISIBLE
                                    txtv_dueam1.setText("Rs "+due+"(Cr)")
                                }
                                else if (!due.startsWith("0")&& due!=null)
                                {
                                    ll_dueamnt?.visibility  = View.VISIBLE
                                    txtv_dueam1.setText("Rs "+due)
                                }
                            }else  {
                                val dialogBuilder = AlertDialog.Builder(this@BalanceEnqActivity, R.style.MyDialogTheme)
                                dialogBuilder.setMessage(jobjt.getString("ResponseMessage"))
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
                 Toast.makeText(applicationContext," Some technical issues.",Toast.LENGTH_LONG).show()
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

    private fun getAccounts() {
     try {
         val builder = AlertDialog.Builder(this)
         val inflater1 = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
         val layout = inflater1.inflate(R.layout.account_dialog_balenq, null)
         val listView = layout.findViewById<ListView>(R.id.listView)
         builder.setView(layout)
         val alertDialog = builder.create()
         val adapter = ArrayAdapter<String>(this, R.layout.list_account, R.id.tvtitle, items)
         listView.adapter = adapter
             listView.onItemClickListener =
                 AdapterView.OnItemClickListener { adapterView, view, position, l ->
                     // TODO Auto-generated method stub
                     value = adapter.getItem(position)
                     txtv_selecacc.text = value
                     alertDialog.dismiss()
                 }
             alertDialog.show()
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

}
