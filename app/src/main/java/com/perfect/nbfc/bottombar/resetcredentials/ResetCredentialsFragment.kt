package com.perfect.nbfc.bottombar.resetcredentials

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.perfect.nbfc.R
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import com.perfect.nbfc.Api.ApiInterface
import com.perfect.nbfc.Api.ApiService
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.Helper.ConnectivityUtils
import com.perfect.nbfc.Helper.CryptoGraphy
import com.perfect.nbfc.Helper.DeviceAppDetails
import com.perfect.nbfc.bottombar.home.HomeFragment
import com.perfect.nbfc.launchingscreens.Login.LoginActivity
import com.perfect.nbfc.launchingscreens.MPIN.MPINActivity
import com.perfect.nbfc.launchingscreens.MainHome.HomeActivity
import kotlinx.android.synthetic.main.fragment_notifications.*
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
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*

class ResetCredentialsFragment : Fragment(),View.OnClickListener  {

    private lateinit var notificationsViewModel: ResetCredentialsViewModel
    lateinit var agentId:String
    lateinit var token:String
    lateinit var agentNameShort:String
    lateinit var toast:Toast
    lateinit var agent:String
    var llMpin: LinearLayout? = null
    var llChangepin:LinearLayout? = null
    var tvPasswordChange:TextView? = null
    var tvMpin:TextView? = null
    var cpUserName:TextInputEditText? = null
    var mPinUserName:TextInputEditText? = null
    var btnCPSubmit:Button? = null
    var btnMPinSubmit:Button? = null

    lateinit var dbHelper : DBHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        notificationsViewModel = ViewModelProviders.of(this).get(ResetCredentialsViewModel::class.java)
        (activity as AppCompatActivity).supportActionBar!!.hide()
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        tvMpin              = root.findViewById(R.id.tvMpin)
        tvPasswordChange    = root.findViewById(R.id.tvPasswordChange)
        llMpin              = root.findViewById(R.id.linear_mpin_view)
        llChangepin         = root.findViewById(R.id.linear_change_pin_view)
        btnCPSubmit         = root.findViewById(R.id.btnCPSubmit)
        btnMPinSubmit       = root.findViewById(R.id.btnMPinSubmit)
        cpUserName          = root.findViewById(R.id.tvReceiverName)
        mPinUserName        = root.findViewById(R.id.input_user_name)
        tvMpin!!.setOnClickListener(this)
        btnCPSubmit!!.setOnClickListener(this)
        btnMPinSubmit!!.setOnClickListener(this)
        tvPasswordChange!!.setOnClickListener(this)
        llMpin!!.isVisible = true
        llChangepin!!.isVisible = false
        val AgentIdSP   = context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
        val TokenId     = context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF4,0)
        val UserName    = context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF5,0)
        val AgentName = context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF2,0)
        agentId     = AgentIdSP.getString("Agent_ID", null)!!
        token       = TokenId.getString("token", null)!!
        agentNameShort   = UserName.getString("username", null)!!
        agent       = AgentName.getString("Agent_Name", null)!!
        if(agentNameShort!=null){
            mPinUserName!!.setText(agent)
            mPinUserName!!.isEnabled = false
            cpUserName!!.setText(agent)
            cpUserName!!.isEnabled = false
        }
        return root
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tvMpin -> {
                tvMpin?.setBackgroundResource(R.drawable.toggle1)
                tvPasswordChange?.setBackgroundResource(R.drawable.toggle3)
                tvMpin!!.setTextColor(Color.parseColor("#ffffff"))
                tvPasswordChange!!.setTextColor(Color.parseColor("#5a1a4c"))
                llMpin!!.isVisible = true
                llChangepin!!.isVisible = false
                input_user_password!!.setText("")
                input_user_mpin!!.setText("")
                input_user_new_pin!!.setText("")
                input_user_new_pin_confirm!!.setText("")
            }
            R.id.tvPasswordChange -> {
                tvMpin?.setBackgroundResource(R.drawable.toggle4)
                tvPasswordChange?.setBackgroundResource(R.drawable.toggle)
                tvPasswordChange!!.setTextColor(Color.parseColor("#ffffff"))
                tvMpin!!.setTextColor(Color.parseColor("#5a1a4c"))
                llMpin!!.isVisible = false
                llChangepin!!.isVisible = true
                tvPassword!!.setText("")
                tvNewPassword!!.setText("")
                tvConfirmPassword!!.setText("")
            }
            R.id.btnCPSubmit -> {
                changePassword()
            }
            R.id.btnMPinSubmit -> {
                changeMPin()
            }
        }
    }

    private fun changeMPin(){
        input_user_mpin.setError(null)
        input_user_new_pin.setError(null)
        input_user_new_pin_confirm.setError(null)
        val userName = mPinUserName?.text.toString()
        val password = input_user_password.text.toString()
        val currPin = input_user_mpin.text.toString()
        val newPin = input_user_new_pin.text.toString()
        val confPin = input_user_new_pin_confirm.text.toString()
        if (userName.isEmpty()) {
            mPinUserName?.setError("Please enter user name")
            return
        }
        else if (password.isEmpty()) {
            input_user_password.setError("Please enter pasword")
            return
        }
        else if (currPin.length != 6) {
            input_user_mpin.setError("Pin must be 6 digit")
            return
        }
        else if (newPin.length != 6) {
            input_user_new_pin.setError("New pin must be 6 digits")
            return
        }
        else if (newPin != confPin) {
            input_user_new_pin_confirm.setError("Enter same as above")
            return
        }
        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId.toString())

//                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
//                        var Imei = DeviceAppDetails.imei
//                        if (Imei != null && !Imei.isEmpty()) {
//                        }else{
//                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
//                            Imei = DeviceAppDetails1.imei
//                        }

        val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(context)
        var Imei = DeviceAppDetails1.imei

        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
        val dateTime = simpleDateFormat.format(calendar.time)
        var  deviceAppDetails : DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails( context )
        val hashList = ArrayList<String>()
        hashList.add(Imei)
        hashList.add(dateTime)
        hashList.add(randomNumber)
        hashList.add(agentId)
        hashList.add(agentNameShort)
        hashList.add(password)
        hashList.add(currPin)
        hashList.add(newPin)
        val hashString = "08" + CryptoGraphy.getInstance().hashing(hashList) + token
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
                requestObject1.put("LoginMode",BizcoreApplication.encryptMessage("5"))
                requestObject1.put("Password",BizcoreApplication.encryptMessage(password))
                requestObject1.put("SystemTrace_AuditNumber",BizcoreApplication.encryptMessage(randomNumber))
                requestObject1.put("Version_code", BizcoreApplication.encryptMessage(Integer.toString(deviceAppDetails.appVersion)))
                requestObject1.put("Token",BizcoreApplication.encryptMessage(hashString))
                requestObject1.put("User_Name",BizcoreApplication.encryptMessage(agentNameShort))
                requestObject1.put("MPINChange",BizcoreApplication.encryptMessage(newPin))
                requestObject1.put("CurrentDate",BizcoreApplication.encryptMessage(dateTime))
                requestObject1.put("MPIN",BizcoreApplication.encryptMessage(currPin))
                requestObject1.put("Agent_ID",BizcoreApplication.encryptMessage(agentId))
                requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
            }catch (e:Exception){
                e.printStackTrace()
            }
            val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
            val call = apiService.getChangeCredentials(body)
            call.enqueue(object :Callback<String>{
                override fun onResponse(call: Call<String>, response:
                Response<String>
                ) {
                    try {
                        val jObject = JSONObject(response.body())
                        val jobjt = jObject.getJSONObject("LogInfo")
                        if (jObject.getString("StatusCode") == "0") {
                            val otpSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF9,0)
                            val otpSPEditer = otpSP.edit()
                                otpSPEditer.putString("mpin", currPin)
                                otpSPEditer.commit()
                            showDialog(jobjt.getString("ResponseMessage"),"mpin")
                        }
                        else if (jObject.getString("StatusCode") == "2") {
                            toast = Toast.makeText(context,jobjt.getString("ResponseMessage"),Toast.LENGTH_LONG)
                            toast.show()
                        }
                        else if (jObject.getString("StatusCode") == "3") {
                            toast = Toast.makeText(context,jobjt.getString("ResponseMessage"),Toast.LENGTH_LONG)
                            toast.show()
                        }
                        else {
                            toast = Toast.makeText(context,jobjt.getString("EXMessage"),Toast.LENGTH_LONG)
                            toast.show()
                        }

                    }
                    catch (e:Exception) {
                        toast = Toast.makeText(context, " Some technical issues.", Toast.LENGTH_LONG)
                        toast.show()
                        e.printStackTrace()
                    }
                }
                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    toast = Toast.makeText(context, " Some technical issues.", Toast.LENGTH_LONG)
                    toast.show()
                }
            })
        }catch (e:Exception){

            toast = Toast.makeText(context, " Some technical issues.", Toast.LENGTH_LONG)
            toast.show()
            e.printStackTrace()
        }
    }

    private fun changePassword() {

        val ReceiverName = tvReceiverName.text.toString()
        val password = tvPassword.text.toString()
        val newPassword = tvNewPassword.text.toString()
        val confirmPassword = tvConfirmPassword.text.toString()
        if (ReceiverName.isEmpty()) {
            tvPassword.setError("Please enter user name")
            return
        }
        if (password.isEmpty()) {
            tvPassword.setError("Please enter password")
            return
        }
        if (newPassword.isEmpty()) {
            tvNewPassword.setError("Please enter password")
            return
        }
        if (newPassword.length < 6) {
            tvNewPassword.setError("Please set minimum 6 digit password")
            return
        }
        if (confirmPassword.isEmpty()) {
            tvConfirmPassword.setError("Please enter confirm password")
            return
        }
        if (newPassword != confirmPassword) {
            Toast.makeText(context, "Password and confirm password should be same", Toast.LENGTH_LONG).show()
            return
        }
        val agentId = agentId
        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
        val hashList = ArrayList<String>()
        val calendar  = Calendar.getInstance()
        val simpleDateFormat    = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
        val dateTime    = simpleDateFormat.format(calendar.time)
//                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
//                        var Imei = DeviceAppDetails.imei
//                        if (Imei != null && !Imei.isEmpty()) {
//                        }else{
//                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
//                            Imei = DeviceAppDetails1.imei
//                        }

        val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(context)
        var Imei = DeviceAppDetails1.imei
        hashList.add(Imei)
        hashList.add(dateTime)
        hashList.add(randomNumber)
        hashList.add(agentId)
        hashList.add(agentNameShort)
        hashList.add(password)
        hashList.add(confirmPassword)
        val hashString = "05" + CryptoGraphy.getInstance().hashing(hashList) + token
        val deviceAppDetails : DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails( context )
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
                requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                requestObject1.put("Version_code", BizcoreApplication.encryptMessage(Integer.toString(deviceAppDetails.getAppVersion())))
                requestObject1.put("Token", BizcoreApplication.encryptMessage(hashString))
                requestObject1.put("LoginMode", BizcoreApplication.encryptMessage("4"))
                requestObject1.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                requestObject1.put("User_Name", BizcoreApplication.encryptMessage(agentNameShort))
                requestObject1.put("Password", BizcoreApplication.encryptMessage(password))
                requestObject1.put("PasswordChange", BizcoreApplication.encryptMessage(confirmPassword))
                requestObject1.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
                requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
            }catch (e:Exception){
                e.printStackTrace()
            }
            val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
            val call = apiService.getChangeCredentials(body)
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response:
                Response<String>
                ) {

                    try {
                        val jObject = JSONObject(response.body())
                        val jobjt = jObject.getJSONObject("LogInfo")
                        if (jObject.getString("StatusCode") == "0") {
                            showDialog(jobjt.getString("ResponseMessage"),"pass")
                        }
                        else{
                            toast = Toast.makeText(context,jObject.getString("EXMessage"),Toast.LENGTH_LONG)
                            toast.show()
                        }
                    }
                    catch (e: Exception) {
                        toast = Toast.makeText(context, " Some technical issues.", Toast.LENGTH_LONG)
                        toast.show()
                        e.printStackTrace()
                    }
            }
                override fun onFailure(call: Call<String>?, t: Throwable?) {

                    toast = Toast.makeText(context, " Some technical issues.", Toast.LENGTH_LONG)
                    toast.show()
                }
            })
        } catch (e: Exception) {

            toast = Toast.makeText(context, " Some technical issues.", Toast.LENGTH_LONG)
            toast.show()
            e.printStackTrace()
        }

    }

    private fun showDialog(title: String,mode : String) {
        val dialog = Dialog(activity!!)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.success_layout)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val message = dialog.findViewById(R.id.txtMsg)as TextView
        message.text = title
        val yesBtn = dialog .findViewById(R.id.btnSubmit) as Button
        yesBtn.setOnClickListener {
            dialog .dismiss()
            tvPassword!!.setText("")
            tvNewPassword!!.setText("")
            tvConfirmPassword!!.setText("")
            input_user_password!!.setText("")
            input_user_mpin!!.setText("")
            input_user_new_pin!!.setText("")
            input_user_new_pin_confirm!!.setText("")

            if (mode.equals("pass")){
                doLogout()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                activity!!.finish()
            }
            if (mode.equals("mpin")){
                val intent = Intent(context, MPINActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                activity!!.finish()
            }

        }
        dialog .show()
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
        val caInput = context!!.assets.open(ApiService.CERT_NAME)
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

    private fun doLogout() {
        try {
            val loginSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF,0)
            val loginEditer = loginSP.edit()
            loginEditer.putString("loginsession", "No")
            loginEditer.commit()
            val AgentIdSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
            val AgentIdEditor = AgentIdSP.edit()
            AgentIdEditor.putString("Agent_ID", "")
            AgentIdEditor.commit()
            val Agent_NameSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF2, 0)
            val Agent_NameEditer = Agent_NameSP.edit()
            Agent_NameEditer.putString("Agent_Name", "")
            Agent_NameEditer.commit()
            val CusMobileSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF3, 0)
            val CusMobileEditer = CusMobileSP.edit()
            CusMobileEditer.putString("CusMobile", "")
            CusMobileEditer.commit()
            val tokenSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
            val tokenEditer = tokenSP.edit()
            tokenEditer.putString("token", "")
            tokenEditer.commit()
            val UserName = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF5, 0)
            val UserNameEditor = UserName.edit()
            UserNameEditor.putString("username", "")
            UserNameEditor.commit()
            val transactionIDSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF6, 0)
            val transactionIDEditor = transactionIDSP.edit()
            transactionIDEditor.putString("Transaction_ID", "1")
            transactionIDEditor.commit()
            val archiveIDSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF7, 0)
            val archiveIDEditor = archiveIDSP.edit()
            archiveIDEditor.putString("Archive_ID", "1")
            archiveIDEditor.commit()
            val loginTimeSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF8, 0)
            val loginTimeEditer = loginTimeSP.edit()
            loginTimeEditer.putString("logintime", "")
            loginTimeEditer.commit()
            dbHelper = DBHandler(context!! )
            dbHelper.deleteallAccount()
            dbHelper.deleteallTransaction()
            dbHelper.deleteAllArchieve()
            var intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity!!.finish()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}