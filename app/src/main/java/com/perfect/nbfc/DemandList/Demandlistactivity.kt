package com.perfect.nbfc.Demandlist

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.perfect.nbfc.Api.ApiInterface
import com.perfect.nbfc.Api.ApiService
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.Helper.ConnectivityUtils
import com.perfect.nbfc.Helper.CryptoGraphy
import com.perfect.nbfc.Offline.Activity.NewCollectionActivity
import com.perfect.nbfc.R
import kotlinx.android.synthetic.main.activity_demandlistactivity.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.KeyStore
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*

class Demandlistactivity : AppCompatActivity() {
    private var progressDialog  : ProgressDialog? = null
    var value :String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demandlistactivity)


        search.setOnClickListener {
//            value =  edittext.text.toString().trim()
            value = "007047035183"
            if (value ==null || value == ""){
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                   "Please enter Loan number" , Snackbar.LENGTH_SHORT
                )
                mySnackbar.show()
                return@setOnClickListener

            }

            if (value.length == 12){
                Load_retrofit_demandlist(value)
            }else{
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                    "Please enter Correct Loan number" , Snackbar.LENGTH_SHORT
                )
                mySnackbar.show()
            }

        }
//comment while live
//        Load_retrofit_demandlist(value)
    }

    fun Load_retrofit_demandlist(value:String){
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@Demandlistactivity, R.style.Progress)
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
                        val calendar = Calendar.getInstance()
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

                        requestObject1.put("Card_Acceptor_Terminal_IDCode",BizcoreApplication.encryptMessage(Imei))
                       requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                       requestObject1.put("FK_Account", BizcoreApplication.encryptMessage(value))
                        requestObject1.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
                       requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                       requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))

//                        requestObject1.put("Card_Acceptor_Terminal_IDCode", "v0PrGMAbar+fkoItM9cvkH3l2SGUhaJSfTfnvoL6Owk=\n")
//                        requestObject1.put("FK_Account", "8Yhes6bInMFjuN/thPaz0Cv9sdkDcxiYXrpb/irEO2M=\n")
//                        requestObject1.put("CurrentDate","Tyz7PDFRxgCZYoOPz6VRPIr+hL/KfEXJ0VFc8GeKSFtMOeYT7+s+X2lFItnIX6uu\n")
//                        requestObject1.put("BankKey", "+SUslvwoUItg5dP+4hBavjiX5MJtwa8E+RbAv5nac68=\n")
//                        requestObject1.put("BankHeader","CgvyW/R2N1D6g5Ok6RR2uPWcDhbsLrYXlVDUCBYw1Qw=\n")

                        Log.e("dataaaa","Agent_ID " + BizcoreApplication.encryptMessage("114"))
                        Log.e("dataaaa","CurrentDate " +BizcoreApplication.encryptMessage(dateTime))
                        Log.e("dataaaa","Card_Acceptor_Terminal_IDCode "+BizcoreApplication.encryptMessage(Imei))
                        Log.e("dataaaa","BankKey"+ BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                        Log.e("dataaaa","BankHeader"+BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                    }
//                    requestObject1.toString().replace("\n","")
//                    requestObject1.toString().replace("","")
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getDemandlist(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                Log.e("DemandList",""+jObject)
//                                val jobjt = jObject.getJSONObject("TransInfo")
                                if (jObject.getString("StatusCode") == "0") {
                                    val jobjt =
                                        jObject.getJSONObject("DemandList")
                                    val jarray =
                                        jobjt.getJSONArray("DemandListDetails")

                                    val obj_adapter = DemandLsitAdaptor(
                                        applicationContext!!,
                                        jarray
                                    )
                                    cv_demandlist!!.layoutManager = LinearLayoutManager(
                                        applicationContext,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                    cv_demandlist!!.adapter = obj_adapter

                                } else {
                                    var EXMessage = jObject.getString("EXMessage")
                                    val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                        EXMessage, Snackbar.LENGTH_SHORT
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
                val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
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

    private fun doCollection() {
        val intent= Intent(this, NewCollectionActivity::class.java)
        intent.putExtra("from", "Deposit")
        startActivity(intent)
        finish()
    }
    private fun getHostnameVerifier(): HostnameVerifier {
        return HostnameVerifier { hostname, session -> true }
    }
}