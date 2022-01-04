package com.perfect.nbfc.launchingscreens.Login

import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.goodiebag.pinview.Pinview
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.perfect.nbfc.Api.ApiInterface
import com.perfect.nbfc.Api.ApiService
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.Helper.ConnectivityUtils
import com.perfect.nbfc.Helper.CryptoGraphy
import com.perfect.nbfc.Helper.DeviceAppDetails
import com.perfect.nbfc.Offline.Model.AccountModel
import com.perfect.nbfc.R
import com.perfect.nbfc.launchingscreens.IntroSlides.WelcomeActivity
import com.perfect.nbfc.launchingscreens.MPIN.MPINActivity
import com.perfect.nbfc.launchingscreens.MainHome.HomeActivity
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.activity_otp.et_otp1
import kotlinx.android.synthetic.main.activity_otp.et_otp2
import kotlinx.android.synthetic.main.activity_otp.et_otp3
import kotlinx.android.synthetic.main.activity_otp.et_otp4
import kotlinx.android.synthetic.main.activity_otp.et_otp5
import kotlinx.android.synthetic.main.activity_otp.et_otp6
import kotlinx.android.synthetic.main.activity_otp.pinview
import kotlinx.android.synthetic.main.activity_pin_login.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
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
import java.util.*
import javax.net.ssl.*
import java.text.SimpleDateFormat;
class OtpActivity : AppCompatActivity(), View.OnClickListener {
    private var progressDialog: ProgressDialog? = null
    lateinit var dbHelper : DBHandler
    private var result:Boolean? = null
    private var hashString:String? = null
    lateinit var handler: Handler
    lateinit var r: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_otp)
        setRegViews()
        edtFocus()
        dbHelper = DBHandler(this)
//        loadData()
        handler = Handler()
        r = Runnable {
            /*val intent= Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()*/
        }
        startHandler()

        pinview.setPinViewEventListener(object : Pinview.PinViewEventListener {
            override fun onDataEntered(pinview: Pinview, fromUser: Boolean) {
                //Make api calls here or what not
                verifyOTP(pinview.value)
            }
        })

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

    private fun nextPageIntent() {
        val intent= Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    fun alertMsg(){
        /*val builder = AlertDialog.Builder(this)
        builder.setTitle("Invalid OTP")
        builder.setMessage("Please enter valid OTP for verifivation")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setNegativeButton("Cancel"){dialogInterface, which ->}
        builder.setPositiveButton("Ok"){dialogInterface, which ->
            et_otp1.text.clear()
            et_otp2.text.clear()
            et_otp3.text.clear()
            et_otp4.text.clear()
            et_otp5.text.clear()
            et_otp6.text.clear()
            et_otp1.requestFocus()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()*/
        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.layout_alertmsg)
      //  val body = dialog .findViewById(R.id.body) as TextView
      //  body.text = title
        val yesBtn = dialog .findViewById(R.id.btnOk) as Button
        val close = dialog .findViewById(R.id.imgv_close) as ImageView
        close.setOnClickListener {
            dialog .dismiss()

        }

        yesBtn.setOnClickListener {
            dialog .dismiss()

        }
       // noBtn.setOnClickListener { dialog .dismiss() }
        dialog .show()
            et_otp1.text.clear()
            et_otp2.text.clear()
            et_otp3.text.clear()
            et_otp4.text.clear()
            et_otp5.text.clear()
            et_otp6.text.clear()
            pinview.clearValue()



    }

    private fun edtFocus() {
        et_otp1.requestFocus()
        et_otp1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                et_otp2.requestFocus()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        et_otp2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                et_otp3.requestFocus()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        et_otp3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                et_otp4.requestFocus()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        et_otp4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                et_otp5.requestFocus()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        et_otp5.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                et_otp6.requestFocus()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        et_otp6.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

                val varOtp =et_otp1.text.toString()+et_otp2.text.toString()+et_otp3.text.toString()+et_otp4.text.toString()+et_otp5.text.toString()+et_otp6.text.toString()
                verifyOTP(varOtp)
            /*    if(varOtp.equals("123456")){
                   // nextPageIntent()

                }else{
                    alertMsg()
                }*/
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun verifyOTP(varOtp: String) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@OtpActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()

                try
                {
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
                    try
                    {

                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
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



                        var  deviceAppDetails :DeviceAppDetails= BizcoreApplication.getInstance().getDeviceAppDetails( this )

                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val  agentId = AgentIdSP.getString("Agent_ID", null)
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)


                        val hashList = ArrayList<String>()
                        hashList.add( Imei )
                        hashList.add( dateTime )
                        hashList.add( randomNumber )
                        hashList.add( agentId!! )
                        hashList.add( varOtp )

                        val hashString = "07" + CryptoGraphy.getInstance().hashing(hashList) + "8173224C-973A-48B1-ACB0-11815F260720"

                        requestObject1.put("LoginMode",  BizcoreApplication.encryptMessage("2"))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("SystemTrace_AuditNumber",  BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject1.put("MPIN",  BizcoreApplication.encryptMessage(varOtp))
                        requestObject1.put("Version_code", BizcoreApplication.encryptMessage(Integer.toString(deviceAppDetails.appVersion)))
                        requestObject1.put("CurrentDate",  BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero

                        }
                    catch (e:Exception) {
                        e.printStackTrace()
                        progressDialog!!.dismiss()
                    }

                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getOTP(body)
                    call.enqueue(object: Callback<String> {
                        override fun onResponse(call: Call<String>, response:
                        Response<String>
                        ) {
                            try
                            {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                val StatusCode = jObject.getString("StatusCode")
                                if (StatusCode == "0") {

                                    val jobjt = jObject.getJSONObject("LogInfo")

                                    val TokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4,0)
                                    val TokenEditer = TokenSP.edit()
                                    TokenEditer.putString("token", jobjt.getString("Token"))
                                    TokenEditer.commit()

                                    val loginSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF,0)
                                    val loginEditer = loginSP.edit()
                                    loginEditer.putString("loginsession", "Yes")
                                    loginEditer.commit()


                                    val calendar = Calendar.getInstance()
                                    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.ENGLISH)
                                    val dateTime = simpleDateFormat.format(calendar.time)

                                    val logintimeSP = applicationContext.getSharedPreferences(
                                        BizcoreApplication.SHARED_PREF8,0)
                                    val logintimeEditer = logintimeSP.edit()
                                    logintimeEditer.putString("logintime", dateTime)
                                    logintimeEditer.commit()

                                    val otpSP = applicationContext.getSharedPreferences(
                                        BizcoreApplication.SHARED_PREF9,0)
                                    val otpSPEditer = otpSP.edit()
                                    otpSPEditer.putString("mpin", varOtp)
                                    otpSPEditer.commit()

                                    startActivity(Intent(this@OtpActivity, HomeActivity::class.java))

                                    val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),jobjt.getString("ResponseMessage"), Snackbar.LENGTH_SHORT)
                                    mySnackbar.show()
                                    nextPageIntent()
                                }
                                else {
//                                    alertMsg()
                                    et_otp1.requestFocus()
                                    pinview.requestPinEntryFocus()

//                                    val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),jObject.getString("EXMessage"), Snackbar.LENGTH_SHORT)
//                                    mySnackbar.show()

                                    val dialogBuilder = android.app.AlertDialog.Builder(this@OtpActivity, R.style.MyDialogTheme)
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

                            }
                            catch (e:Exception) {
                                e.printStackTrace()
                                progressDialog!!.dismiss()
                            }

                        }
                        override fun onFailure(call:Call<String>, t:Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),"some technical issue", Snackbar.LENGTH_SHORT)
                            mySnackbar.show()

                        }
                    })
                }
                catch (e:Exception) {

                }

            }
            false -> {
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),"No Internet Connection!!", Snackbar.LENGTH_SHORT)
                mySnackbar.show()
            }
        }
    }

    private fun setRegViews() {
      //  tv_resend.setOnClickListener(this)
      //  tv_back.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.tv_resend->{
               /* val intent= Intent(this, HomeActivity::class.java)
                startActivity(intent)*/
            }
            R.id.tv_back->{
                val intent= Intent(this, LoginActivity::class.java)
                startActivity(intent)
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
        KeyManagementException::class)

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
                        hashList.add(agentId!!)
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
                                    for (i in 0..jarray.length()-1) {
                                        print(i)
                                        val jsonObject=jarray.getJSONObject(i)
                                        result =  dbHelper.insertUser(
                                            AccountModel(accountid = (i+1).toString(),
                                                shortname = jsonObject.getString("ShortName"),depositno = jsonObject.getString("DepositNumber"),
                                                customername = jsonObject.getString("CusName"),balance = jsonObject.getString("BalanceAmount"),
                                                deposittype = jsonObject.getString("DepositType"),module = jsonObject.getString("Module"),
                                                depositdate =jsonObject.getString("DepositDate") )
                                        )
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

}
