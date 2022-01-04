package com.perfect.nbfc.AgentReport.Balance

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.perfect.nbfc.Api.ApiInterface
import com.perfect.nbfc.Api.ApiService
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.Helper.ConnectivityUtils
import com.perfect.nbfc.Helper.CryptoGraphy
import com.perfect.nbfc.R
import com.perfect.nbfc.launchingscreens.MPIN.MPINActivity
import kotlinx.android.synthetic.main.activity_agent_balance.*
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


class AgentBalanceActivity : AppCompatActivity(), View.OnClickListener {

    private var progressDialog  : ProgressDialog? = null
    private var hashString      : String?         = null
    val calendar = Calendar.getInstance()
    lateinit var handler: Handler
    lateinit var r: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agent_balance)
        imback.setOnClickListener { onBackPressed() }
        view()
        loadBalance()
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
        stopHandler()
        startHandler()
    }

    fun stopHandler() {
        handler.removeCallbacks(r)
    }

    fun startHandler() {
        handler.postDelayed(r, 5 * 60 * 1000)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ll_print->{
                Toast.makeText(applicationContext,"print",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun view(){
        val AgentName = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF2,0)
        tv_holder_name!!.text = AgentName.getString("Agent_Name", null)

        val AgentPhoneNumber = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF3,0)
        tv_phone_number!!.text = AgentPhoneNumber.getString("CusMobile", null)

        tv_date.text = "Details as of "+SimpleDateFormat("dd-MM-yyyy HH:mm a", Locale.ENGLISH).format(calendar.time)

        ll_print.setOnClickListener(this)
    }

    private fun loadBalance(){
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@AgentBalanceActivity, R.style.Progress)
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
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
                        val dateTime = simpleDateFormat.format(calendar.time)
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
                        val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
                        val token = tokenSP.getString("token", null)
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val hashList = ArrayList<String>()
                            hashList.add( Imei )
                            hashList.add( dateTime )
                            hashList.add( randomNumber )
                            hashList.add(AgentIdSP.getString("Agent_ID", null).toString())
                        hashString = CryptoGraphy.getInstance().hashing( hashList )
                        hashString = "06"+hashString+token
                        requestObject1.put("Processing_Code", BizcoreApplication.encryptMessage("060000"))
                        requestObject1.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("TransDate", BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode",BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                    }
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getAgentBalance(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                val jobjt = jObject.getJSONObject("AgntBalInfo")
                                if (jObject.getString("StatusCode") == "0") {
                                    if(jobjt.getString("OpBalance").indexOf("Cr") != -1){
                                        tv_open_cr.text = "₹ "+jobjt.getString("OpBalance").replace(" Cr","")
                                        tv_open_dr.text = ""
                                    }
                                    else{
                                        tv_open_dr.text = "₹"+jobjt.getString("OpBalance").replace(" Dr","")
                                        tv_open_cr.text = ""
                                    }

                                    if(jobjt.getString("Payment").indexOf("Cr") != -1){
                                        tv_payment_cr.text = "₹ "+jobjt.getString("Payment").replace(" Cr","")
                                        tv_payment_dr.text = ""
                                    }
                                    else{
                                        tv_payment_dr.text = "₹ "+jobjt.getString("Payment").replace(" Dr","")
                                        tv_payment_cr.text = ""
                                    }
                                    if(jobjt.getString("Receipt").indexOf("Cr") != -1){
                                        tv_receipt_cr.text = "₹ "+jobjt.getString("Receipt").replace(" Cr","")
                                        tv_receipt_dr.text = ""
                                    }
                                    else{
                                        tv_receipt_dr.text = "₹ "+jobjt.getString("Receipt").replace(" Dr","")
                                        tv_receipt_cr.text = ""
                                    }
                                    if(jobjt.getString("Closing").indexOf("Cr") != -1){
                                        tv_curbal_cr.text = "₹ "+jobjt.getString("Closing").replace(" Cr","")
                                        tv_curbal_dr.text = ""
                                    }
                                    else{
                                        tv_curbal_dr.text = "₹ "+jobjt.getString("Closing").replace(" Dr","")
                                        tv_curbal_cr.text = ""
                                    }
                                    val mySnackbar = Snackbar.make(
                                        findViewById(R.id.rl_main),
                                        jobjt.getString("ResponseMessage"), Snackbar.LENGTH_SHORT
                                    )
                                    mySnackbar.show()
                                } else {
                                    val mySnackbar = Snackbar.make(
                                        findViewById(R.id.rl_main),
                                        jobjt.getString("ResponseMessage"), Snackbar.LENGTH_SHORT
                                    )
                                    mySnackbar.show()
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

}
