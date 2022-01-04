package com.perfect.nbfc.balanceEnquiry

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.perfect.nbfc.Api.ApiInterface
import com.perfect.nbfc.Api.ApiService
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Deposit.DepositActivity
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.Helper.ConnectivityUtils
import com.perfect.nbfc.Helper.CryptoGraphy
import com.perfect.nbfc.Offline.Model.AccountModel
import com.perfect.nbfc.R
import com.perfect.nbfc.launchingscreens.Login.LoginActivity
import com.perfect.nbfc.launchingscreens.MPIN.MPINActivity
import com.perfect.nbfc.launchingscreens.MainHome.HomeActivity
import kotlinx.android.synthetic.main.activity_select.*
import kotlinx.android.synthetic.main.activity_select.et_otp1
import kotlinx.android.synthetic.main.activity_select.et_otp2
import kotlinx.android.synthetic.main.activity_select.et_otp3
import kotlinx.android.synthetic.main.activity_select.et_otp4
import kotlinx.android.synthetic.main.activity_select.et_otp5
import kotlinx.android.synthetic.main.activity_select.et_otp6
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

class SelectAccountActivity : AppCompatActivity(),View.OnClickListener {

    lateinit var dbHelper : DBHandler
    private var strModule: String? = null
    private var strModuleValue: String? = null
    private var strfrom: String? = null
    var listItem: Array<String>? = null
    private var multipleList: String? = null
    private var layt_otp: LinearLayout? = null
    private var mob: String?=null
    private var otp: String?=null
    private var isCarded: Boolean = false
    private var progressDialog: ProgressDialog? = null
    private val transtypeIndicator: String = "R"
    private var mAccountNo1:String? = null
    lateinit var handler: Handler
    lateinit var r: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
        initiateViews()
        setRegViews()
        edtFocus()
        val bundle:Bundle = intent.extras!!
        val from = bundle.get("from")
        strfrom= from as String?
        if(strfrom.equals("Deposit")){
            btn_send.text="Proceed"
        }
        if(strfrom.equals("Balance Enquiry")){
            btn_send.text="Proceed"
        }
        numClick()
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

    private fun initiateViews() {
        layt_otp =findViewById(R.id.layt_otp)
    }

    private fun numClick(){
        edt_acc_second.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN ->{
                        val msg: String = edt_acc_first.text.toString()
                        if(msg.length==1)
                        {
                            edt_acc_first.setText("00"+edt_acc_first.text.toString())
                        }
                        else if(msg.length==2)
                        {
                            edt_acc_first.setText("0"+edt_acc_first.text.toString())
                        }
                        if(msg.equals("")||msg.equals("0")||msg.equals("00")||msg.equals("000"))    //size as per your requirement
                        {
                            edt_acc_first.setText("000")
                        }
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })
        edt_acc_third.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN ->{
                        val msg: String = edt_acc_second.text.toString()
                        if(msg.length==1)
                        {
                            edt_acc_second.setText("00"+edt_acc_second.text.toString())
                        }
                        else if(msg.length==2)
                        {
                            edt_acc_second.setText("0"+edt_acc_second.text.toString())
                        }
                        else if(msg.equals("")||msg.equals("0")||msg.equals("00")||msg.equals("000"))
                        {
                            edt_acc_second.setText("000"+edt_acc_second.text.toString())
                        }
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })
        edt_acc_first?.setOnKeyListener { v, keyCode, event ->
            if((event.action == KeyEvent.ACTION_DOWN)
                && (event.keyCode == KeyEvent.KEYCODE_ENTER)){
                val msg: String = edt_acc_first.text.toString()
                if(msg.length==1)
                {
                    edt_acc_first.setText("00"+edt_acc_first.text.toString())
                    edt_acc_second.requestFocus()
                }
                else if(msg.length==2)
                {
                    edt_acc_first.setText("0"+edt_acc_first.text.toString())
                    edt_acc_second.requestFocus()
                }
                if(msg.equals("")||msg.equals("0")||msg.equals("00")||msg.equals("000"))    //size as per your requirement
                {
                    edt_acc_first.setText("000")
                    edt_acc_second.requestFocus()
                }
                else if(msg.length==3)
                {
                    edt_acc_second.requestFocus()
                }
                return@setOnKeyListener true
            }
            false
        }
        edt_acc_second?.setOnKeyListener { v, keyCode, event ->
            if((event.action == KeyEvent.ACTION_DOWN)
                && (event.keyCode == KeyEvent.KEYCODE_ENTER)){
                val msg: String = edt_acc_second.text.toString()
                if(msg.length==1)
                {
                    edt_acc_second.setText("00"+edt_acc_second.text.toString())
                    edt_acc_third.requestFocus()
                }
                else if(msg.length==2)
                {
                    edt_acc_second.setText("0"+edt_acc_second.text.toString())
                    edt_acc_third.requestFocus()
                }
                else if(msg.equals("")||msg.equals("0")||msg.equals("00")||msg.equals("000"))
                {
                    edt_acc_second.setText("000"+edt_acc_second.text.toString())
                    edt_acc_third.requestFocus()
                }
                else if(msg.length==3)
                {
                    edt_acc_third.requestFocus()
                }
                return@setOnKeyListener true
            }
            false
        }


     /*   edt_acc_second?.setOnKeyListener { v, keyCode, event ->
            if((event.action == KeyEvent.ACTION_DOWN)
                && (event.keyCode == KeyEvent.KEYCODE_ENTER)){


                val msg: String = edt_acc_second.text.toString()
                if(msg.length==1)    //size as per your requirement
                {
                    edt_acc_second.setText("00"+edt_acc_second.text.toString())
                    edt_acc_third.requestFocus()
                }
                else if(msg.length==2)    //size as per your requirement
                {
                    edt_acc_second.setText("0"+edt_acc_second.text.toString())
                    edt_acc_third.requestFocus()
                }
                else if(msg.equals("")||msg.equals("0")||msg.equals("00")||msg.equals("000"))    //size as per your requirement
                {
                    edt_acc_second.setText("000"+edt_acc_second.text.toString())
                    edt_acc_third.requestFocus()
                }
                else if(msg.length==3)    //size as per your requirem
                // ent
                {
                    edt_acc_third.requestFocus()
                }


                return@setOnKeyListener true
            }

            false
        }*/
        /*edt_acc_third?.setOnKeyListener { v, keyCode, event ->
            if((event.action == KeyEvent.ACTION_DOWN)
                && (event.keyCode == KeyEvent.KEYCODE_ENTER)){
                //Do something, such as loadJob()
                val msg: String = edt_acc_third.text.toString()
                if(msg.length==4){
                    edt_acc_third.setText("00"+msg)
                    edt_acc_third.setSelection(edt_acc_third.getText().length)

                    //    val intent = Intent(this, BalanceEnq1Activity::class.java)
                    //   startActivity(intent)

                }


                return@setOnKeyListener true
            }

            false
        }*/

        edt_acc_third?.setOnKeyListener { v, keyCode, event ->
            if((event.action == KeyEvent.ACTION_DOWN)
                && (event.keyCode == KeyEvent.KEYCODE_ENTER)){
                //Do something, such as loadJob()
                val msg: String = edt_acc_third.text.toString()
                if(msg.length==4){
                    edt_acc_third.setText("00"+msg)
                    edt_acc_third.setSelection(edt_acc_third.getText().length)

                    //    val intent = Intent(this, BalanceEnq1Activity::class.java)
                    //   startActivity(intent)

                }


                return@setOnKeyListener true
            }

            false
        }
    }

    private fun setRegViews() {
        imback.setOnClickListener(this)
        btn_send.setOnClickListener(this)
        layt_selctacc.setOnClickListener(this)
        edt_acc_second.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_send -> {
                if(strfrom.equals("Deposit")) {
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
                    }
                    else{
                        val msg: String = edt_acc_third.text.toString()
                        if(msg.length==4){
                            edt_acc_third.setText("00"+msg)
                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                            proceedForAccountDetails()
                        }
                        else if(msg.length==5 ){
                            edt_acc_third.setText("0"+msg)
                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                        }
                        else if(msg.length==3||msg.startsWith("000"))
                        {
                            edt_acc_third.setText("000"+msg)
                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                        }
                        else if(msg.length==2||msg.startsWith("0000"))
                        {
                            edt_acc_third.setText("0000"+msg)
                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                        }
                        else if(msg.length==1||msg.startsWith("00000"))
                        {
                            edt_acc_third.setText("00000"+msg)
                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                            Toast.makeText(applicationContext,"Invalid Account Number",Toast.LENGTH_LONG).show()
                        }
                        if(msg.equals("")||msg.equals("0")||msg.equals("00")||msg.equals("000")||msg.equals("0000")||msg.equals("00000")||msg.equals("000000"))
                        {
                            edt_acc_first.setText("000")
                            edt_acc_second.setText("000")
                            edt_acc_third.setText("000000")
                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                            Toast.makeText(applicationContext,"Invalid Account Number",Toast.LENGTH_LONG).show()
                        }
                        else if(msg.length==6 && (!"000000".equals(msg)))
                        {
                            edt_acc_third.setText(msg)
                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                            proceedForAccountDetails()
                        }
                    }
                }
                else if(strfrom.equals("Balance Enquiry")) {
                    if (txtv_selecacc.text.toString().equals("Select Account")){
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
                    }
                    else{
                        val msg: String = edt_acc_third.text.toString()
                        if(msg.length==4){
                            edt_acc_third.setText("00"+msg)
                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                            accountFetchingRequestOtp()
                        }
                        else if(msg.length==5 ){
                            edt_acc_third.setText("0"+msg)
                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                        }
                        else if(msg.length==3||msg.startsWith("000"))
                        {
                            edt_acc_third.setText("000"+msg)
                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                        }
                        else if(msg.length==2||msg.startsWith("0000"))
                        {
                            edt_acc_third.setText("0000"+msg)
                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                        }
                        else if(msg.length==1||msg.startsWith("00000"))
                        {
                            edt_acc_third.setText("00000"+msg)
                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                            Toast.makeText(applicationContext,"Invalid Account Number",Toast.LENGTH_LONG).show()
                        }
                        if(msg.equals("")||msg.equals("0")||msg.equals("00")||msg.equals("000")||msg.equals("0000")||msg.equals("00000")||msg.equals("000000"))
                        {
                            edt_acc_first.setText("000")
                            edt_acc_second.setText("000")
                            edt_acc_third.setText("000000")
                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                            Toast.makeText(applicationContext,"Invalid Account Number",Toast.LENGTH_LONG).show()
                        }
                        else if(msg.length==6 && (!"000000".equals(msg)))
                        {
                            edt_acc_third.setText(msg)
                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                            accountFetchingRequestOtp()
                        }
                    }
                }
            }
            R.id.layt_selctacc -> {
                getAccounts()
            }
            R.id.imback -> {
                finish()
            }
        }
    }

    private fun getAccounts() {
        try {
            val builder = AlertDialog.Builder(this)
            val inflater1 = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater1.inflate(R.layout.account_dialog_layout, null)
            val listView = layout.findViewById<ListView>(R.id.listView)
            builder.setView(layout)
            val alertDialog = builder.create()
            listItem = resources.getStringArray(R.array.array_accounts)
            val adapter = ArrayAdapter<String>(this, R.layout.list_account, R.id.tvtitle,
                listItem!!
            )
            listView.adapter = adapter
            listView.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView, view, position, l ->
                    // TODO Auto-generated method stub
                    val value = adapter.getItem(position)
                    txtv_selecacc.text = value
                    if (position == 0) {
                        strModule = "SB"
                        strModuleValue = "10"
                    }
                    if (position == 1) {
                        strModule = "DD"
                        strModuleValue = "21"
                    }
                    if (position == 2) {
                        strModule = "RD"
                        strModuleValue = "22"
                    }
                    if (position == 3) {
                        strModule = "GS"
                        strModuleValue = "23"
                    }
                    alertDialog.dismiss()
                }
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun edtFocus() {
        edt_acc_first.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val msg: String = edt_acc_first.text.toString()
            }
        })
        edt_acc_second.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val msg: String = edt_acc_second.text.toString()
            }
        })
        edt_acc_third.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun proceedForAccountDetails() {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@SelectAccountActivity, R.style.Progress)
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
                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
                        val dateTime = simpleDateFormat.format(calendar.time)
                        val mAccountNo = edt_acc_first.text.toString()+edt_acc_second.text.toString()+edt_acc_third.text.toString()
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
                        val hashList = ArrayList<String>()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(agentId!!)
                        hashList.add(mAccountNo)
                        val hashString = "09" + CryptoGraphy.getInstance().hashing(hashList) + token
                        requestObject1.put("Processing_Code", BizcoreApplication.encryptMessage("090000"))
                        requestObject1.put("Customer_Number", BizcoreApplication.encryptMessage("000000000000"))
                        requestObject1.put("Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage("0000000000000000"))
                        requestObject1.put("From_Module", BizcoreApplication.encryptMessage(strModule))
                        requestObject1.put("AccountIdentification2", BizcoreApplication.encryptMessage(mAccountNo))
                        requestObject1.put("RequestMessage", BizcoreApplication.encryptMessage("temporary"))
                        requestObject1.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject1.put("CardLess", BizcoreApplication.encryptMessage("1"))
                        requestObject1.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                            " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getVerificationCall(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                val jobjt = jObject.getJSONObject("verInfo")
                                if (jObject.getString("StatusCode") == "0") {
                                  //  Toast.makeText(applicationContext,jObject.toString(),Toast.LENGTH_LONG).show()
                                    Log.i("Response Verification",jObject.toString())
                                    btn_send.text="Proceed"
                                    val intent= Intent(applicationContext, DepositActivity::class.java)
                                    intent.putExtra("accountname", jobjt.getString("CusName"))
                                    intent.putExtra("strModule", strModule)
                                    intent.putExtra("strModuleValue", strModuleValue)
                                    intent.putExtra("accountno", edt_acc_first.text.toString()+edt_acc_second.text.toString()+edt_acc_third.text.toString())
                                    startActivity(intent)
                                    finish()
                                }
                                else {
                                    val dialogBuilder = AlertDialog.Builder(this@SelectAccountActivity, R.style.MyDialogTheme)
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

    private fun accountFetchingRequestOtp() {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@SelectAccountActivity, R.style.Progress)
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
                        val mAccountNo1 = edt_acc_first.text.toString()+edt_acc_second.text.toString()+edt_acc_third.text.toString()
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
                        val hashList = ArrayList<String>()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(agentId!!)
                        hashList.add(cardNo)
                        hashList.add(customNo)
                        hashList.add(mAccountNo1)
                        val hashString ="21"+CryptoGraphy.getInstance().hashing(hashList)+token
                        requestObject2.put("Processing_Code", BizcoreApplication.encryptMessage("211011"))
                        requestObject2.put("Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage(cardNo))
                        requestObject2.put("Customer_Number", BizcoreApplication.encryptMessage("000000000000"))
                        requestObject2.put("AccountIdentification1", BizcoreApplication.encryptMessage(mAccountNo1))
                        requestObject2.put("MultipleList", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("From_Module", BizcoreApplication.encryptMessage(strModule))
                        requestObject2.put("RequestMessage", BizcoreApplication.encryptMessage("hloooo"))
                        requestObject2.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject2.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject2.put("ResponseType", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject2.put("CardLess", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("VerifyOTP", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("Amount", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
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
                    val call1 = apiService1.getAccountfetch(body1)
                    call1.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response1:
                        Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject1 = JSONObject(response1.body())
                                val jobjt = jObject1.getJSONObject("AccInfo")
                                if (jObject1.getString("StatusCode") == "0") {
                                   btn_send.text="Proceed"
                                    mob = jobjt.getString("CusMobile")
                                    otp = jobjt.getString("OTPRefNum")
                                    if(mob.isNullOrBlank()) {
                                        Toast.makeText(applicationContext, "No phone number is linked with this account.", Toast.LENGTH_SHORT).show()
                                    }else{
                                        layt_otp!!.visibility = View.VISIBLE
                                        otpClick()
                                    }
                                }
                                else  if (jObject1.getString("StatusCode") == "1") {
                                    doLogout()
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

    private fun otpClick() {
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
                 sendPinForAccountFetching(otp,varOtp)
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun sendPinForAccountFetching(otp: String?, varOtp: String) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@SelectAccountActivity, R.style.Progress)
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
                        mAccountNo1 = edt_acc_first.text.toString()+edt_acc_second.text.toString()+edt_acc_third.text.toString()
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
                        val hashList = ArrayList<String>()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(agentId!!)
                        hashList.add(cardNo)
                        hashList.add(customNo)
                        hashList.add(mAccountNo1!!)
                        val hashString ="21"+CryptoGraphy.getInstance().hashing(hashList)+token
                        requestObject2.put("Processing_Code", BizcoreApplication.encryptMessage("211011"))
                        requestObject2.put("Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage(cardNo))
                        requestObject2.put("Customer_Number", BizcoreApplication.encryptMessage(customNo))
                        requestObject2.put("AccountIdentification1", BizcoreApplication.encryptMessage(mAccountNo1))
                        requestObject2.put("MultipleList", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("From_Module", BizcoreApplication.encryptMessage(strModule))
                        requestObject2.put("RequestMessage", BizcoreApplication.encryptMessage("hloooo"))
                        requestObject2.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject2.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject2.put("ResponseType", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject2.put("CardLess", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("OTPRefNum", BizcoreApplication.encryptMessage(otp))
                        requestObject2.put("VerifyOTP", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("Amount", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("OTP", BizcoreApplication.encryptMessage(varOtp))
                        requestObject2.put("TranstypeIndicator",BizcoreApplication.encryptMessage("R"))
                        requestObject2.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
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
                    val call1 = apiService1.getAccountfetch(body1)
                    call1.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response1:
                        Response<String>
                        ) {
                            Log.i("Responsesendpin",response1.body())
                            try {
                                progressDialog!!.dismiss()
                                val jObject1 = JSONObject(response1.body())
                                val jobjt = jObject1.getJSONObject("AccInfo")
                                if (jObject1.getString("StatusCode") == "0") {
                                      val intent= Intent(applicationContext, BalanceEnqActivity::class.java)
                                      intent.putExtra("accountname", jobjt.getString("CustomerName"))
                                      intent.putExtra("Response", jObject1.toString())
                                      intent.putExtra("AuthID", jobjt.getString("Auth_ID"))
                                      intent.putExtra("accountno", edt_acc_first.text.toString()+edt_acc_second.text.toString()+edt_acc_third.text.toString())
                                      startActivity(intent)
                                      finish()
                                }
                                else {
                                    Toast.makeText(applicationContext,"Error",Toast.LENGTH_LONG).show()
                                    val dialogBuilder = AlertDialog.Builder(this@SelectAccountActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage("OTP Mismatch, Please try later.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", DialogInterface.OnClickListener {
                                                dialog, id ->goHome()
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

    private fun goHome() {
        val intent = Intent(this, HomeActivity::class.java)
          startActivity(intent)
    }

}












