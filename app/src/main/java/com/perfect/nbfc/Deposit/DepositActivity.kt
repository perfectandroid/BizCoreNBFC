package com.perfect.nbfc.Deposit

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.perfect.nbfc.Api.ApiInterface
import com.perfect.nbfc.Api.ApiService
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.Helper.ConnectivityUtils
import com.perfect.nbfc.Helper.CryptoGraphy
import com.perfect.nbfc.Helper.NumberToWord
import com.perfect.nbfc.Offline.Activity.NewCollectionActivity
import com.perfect.nbfc.R
import com.perfect.nbfc.balanceEnquiry.SelectAccountActivity
import com.perfect.nbfc.launchingscreens.Login.LoginActivity
import com.perfect.nbfc.launchingscreens.MPIN.MPINActivity
import kotlinx.android.synthetic.main.activity_deposit.*
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
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*

class DepositActivity : AppCompatActivity() {

    private var progressDialog  : ProgressDialog? = null
    private var hashString      : String?         = null
    private var strModule       : String?         = null
    private var strModuleValue  : String?         = null
    private var accountno       : String?         = null
    private var accountName     : String?         = null
    private var strAmount       : String?         = null
    private var strMsg          : String?         = null
    private var selectedPrinter          : String?         = null
    lateinit var dbHelper : DBHandler
    lateinit var handler: Handler
    lateinit var r: Runnable

    private var avlBal          : Double?         = null
    private var netAmt          : Double?         = null
    private var opBal          : Double?         = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit)
        val bundle:Bundle = intent.extras!!
        accountName = bundle.get("accountname") as String?
        strModule = bundle.get("strModule") as String?
        strModuleValue = bundle.get("strModuleValue")as String?
        accountno = bundle.get("accountno")as String?

        tv_cus_name.text = accountName
        tv_acc_number.text = accountno
        setEdtTxtAmountCommaSeperator(input_amount,tv_rupees,true)
        //Toast.makeText(applicationContext,strModule+" "+strModuleValue+" "+accountno,Toast.LENGTH_LONG).show()
        btnSubmit!!.setOnClickListener({amountValidation()})
        imback!!.setOnClickListener { onBackPressed() }

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

    private fun amountValidation(){
        if (input_amount!!.text.toString() == null || input_amount!!.text.toString().isEmpty()) {
            input_amount.setError("Please enter deposit amount")
        }
        else{
            strAmount= input_amount.text?.toString()
            strAmount = strAmount!!.replace(",","")
            strMsg= input_msg.text?.toString()
            submitDeposit(strAmount,strMsg)
        }
    }

    private fun submitDeposit(strAmount: String?, strMsg: String?) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@DepositActivity, R.style.Progress)
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
                        var cardlessValue  = 1

                        val hashList = ArrayList<String>()
                        hashList.add( Imei )
                        hashList.add( dateTime )
                        hashList.add( randomNumber )
                        hashList.add( agentId!! )
                        hashList.add( "0000000000000000" )//card no
                        hashList.add( "000000000000" )//cus no
                        hashList.add(accountno.toString())//a/c no
                        hashList.add(strAmount!!)//amount
                        hashString = CryptoGraphy.getInstance().hashing( hashList )
                        hashString = "76"+hashString+token

                        val processingCode      = "760"+strModuleValue+"11"

                        requestObject1.put("Processing_Code", BizcoreApplication.encryptMessage(processingCode))
                        requestObject1.put("Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage("0000000000000000"))
                        requestObject1.put("Customer_Number", BizcoreApplication.encryptMessage("000000000000"))
                        requestObject1.put("AccountIdentification1", BizcoreApplication.encryptMessage(accountno))
                        requestObject1.put("From_Module", BizcoreApplication.encryptMessage(strModule))
                        requestObject1.put("RequestMessage", BizcoreApplication.encryptMessage(strMsg))
                        requestObject1.put("Narration", BizcoreApplication.encryptMessage(strMsg))
                        requestObject1.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("Amount", BizcoreApplication.encryptMessage(strAmount))
                        requestObject1.put("TransDate", BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject1.put("CardLess", BizcoreApplication.encryptMessage(1.toString()))
                        requestObject1.put("TransType", BizcoreApplication.encryptMessage("RECEIPT"))
                        requestObject1.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode",BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero

                        requestObject1.put("Latitude", BizcoreApplication.encryptMessage("11.87654"))
                        requestObject1.put("Longitude", BizcoreApplication.encryptMessage("76.87654"))
                        requestObject1.put("AddressLine", BizcoreApplication.encryptMessage("Ambadi"))
                        requestObject1.put("Locality", BizcoreApplication.encryptMessage("nadakvu"))
                        requestObject1.put("Area", BizcoreApplication.encryptMessage("kerala"))
                        requestObject1.put("Country", BizcoreApplication.encryptMessage("india"))
                        requestObject1.put("PostalCode", BizcoreApplication.encryptMessage("673011"))

                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                    }
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getTransactionRequest(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                val jobjt = jObject.getJSONObject("TransInfo")
                                if (jObject.getString("StatusCode") == "0") {

                                    val mySnackbar = Snackbar.make(
                                        findViewById(R.id.rl_main),
                                        jobjt.getString("ResponseMessage"), Snackbar.LENGTH_SHORT

                                    )
                                    mySnackbar.show()
                                    showSuccessDialog(": "+jobjt.getString("ResponseMessage"),
                                        ": "+accountName,
                                        ": "+accountno.toString()+"("+strModule+")",
                                        ": ₹ "+jobjt.getString("NetAmount")+" /-",
                                        ": "+jobjt.getInt("ReferanceNumber").toString(),
                                        jobjt.getString("BalanceAmount"),
                                        jobjt.getString("NetAmount"))

                                } else {
                                    val dialogBuilder = AlertDialog.Builder(this@DepositActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jObject.getString("EXMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", DialogInterface.OnClickListener {
                                                dialog, id -> dialog.dismiss()
                                           // doLogout()
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

    private fun doCollection() {
        val intent= Intent(this, NewCollectionActivity::class.java)
        intent.putExtra("from", "Deposit")
        startActivity(intent)
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun showSuccessDialog(deposit: String, recName: String, recAc: String, amount: String, reffNo: String, BalAmount: String, netAmount: String) {
        val dialog = Dialog(this)
            dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog .setCancelable(false)
            dialog .setContentView(R.layout.success_deposit_layout)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val txtDeposit = dialog.findViewById(R.id.txtDeposit)as TextView
            txtDeposit.text = deposit

        val txtReceiverName = dialog.findViewById(R.id.txtReceiverName)as TextView
            txtReceiverName.text = recName

        val txtReceiverAC = dialog.findViewById(R.id.txtReceiverAC)as TextView
            txtReceiverAC.text = recAc

        avlBal=(BalAmount.replace("C","")).toDouble()
        netAmt=(netAmount).toDouble()
        opBal= avlBal!! - netAmt!!
        val txtAOpeningBal = dialog.findViewById(R.id.txtAOpeningBal)as TextView
        txtAOpeningBal.text = ": ₹ "+opBal.toString()+"Cr"

        val txtAvbal = dialog.findViewById(R.id.txtAvbal)as TextView
        txtAvbal.text = ": ₹ "+BalAmount+"r"

        val txtAmount = dialog.findViewById(R.id.txtAmount)as TextView
            txtAmount.text = amount+"\n[ "+tv_rupees.text.toString()+" ]"

        val txtRefferenceNo = dialog.findViewById(R.id.txtRefferenceNo)as TextView
            txtRefferenceNo.text = reffNo

        val okBtn = dialog .findViewById(R.id.btnOK) as Button
            okBtn.setOnClickListener {
                dialog .dismiss()
                onBackPressed()
            }
        val printBtn = dialog.findViewById(R.id.btnprint) as Button
            printBtn.setOnClickListener{
                showPrintDialog()
            }
        dialog .show()
    }



    /*Place comma seperator on edit text and display amount in words on a text view*/
    fun setEdtTxtAmountCommaSeperator(
        editText: EditText,
        txtAmt: TextView?,
        isDecimalAllowed: Boolean
    ) {

        editText.addTextChangedListener(object : TextWatcher {
            internal lateinit var firstString: String
            internal var beforeInt = ""
            internal var beforeDec = ""

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                txtAmt!!.text = ""
                firstString = charSequence.toString()
                val rupee =
                    firstString.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (rupee.size > 0)
                    beforeInt = rupee[0]
                if (rupee.size > 1)
                    beforeDec = rupee[1]

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                try {
                    val rupeeString = "Rupees "
                    val amount = charSequence.toString().replace(",".toRegex(), "")
                    if (txtAmt != null && !amount.isEmpty()) {
                        val rupee = amount.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        if (rupee.size == 0)
                            return
                        var intPart = rupee[0]
                        val arrayLength = rupee.size
                        if (arrayLength == 2) {
                            var decPart = rupee[1]
                            if (decPart.length == 1)
                                decPart += "0"
                            if (isDecimalAllowed) {
                                if (intPart.length > 6 || decPart.length > 2) {
                                    editText.removeTextChangedListener(this)
                                    firstString = commSeperator(beforeInt)
                                    if (!beforeDec.isEmpty())
                                        firstString += ".$beforeDec"
                                    editText.setText(firstString)

                                    editText.setSelection(firstString.length)
                                    editText.addTextChangedListener(this)

                                    var amountInWords =
                                        rupeeString + NumberToWord.convertNumberToWords(
                                            Integer.parseInt(
                                                beforeInt.replace(
                                                    ",".toRegex(), ""
                                                )
                                            )
                                        )
                                    if (!beforeDec.isEmpty()) {
                                        beforeDec = beforeDec.replace(",".toRegex(), "")
                                        beforeDec = String.format(
                                            Locale.ENGLISH,
                                            "%02d",
                                            Integer.parseInt(beforeDec)
                                        )
                                        amountInWords += " and " + NumberToWord.convertNumberToWords(
                                            Integer.parseInt(beforeDec)
                                        )
                                        amountInWords += " paisa only"
                                    }
                                    txtAmt.text = amountInWords

                                } else {
                                    if (intPart.isEmpty())
                                        intPart = "0"
                                    var amountInWords =
                                        rupeeString + NumberToWord.convertNumberToWords(
                                            Integer.parseInt(intPart)
                                        )

                                    amountInWords += " and " + NumberToWord.convertNumberToWords(
                                        Integer.parseInt(decPart)
                                    )
                                    amountInWords += " paisa only"

                                    txtAmt.text = amountInWords
                                }
                            }

                        } else if (arrayLength == 1) {
                            if (intPart.length > 6) {
                                editText.removeTextChangedListener(this)
                                firstString = commSeperator(beforeInt.replace(",".toRegex(), ""))

                                editText.setText(firstString)
                                editText.setSelection(firstString.length)
                                editText.addTextChangedListener(this)

                                val amountInWords = rupeeString +
                                        NumberToWord.convertNumberToWords(
                                            Integer.parseInt(
                                                beforeInt.replace(
                                                    ",".toRegex(),
                                                    ""
                                                )
                                            )
                                        ) + " only"

                                txtAmt.text = amountInWords

                            } else {
                                editText.removeTextChangedListener(this)
                                firstString = commSeperator(intPart)
                                if (amount.contains("."))
                                    firstString += "."
                                editText.setText(firstString)
                                editText.setSelection(firstString.length)
                                editText.addTextChangedListener(this)
                                val amountInWords = rupeeString + NumberToWord.convertNumberToWords(
                                    Integer.parseInt(intPart)
                                ) + " only"
                                txtAmt.text = amountInWords
                            }
                        }

                    }
                } catch (e: Exception) {
                    if (BizcoreApplication.DEBUG)
                        Log.e("error", e.toString())
                }

            }

            override fun afterTextChanged(s: Editable) {
                //Do nothing
            }
        })
    }

    /*Add comma to a amount string ex: 10024 converted to 10,024*/
    fun commSeperator(originalString: String?): String {
        var originalString = originalString
        if (originalString == null || originalString.isEmpty())
            return ""
        val longval: Long?
        if (originalString.contains(",")) {
            originalString = originalString.replace(",".toRegex(), "")
        }
        longval = java.lang.Long.parseLong(originalString)

        val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,##,##,##,###")
        return formatter.format(longval)
    }

    private fun showPrintDialog() {

        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.print_selection_layout)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val txtMaximus = dialog.findViewById(R.id.txt_maximus)as TextView
        txtMaximus.setOnClickListener {
            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                "Maximus", Snackbar.LENGTH_SHORT
            )
            mySnackbar.show()
            selectedPrinter = "1"
            dialog .dismiss()
        }

        val txtEvalute = dialog.findViewById(R.id.txt_evalute)as TextView
        txtEvalute.setOnClickListener {
            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                "Evalute", Snackbar.LENGTH_SHORT
            )
            mySnackbar.show()
            selectedPrinter = "2"
            dialog .dismiss()
        }

        val txtEvaluteMini = dialog.findViewById(R.id.txt_evalute_mini)as TextView
        txtEvaluteMini.setOnClickListener {
            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                "Evalute mini", Snackbar.LENGTH_SHORT
            )
            mySnackbar.show()
            selectedPrinter = "3"
            dialog .dismiss()
        }

        val txtSoftland = dialog.findViewById(R.id.txt_softland)as TextView
        txtSoftland.setOnClickListener {
            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                "softland", Snackbar.LENGTH_SHORT
            )
            mySnackbar.show()
            selectedPrinter = "4"
            dialog .dismiss()
        }

        val txtSunmi = dialog.findViewById(R.id.txt_sunmi)as TextView
        txtSunmi.setOnClickListener {
            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                "sunmi", Snackbar.LENGTH_SHORT
            )
            mySnackbar.show()
            selectedPrinter = "5"
            dialog .dismiss()
        }

        val okBtn = dialog .findViewById(R.id.btnCANCEL) as Button
        okBtn.setOnClickListener {
            dialog .dismiss()
        }
        dialog .show()
    }

}
