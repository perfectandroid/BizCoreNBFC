package com.perfect.nbfc.Common

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.perfect.nbfc.Api.ApiInterface
import com.perfect.nbfc.Api.ApiService
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.Helper.ConnectivityUtils
import com.perfect.nbfc.Helper.CryptoGraphy
import com.perfect.nbfc.Helper.NumberToWord
import com.perfect.nbfc.Ministatement.Grouplistadaptor
import com.perfect.nbfc.Ministatement.Ministatementadaptor
import com.perfect.nbfc.Offline.Activity.NewCollectionActivity
import com.perfect.nbfc.R
import com.perfect.nbfc.launchingscreens.Login.LoginActivity
import com.perfect.nbfc.locations.Locations
import kotlinx.android.synthetic.main.activity_customer_search.*
import kotlinx.android.synthetic.main.success_deposit_layout.*
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
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*

class CustomerSearchActivity : AppCompatActivity() ,View.OnClickListener{

    var locationManager: LocationManager? = null

    val MyPREFERENCES = "MyBizcore"
    var sharedpreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor?=null
    var Latitude: String = ""
    var Longitude: String = ""
    var addresLine: String = ""
    var Locality: String = ""
    var Area: String = ""
    var Country: String = ""
    var PostalCode: String = ""
    val TAG: String ="CustomerSearchActivity"
    private var progressDialog              : ProgressDialog? = null
    private var strCustName                 : String?=""
    private var strCusName:String?=""
    private var strSubModule:String?=""
    private var strfkaccount:String?=""
    private var strID_ModuleSettings:String?=""
    private var textlength = 0
    private var textlength1 = 0
    private var etxtsearch: EditText? =null
    private var list_view: ListView?=null
    private var etxtsearchtnx: EditText? =null
    private var list_viewtnx: ListView?=null
    private var etxtsearch1: EditText? =null
    private var list_view1: ListView?=null
    private var list_view2: ListView?=null
    private var array_sortGroup =ArrayList<GroupModel>()
    private var array_sorttnx =ArrayList<TnxModel>()
    private var array_sort =ArrayList<ModuleModel>()
    private var searchGroupArrayList = ArrayList<GroupModel>()
    private var searchModuleArrayList = ArrayList<ModuleModel>()
    private var searchTnxArrayList = ArrayList<TnxModel>()
    private var sadapter: ModuleListAdapter? = null
    private var sadaptertnx: TnxListAdapter? = null
    private var sadapterGroup: GroupListAdapter? = null
    private var array_sort1 =ArrayList<CustomerModel>()
    private var searchCustomerArrayList = ArrayList<CustomerModel>()
    private var sadapter1: CustomerListAdapter? = null
    private var llcust: LinearLayout? = null
    private var imback: ImageView? = null
    private var imBalance: ImageView? = null
    private var imTnxHistory: ImageView? = null
    private var edt_txt_module: EditText? = null
    private var edt_txt_mobile: EditText? = null
    private var edt_txt_name: EditText? = null
    private var edt_acc_first: EditText? = null
    private var edt_acc_second: EditText? = null
    private var edt_acc_third: EditText? = null
    private var btnSearch: Button? = null
    private var txt_name: TextView? = null
    private var txt_acno: TextView? = null
    private var tvBal: TextView? = null
    private var tvTnxHistory: TextView? = null
    private var txt_grpname: TextView? = null
    private var rv_grpcustlist: RecyclerView? = null
    private var llBal: LinearLayout? = null
    private var llTnx: LinearLayout? = null
    private var tvBalance: TextView? = null
    private var tvPrincipalamt: TextView? = null
    private var tvInterest          : TextView? = null
    private var tvOthers            : TextView? = null
    private var tvTimeason          : TextView? = null
    private var tvTimeason1         : TextView? = null
    private var tv_reset            : TextView? = null
    private var rvTranscation       : RecyclerView? = null
    private var input_amount        : EditText? = null
    private var txt_grp_name        : EditText? = null
    private var input_grploan_remarks        : EditText? = null
    private var input_msg           : EditText? = null
    private var tv_rupees           : TextView? = null
    private var tvPrincipalamt1     : TextView? = null
    private var tvInterest1         : TextView? = null
    private var tvOthers1           : TextView?       = null
    private var tvBalance1          : TextView?       = null
    private var llsearch            : LinearLayout?   = null
    private var llsearchGrp            : LinearLayout?   = null
    private var btnGrp              : Button?         = null
    private var hashString          : String?         = null
    private var strAmount           : String?         = null
    private var strMsg              : String?         = null
    private var selectedPrinter     : String?         = null
    private var lasttransactionid   : String?         = null
    lateinit var handler            : Handler
    lateinit var r                  : Runnable
    private var avlBal              : Double?         = null
    private var netAmt              : Double?         = null
    private var opBal               : Double?         = null
    var from = ""
    lateinit var dbHelper : DBHandler
    private var result:Boolean? = null
    var remark: Int = 0
    var fk_acc_ind = ""
    var fk_acc_grp = ""
    var strAmnt =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_search)
        initiateViews()
        setRegViews()
        numClick()
        edtFocus()
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())
        tvTimeason!!.text="Due Amount As On "+currentDate

        setEdtTxtAmountCommaSeperator(input_amount!!,tv_rupees,true)

        dbHelper = DBHandler(this)
        dbHelper.deletecollectiontable()
        handler = Handler()
        r = Runnable {
            /* val intent= Intent(this, MPINActivity::class.java)
             startActivity(intent)
             finish()*/
        }
        startHandler()
        var bundle:Bundle = intent.extras!!
        from = bundle.get("from").toString()
        if (from!!.equals("Collection")){
            tv_header.text = "Collection"
        }
        else if (from!!.equals("CusStatement")){
            tv_header.text = "Customer Statement"
        }
        else if (from!!.equals("DemandList")){
            tv_header.text = "Demand List"
        }
        else if (from!!.equals("BalanceEnq")){
            tv_header.text = "Balance Enquiry"
        }



        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
        editor = sharedpreferences!!.edit()
        editor!!.clear()
        editor!!.commit()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        checkGpsOn();

        val service = Intent(this, Locations::class.java)
        startService(service)
       // locationDetails("0")

    }

    @SuppressLint("ResourceAsColor")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.imBalance -> {
                getbal()
            }
            R.id.imTnxHistory -> {
                gettnxHistory()
            }
            R.id.btnGrpSearch -> {
                hideKeyboard(v)
                getGroupPopup("0")
            }
            R.id.btnIndivitualSearch -> {

                val msg: String = edt_acc_third!!.text.toString()

                if(edt_acc_first!!.text.toString()!!.equals("000")&&edt_acc_second!!.text.toString()!!.equals("000")&&edt_acc_third!!.text.toString()!!.equals("000000")){
                    edt_acc_first!!.setText("")
                    edt_acc_second!!.setText("")
                    edt_acc_third!!.setText("")
                }
                if(edt_txt_module!!.getText().toString().isEmpty()){
                    val toast = Toast.makeText(applicationContext, "Please Select Account Type", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
                else if (edt_txt_name!!.getText().toString().isEmpty() && edt_txt_mobile!!.getText().toString().isEmpty() && edt_acc_first!!.text.toString().isEmpty() && edt_acc_second!!.text.toString().isEmpty() && edt_acc_third!!.text.toString().isEmpty() ){

                    val toast = Toast.makeText(applicationContext, "Please Enter Name, Phone Number Or Account Number", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }else if(!edt_txt_name!!.getText().toString().isEmpty()){
                    if (edt_txt_name!!.getText().toString().length >= 3){
                        if (msg.length != 0 ){
                            elsevalidation()
                        }
                        else{
                            fetchData()
                        }

                    }else{
                        val toast = Toast.makeText(applicationContext, "Please Enter Min 3 Digit Of Name.", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()

                        edt_txt_name!!.setText("")

                        elsevalidation()
                        return
                    }

                }else if( !edt_txt_mobile!!.getText().toString().isEmpty()){

                    if (edt_txt_mobile!!.getText().toString().length >= 10){
                        if (msg.length != 0 ){
                            elsevalidation()
                        }
                        else{
                            fetchData()
                        }
                    }else{
                        val toast = Toast.makeText(applicationContext, "Please Enter 10 Digit Mobile Number.", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        edt_txt_mobile!!.setText("")

                        elsevalidation()
                        return
                    }

                }else  {

                    elsevalidation()
                }

            }
            R.id.edt_txt_module -> {
                getModulePopup()
            }
            R.id.imback -> {
                finish()
            }
            R.id.btName -> {
                edt_txt_name!!.visibility = View.VISIBLE
                edt_txt_mobile!!.visibility = View.GONE
                edt_txt_mobile!!.setText("")

                btName?.setBackgroundResource(R.drawable.toggle_n)
                btMob?.setBackgroundResource(R.drawable.toggle_m)
            }
            R.id.btMob -> {
                edt_txt_mobile!!.visibility = View.VISIBLE
                edt_txt_name!!.visibility = View.GONE
                edt_txt_name!!.setText("")
                btName?.setBackgroundResource(R.drawable.toggle_n1)
                btMob?.setBackgroundResource(R.drawable.toggle_m1)

            }



            R.id.tv_reset -> {
                doReset()
            }
            R.id.tv_send_group -> {
                remark = 2
                locationDetails("1")
            }
            R.id.tv_send_individual->{
                remark = 0
                locationDetails("2")
            }
            R.id.tv_send_remark->{
                if(strSubModule!!.equals("TLGP")) {
                    if(input_grploan_remarks!!.text.toString().equals("")){
                        val toast = Toast.makeText(applicationContext, "Please Enter Remarks.", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }
                    else{
                        remark = 3
                        locationDetails("1")
                    }
                }
                else{
                    if(input_msg!!.text.toString().equals("")){
                        val toast = Toast.makeText(applicationContext, "Please Enter Remarks.", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }
                    else{
                        remark = 1
                        locationDetails("2")
                    }
                }
            }
        }
    }

    fun  elsevalidation(){
        val msg0 :String = edt_acc_first!!.text.toString()
        val msg1 :String = edt_acc_second!!.text.toString()
        val msg: String = edt_acc_third!!.text.toString()

        if (msg0.isEmpty() && msg1.isEmpty() && msg.isEmpty()){
            return
        }

        if (msg0.length==1){
            edt_acc_first!!.setText("00"+msg0)
            edt_acc_first!!.setSelection(edt_acc_first!!.getText().length)
        }
        if (msg0.length==2){
            edt_acc_first!!.setText("0"+msg0)
            edt_acc_first!!.setSelection(edt_acc_first!!.getText().length)

        }
        if (msg0.length==3){
            edt_acc_first!!.setText(msg0)
            edt_acc_first!!.setSelection(edt_acc_first!!.getText().length)

        }
        if(msg0.length==0){
            edt_acc_first!!.setText("000")
            edt_acc_first!!.setSelection(edt_acc_first!!.getText().length)
        }

        if (msg1.length==1){
            edt_acc_second!!.setText("00"+msg1)
            edt_acc_second!!.setSelection(edt_acc_second!!.getText().length)
        }
        if (msg1.length==2){
            edt_acc_second!!.setText("0"+msg1)
            edt_acc_second!!.setSelection(edt_acc_second!!.getText().length)

        }
        if (msg1.length==3){
            edt_acc_second!!.setText(msg1)
            edt_acc_second!!.setSelection(edt_acc_second!!.getText().length)

        }
        if (msg1.length==0){
            edt_acc_second!!.setText("000")
            edt_acc_second!!.setSelection(edt_acc_second!!.getText().length)

        }

        if (msg.length==1){
            edt_acc_third!!.setText("00000"+msg)
            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
        }

        if (msg.length==2){
            edt_acc_third!!.setText("0000"+msg)
            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
        }

        if (msg.length==3){
            edt_acc_third!!.setText("000"+msg)
            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
        }

        if (msg.length==4){
            edt_acc_third!!.setText("00"+msg)
            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
        }

        if (msg.length==5){
            edt_acc_third!!.setText("0"+msg)
            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
        }

        if (msg.length==6){
            edt_acc_third!!.setText(msg)
            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
        }
        if (msg.length==0){
            edt_acc_third!!.setText("000000")
            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
        }

        if (edt_acc_first!!.text.toString().equals("000")&&edt_acc_second!!.text.toString().equals("000")&&edt_acc_third!!.text.toString().equals("000000")){
//            Toast.makeText(applicationContext,"Please enter valid account number",Toast.LENGTH_LONG).show()
            val toast = Toast.makeText(applicationContext, "Please Enter Valid Account Number", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return
        }
        if (edt_acc_third!!.text.toString().equals("000000")){

            val toast = Toast.makeText(applicationContext, "Please Enter Valid Account Number", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()

            return
        }
        fetchData()





//        if(msg.length==4){
//            edt_acc_third!!.setText("00"+msg)
//            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
//            fetchData()
//        }
//        else if(msg.length==5 ){
//            edt_acc_third!!.setText("0"+msg)
//            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
//        }
//        else if(msg.length==3||msg.startsWith("000"))
//        {
//            edt_acc_third!!.setText("000"+msg)
//            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
//        }
//        else if(msg.length==2||msg.startsWith("0000"))
//        {
//            edt_acc_third!!.setText("0000"+msg)
//            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
//        }
//        else if(msg.length==1||msg.startsWith("00000"))
//        {
//            edt_acc_third!!.setText("00000"+msg)
//            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
//            Toast.makeText(applicationContext,"Invalid Account Number",Toast.LENGTH_LONG).show()
//        }
//        if(msg.equals("")||msg.equals("0")||msg.equals("00")||msg.equals("000")||msg.equals("0000")||msg.equals("00000")||msg.equals("000000"))
//        {
//            edt_acc_first!!.setText("000")
//            edt_acc_second!!.setText("000")
//            edt_acc_third!!.setText("000000")
//            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
//            if(msg.length==6 && (!"000000".equals(msg))){
//                edt_acc_third!!.setText(msg)
//                edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
//                fetchData()
//            }else{
//                Toast.makeText(applicationContext,"Invalid Account Number",Toast.LENGTH_LONG).show()
//            }
//
//        }
//        else if(msg.length==6 && (!"000000".equals(msg)))
//        {
//            edt_acc_third!!.setText(msg)
//            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
//            fetchData()
//        }
    }

    private fun initiateViews() {
        llsearch =findViewById(R.id.llsearch)
        llsearchGrp =findViewById(R.id.llsearchGrp)
        btnGrp =findViewById(R.id.btnGrpSearch)
        txt_grpname =findViewById(R.id.txt_grpname)
        rv_grpcustlist =findViewById(R.id.rv_grpcustlist)
        rvTranscation =findViewById(R.id.rvTranscation)
        edt_txt_module =findViewById(R.id.edt_txt_module)
        imback=findViewById(R.id.imback)
        btnSearch=findViewById(R.id.btnIndivitualSearch)
        edt_txt_mobile=findViewById(R.id.edt_txt_mobile)
        edt_txt_name=findViewById(R.id.edt_txt_name)
        edt_acc_first=findViewById(R.id.edt_acc_first)
        edt_acc_second=findViewById(R.id.edt_acc_second)
        edt_acc_third=findViewById(R.id.edt_acc_third)
        txt_name=findViewById(R.id.txt_name)
        txt_acno=findViewById(R.id.txt_acno)
        imBalance=findViewById(R.id.imBalance)
        imTnxHistory=findViewById(R.id.imTnxHistory)
        llcust=findViewById(R.id.llcust)
        tvBal=findViewById(R.id.tvBal)
        llBal=findViewById(R.id.llBal)
        llTnx=findViewById(R.id.llTnx)
        tvTnxHistory=findViewById(R.id.tvTnxHistory)
        tvBalance=findViewById(R.id.tvBalance)
        tvPrincipalamt=findViewById(R.id.tvPrincipalamt)
        tvInterest=findViewById(R.id.tvInterest)
        tvOthers=findViewById(R.id.tvOthers)
        tvTimeason=findViewById(R.id.tvTimeason)
        tv_reset=findViewById(R.id.tv_reset)
        input_amount=findViewById(R.id.input_amount)
        txt_grp_name=findViewById(R.id.edt_txt_grp_name)
        input_msg=findViewById(R.id.input_msg)
        tv_rupees=findViewById(R.id.tv_rupees)
        input_grploan_remarks=findViewById(R.id.input_grploan_remarks)
    }

    private fun setRegViews() {
        btName!!.setOnClickListener(this)
        btMob!!.setOnClickListener(this)
        btnGrp!!.setOnClickListener(this)
        tv_reset!!.setOnClickListener(this)
        tvBal!!.setOnClickListener(this)
        tvTnxHistory!!.setOnClickListener(this)
        imTnxHistory!!.setOnClickListener(this)
        imBalance!!.setOnClickListener(this)
        tv_send_group!!.setOnClickListener(this)
        tv_send_remark!!.setOnClickListener(this)
        tv_send_individual!!.setOnClickListener(this)
        edt_txt_module!!.setOnClickListener(this)
        imback?.setOnClickListener(this)
        btnSearch?.setOnClickListener(this)
        edt_txt_module!!.keyListener=null
        llcust!!.visibility=View.GONE
        imBalance!!.visibility=View.INVISIBLE
        imTnxHistory!!.visibility=View.INVISIBLE
    }


    private fun fetchData(){
//          if(strSubModule!!.equals("TLGP")){
//              getCustomer("0")
//          }
//          else {
        getCustomer("0")
//          }
    }

    private fun doReset() {
        val intent= Intent(this, CustomerSearchActivity::class.java)
        intent.putExtra("from", from)
        startActivity(intent)
        finish()
    }



    private fun getModulePopup() {
        try {
            val builder = AlertDialog.Builder(this)
            val inflater1 =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater1.inflate(R.layout.modulelist_popup, null)
            list_view = layout.findViewById(R.id.list_view)
            etxtsearch  = layout.findViewById(R.id.etsearch)
            val tv_popuptitle = layout.findViewById(R.id.tv_popuptitle) as TextView
            tv_popuptitle.setText("Choose Module")
            builder.setView(layout)
            val alertDialog = builder.create()
            getModuleList(alertDialog)
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getModuleList(dialog: AlertDialog) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
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


                        val agentId = "0000"
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss.SSS",
                            Locale.ENGLISH
                        )
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

                        requestObject1.put("LoginMode", BizcoreApplication.encryptMessage("0"))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(
                                    R.string.BankKey
                                )))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(
                                    R.string.BankHeader
                                )))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")
                        requestObject1.put("Module", BizcoreApplication.encryptMessage("0"))
                        requestObject1.put("Groupid", BizcoreApplication.encryptMessage("0"))
                        requestObject1.put("TransType", BizcoreApplication.encryptMessage("R"))

                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(
                            findViewById(R.id.rl_main),
                            " Some Technical Issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }

                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        requestObject1.toString()
                    )
                    val call = apiService.getLoginLogin(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                            call: retrofit2.Call<String>, response:
                            Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {
                                    val jobjt = jObject.getJSONObject("ModuleList")
                                    if (jobjt.getString("ModuleListDetails") == "null") {
                                    } else {
                                        val jarray = jobjt.getJSONArray("ModuleListDetails")
                                        array_sort = java.util.ArrayList<ModuleModel>()
                                        searchModuleArrayList = ArrayList<ModuleModel>()
                                        for (k in 0 until jarray.length()) {
                                            val jsonObject = jarray.getJSONObject(k)

                                            searchModuleArrayList.add(
                                                ModuleModel(
                                                    jsonObject.getString("ModuleName"),
                                                    jsonObject.getString("SubModule"),
                                                    jsonObject.getString("TransType"),
                                                    jsonObject.getString("Groupid"),
                                                    jsonObject.getString("ID_ModuleSettings")
                                                )
                                            )
                                            array_sort.add(
                                                ModuleModel(
                                                    jsonObject.getString("ModuleName"),
                                                    jsonObject.getString("SubModule"),
                                                    jsonObject.getString("TransType"),
                                                    jsonObject.getString("Groupid"),
                                                    jsonObject.getString("ID_ModuleSettings")
                                                )
                                            )
                                        }

                                        sadapter = ModuleListAdapter(this@CustomerSearchActivity, array_sort)
                                        list_view!!.setAdapter(sadapter)
                                        list_view!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->

                                            cv_groupcollection!!.visibility = View.GONE
                                            cv_collection!!.visibility = View.GONE
                                            cv_demandlist!!.visibility = View.GONE
                                            cv_ministatement!!.visibility = View.GONE
                                            cv_balanceenq!!.visibility = View.GONE
                                            tv_send_group!!.visibility = View.GONE
                                            tv_send_remark!!.visibility = View.GONE
                                            tv_send_individual!!.visibility = View.GONE


                                            array_sort.get(position).ModuleName
                                            edt_txt_module!!.setText(array_sort[position].ModuleName)
                                            strSubModule = array_sort[position].SubModule
                                            strID_ModuleSettings = array_sort[position].ID_ModuleSettings

                                            if(strSubModule!!.equals("TLGP")){
                                                llsearch!!.visibility=View.GONE
                                                llsearchGrp!!.visibility=View.VISIBLE
                                                btnGrp!!.visibility=View.VISIBLE
                                                input_amount!!.setText("")
                                                txt_grp_name!!.setText("")
                                                input_msg!!.setText("")
                                                edt_txt_name!!.setText("")
                                                edt_acc_first!!.setText("")
                                                edt_acc_second!!.setText("")
                                                edt_acc_third!!.setText("")
                                            }else {
                                                llsearch!!.visibility=View.VISIBLE
                                                llsearchGrp!!.visibility=View.GONE
                                                btnGrp!!.visibility=View.GONE
                                                input_amount!!.setText("")
                                                txt_grp_name!!.setText("")
                                                input_msg!!.setText("")
                                                edt_txt_name!!.setText("")
                                                edt_acc_first!!.setText("")
                                                edt_acc_second!!.setText("")
                                                edt_acc_third!!.setText("")
                                            }
                                            dialog.dismiss()
                                        })
                                    }
                                    etxtsearch!!.addTextChangedListener(object : TextWatcher {
                                        override fun afterTextChanged(p0: Editable?) {}

                                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                                            list_view!!.setVisibility(View.VISIBLE)
                                            textlength = etxtsearch!!.text.length
                                            array_sort.clear()
                                            for (i in searchModuleArrayList.indices) {
                                                if (textlength <= searchModuleArrayList[i].ModuleName!!.length) {
                                                    if (searchModuleArrayList[i].ModuleName!!.toLowerCase()
                                                            .trim().contains(
                                                                etxtsearch!!.text.toString()
                                                                    .toLowerCase()
                                                                    .trim { it <= ' ' })
                                                    ) {
                                                        array_sort.add(searchModuleArrayList[i])
                                                    }
                                                }
                                            }
                                            sadapter = ModuleListAdapter(this@CustomerSearchActivity, array_sort)
                                            list_view!!.adapter = sadapter
                                        }
                                    })
                                }
                                else if (jObject.getString("StatusCode") == "-12") {
                                    val jobjt = jObject.getJSONObject("ModuleList")
                                    val dialogBuilder = AlertDialog.Builder(
                                        this@CustomerSearchActivity,
                                        R.style.MyDialogTheme
                                    )
                                    dialogBuilder.setMessage(jobjt.getString("ResponseMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "OK",
                                            DialogInterface.OnClickListener { dialog, id ->
                                                dialog.dismiss()

                                            })
                                        .setCancelable(false);
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }
                                else {
                                    val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity, R.style.MyDialogTheme)
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
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                val mySnackbar = Snackbar.make(
                                    findViewById(R.id.rl_main),
                                    " Some Technical Issues.", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some Technical Issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })

                } catch (e: Exception) {
                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(
                        findViewById(R.id.rl_main),
                        " Some Technical Issues.", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }
            }
            false -> {
                val mySnackbar = Snackbar.make(
                    findViewById(R.id.rl_main),
                    "No Internet Connection!!",
                    Snackbar.LENGTH_SHORT
                )
                mySnackbar.show()
            }
        }

    }



    private fun getCustomer(Loginmode: String) {
        try {
            val builder = AlertDialog.Builder(this)
            val inflater1 = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater1.inflate(R.layout.customerlist_popup, null)
            list_view1 = layout.findViewById(R.id.list_view1)
            etxtsearch1  = layout.findViewById(R.id.etsearch1)
            val tv_popuptitle1 = layout.findViewById(R.id.tv_popuptitle1) as TextView
            tv_popuptitle1.setText("Customer List")
            builder.setView(layout)
            val alertDialog = builder.create()
            doCusSearch(alertDialog,Loginmode)
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun doCusSearch(layoutdialog: AlertDialog, Loginmode: String) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
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

                        val AgentIdSP = applicationContext.getSharedPreferences(
                            BizcoreApplication.SHARED_PREF1,
                            0
                        )
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("LoginMode", BizcoreApplication.encryptMessage(Loginmode))
                        requestObject1.put("CustomerId", BizcoreApplication.encryptMessage("0"))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(
                            R.string.BankKey
                        )))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(
                            R.string.BankHeader
                        )))
                        requestObject1.put(
                            "BankVerified",
                            "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg="
                        )
                        requestObject1.put(
                            "Module",
                            BizcoreApplication.encryptMessage(strSubModule)
                        )
                        requestObject1.put(
                            "Name",
                            BizcoreApplication.encryptMessage(edt_txt_name!!.text.toString())
                        )
                        requestObject1.put(
                            "MobileNumber", BizcoreApplication.encryptMessage(
                                edt_txt_mobile!!.text.toString()
                            )
                        )
                        requestObject1.put(
                            "AccountNumber", BizcoreApplication.encryptMessage(
                                edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString()
                            )
                        )


                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(
                            findViewById(R.id.rl_main),
                            " Some Technical Issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }

                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        requestObject1.toString()
                    )
                    val call = apiService.getCustomerSerachDetails(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                            call: retrofit2.Call<String>, response:
                            Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                Log.i("Response",response.body())
                                if (jObject.getString("StatusCode") == "0") {


                                    val jobjt = jObject.getJSONObject("CustomerSerachDetails")
                                    if (jobjt.getString("CustomerSerachDetailsList") != "null") {
                                        val jarray = jobjt.getJSONArray("CustomerSerachDetailsList")
                                        array_sort1 = java.util.ArrayList<CustomerModel>()
                                        searchCustomerArrayList = ArrayList<CustomerModel>()
                                        for (k in 0 until jarray.length()) {
                                            val jsonObject = jarray.getJSONObject(k)

                                            searchCustomerArrayList.add(
                                                CustomerModel(
                                                    jsonObject.getString("Module"),
                                                    jsonObject.getString("Name"),
                                                    jsonObject.getString("CustomerId"),
                                                    jsonObject.getString("FK_Account"),
                                                    jsonObject.getString("AccountNumber"),
                                                    jsonObject.getString("Address"),
                                                    jsonObject.getString("MobileNumber"),
                                                    jsonObject.getString("LastTransactionId"))

                                            )

                                            array_sort1.add(
                                                CustomerModel(
                                                    jsonObject.getString("Module"),
                                                    jsonObject.getString("Name"),
                                                    jsonObject.getString("CustomerId"),
                                                    jsonObject.getString("FK_Account"),
                                                    jsonObject.getString("AccountNumber"),
                                                    jsonObject.getString("Address"),
                                                    jsonObject.getString("MobileNumber"),
                                                    jsonObject.getString("LastTransactionId"))

                                            )
                                        }

                                        sadapter1 = CustomerListAdapter(this@CustomerSearchActivity, array_sort1)
                                        list_view1!!.setAdapter(sadapter1)
                                        list_view1!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->

                                            fk_acc_ind = (array_sort1.get(position).FK_Account).toString()

                                            array_sort1.get(position).Name
                                            strfkaccount = array_sort1.get(position).FK_Account
                                            strCustName = array_sort1.get(position).Name + ", " + array_sort1.get(position).Address
                                            strCusName = array_sort1.get(position).Name
                                            lasttransactionid = array_sort1.get(position).LastTransactionId


                                            val accno = array_sort1.get(position).AccountNumber
                                            val f1: String = accno!!.substring(0, accno!!.length / 4) // gives "How ar"
                                            val f2: String = accno!!.substring(accno!!.length / 2)
                                            val f3: String = accno!!.substring(0, accno!!.length / 2)
                                            val f5 = f3.substring(f3.length / 2)

                                            edt_acc_first!!.setText(f1)
                                            edt_acc_second!!.setText(f5)
                                            edt_acc_third!!.setText(f2)


                                            if (from!!.equals("Collection")){

                                                cv_collection.visibility = View.VISIBLE
                                                tv_send_individual!!.visibility = View.VISIBLE
                                                tv_send_remark!!.visibility = View.VISIBLE
                                                llcust!!.visibility = View.VISIBLE
                                                input_amount!!.requestFocus()

                                                imBalance!!.visibility = View.VISIBLE
                                                imTnxHistory!!.visibility = View.VISIBLE
                                                txt_name!!.text = array_sort1.get(position).Name + ", " + array_sort1.get(position).Address+"\n"+"Ac/No :" + array_sort1.get(position).AccountNumber
                                                txt_acno!!.text = "Ac/No :" + array_sort1.get(position).AccountNumber

                                            }
                                            if (from!!.equals("DemandList")){
                                                LoadRetrofitDemandlist(strfkaccount.toString())
                                            }
                                            if (from!!.equals("CusStatement")) {

                                                txt_min_name!!.text = array_sort1.get(position).Name
                                                txt_min_mob!!.text = array_sort1.get(position).MobileNumber
                                                txt_min_acno!!.text = array_sort1.get(position).AccountNumber
                                                Loadministatementlisting(array_sort1.get(position).AccountNumber.toString())
                                            }
                                            if (from!!.equals("BalanceEnq")) {

                                                txt_blnc_name!!.text = array_sort1.get(position).Name
                                                txt_blnc_mob!!.text = array_sort1.get(position).MobileNumber
                                                txt_blnc_acno!!.text = array_sort1.get(position).AccountNumber
                                                getAccountDetails(array_sort1.get(position).AccountNumber.toString())
                                            }

                                            layoutdialog.dismiss()
                                        })
                                    }
                                    etxtsearch1!!.addTextChangedListener(object : TextWatcher {
                                        override fun afterTextChanged(p0: Editable?) {
                                        }

                                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                                        }

                                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                                            list_view1!!.setVisibility(View.VISIBLE)
                                            textlength1 = etxtsearch1!!.text.length
                                            array_sort1.clear()
                                            for (i in searchCustomerArrayList.indices) {
                                                if (textlength1 <= searchCustomerArrayList[i].Name!!.length) {
                                                    if (searchCustomerArrayList[i].Name!!.toLowerCase()
                                                            .trim().contains(
                                                                etxtsearch1!!.text.toString()
                                                                    .toLowerCase()
                                                                    .trim { it <= ' ' })
                                                    ) {
                                                        array_sort1.add(searchCustomerArrayList[i])
                                                    }
                                                }
                                            }
                                            sadapter1 = CustomerListAdapter(
                                                this@CustomerSearchActivity,
                                                array_sort1
                                            )
                                            list_view1!!.adapter = sadapter1
                                        }
                                    })
                                }
                                else{
//                                    if (from!!.equals("Collection")){
//                                        cv_collection.visibility = View.GONE
//                                        tv_send!!.visibility = View.GONE
//                                    }
                                    layoutdialog.dismiss()
                                    if (jObject.getString("StatusCode") == "-12") {

                                        val jobjt = jObject.getJSONObject("CustomerSerachDetails")
                                        val dialogBuilder = AlertDialog.Builder(
                                            this@CustomerSearchActivity,
                                            R.style.MyDialogTheme
                                        )
                                        dialogBuilder.setMessage(jobjt.getString("ResponseMessage"))
                                            .setCancelable(false)
                                            .setPositiveButton(
                                                "OK",
                                                DialogInterface.OnClickListener { dialog, id ->
                                                    dialog.dismiss()

                                                })
                                            .setCancelable(false);
                                        val alert = dialogBuilder.create()
                                        alert.show()
                                        val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                        pbutton.setTextColor(Color.MAGENTA)
                                    }
                                    else {

                                        val dialogBuilder = AlertDialog.Builder(
                                            this@CustomerSearchActivity,
                                            R.style.MyDialogTheme
                                        )
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
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                val mySnackbar = Snackbar.make(
                                    findViewById(R.id.rl_main),
                                    " Some technical issues.", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })

                } catch (e: Exception) {
                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(
                        findViewById(R.id.rl_main),
                        " Some technical issues.", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }
            }
            false -> {
                val mySnackbar = Snackbar.make(
                    findViewById(R.id.rl_main),
                    "No Internet Connection!!",
                    Snackbar.LENGTH_SHORT
                )
                mySnackbar.show()
            }
        }
    }



    private fun getbal() {
        try {
            val builder = AlertDialog.Builder(this)
            val inflater1 = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater1.inflate(R.layout.ballist_popup, null)

            tvTimeason1  = layout.findViewById(R.id.tvTimeason1)
            tvBalance1  = layout.findViewById(R.id.tvBalance1)
            tvPrincipalamt1  = layout.findViewById(R.id.tvPrincipalamt1)
            tvInterest1  = layout.findViewById(R.id.tvInterest1)
            tvOthers1  = layout.findViewById(R.id.tvOthers1)
            val tv_popuptitle = layout.findViewById(R.id.tv_popuptitle) as TextView
            tv_popuptitle.setText("Balance Details")
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val currentDate = sdf.format(Date())
            tvTimeason1!!.text="Due Amount As On "+currentDate
            builder.setView(layout)
            val alertDialog = builder.create()
            getbalanceenqsplit()
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getbalanceenqsplit() {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
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
//                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
//                        var Imei = DeviceAppDetails.imei
//                        if (Imei != null && !Imei.isEmpty()) {
//                        }else{
//                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
//                            Imei = DeviceAppDetails1.imei
//                        }

                        val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                        var Imei = DeviceAppDetails1.imei


                        val AgentIdSP = applicationContext.getSharedPreferences(
                            BizcoreApplication.SHARED_PREF1,
                            0
                        )
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put(
                            "Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(
                                Imei
                            )
                        )
                        requestObject1.put(
                            "BankKey", BizcoreApplication.encryptMessage(
                                getResources().getString(
                                    R.string.BankKey
                                )
                            )
                        )
                        requestObject1.put(
                            "BankHeader", BizcoreApplication.encryptMessage(
                                getResources().getString(
                                    R.string.BankHeader
                                )
                            )
                        )
                        requestObject1.put(
                            "BankVerified",
                            "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg="
                        )
                        requestObject1.put(
                            "Module",
                            BizcoreApplication.encryptMessage(strSubModule)
                        )
                        requestObject1.put(
                            "AccountNumber", BizcoreApplication.encryptMessage(
                                edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString()
                            )
                        )
                        requestObject1.put("FK_Account", BizcoreApplication.encryptMessage(strfkaccount))
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(
                            findViewById(R.id.rl_main),
                            " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }

                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        requestObject1.toString()
                    )
                    val call = apiService.getbalsplit(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                            call: retrofit2.Call<String>, response:
                            Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {

                                    val jobjt = jObject.getJSONObject("BalanceEnquirySplitupList")

                                    tvBalance1!!.text       ="₹ " + jobjt.getString("AvailableBalance").toString()
                                    tvPrincipalamt1!!.text  ="₹ " + jobjt.getString("Principal").toString()
                                    tvInterest1!!.text      ="₹ " + jobjt.getString("Interest").toString()
                                    tvOthers1!!.text        ="₹ " + jobjt.getString("Others").toString()

                                }
                                else if (jObject.getString("StatusCode") == "-12") {
                                    val jobjt = jObject.getJSONObject("CustomerSerachDetails")
                                    val dialogBuilder = AlertDialog.Builder(
                                        this@CustomerSearchActivity,
                                        R.style.MyDialogTheme
                                    )
                                    dialogBuilder.setMessage(jobjt.getString("ResponseMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "OK",
                                            DialogInterface.OnClickListener { dialog, id ->
                                                dialog.dismiss()

                                            })
                                        .setCancelable(false);
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                } else {
                                    val dialogBuilder = AlertDialog.Builder(
                                        this@CustomerSearchActivity,
                                        R.style.MyDialogTheme
                                    )
                                    dialogBuilder.setMessage(jObject.getString("EXMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                                                dialog.dismiss()

                                            })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                val mySnackbar = Snackbar.make(
                                    findViewById(R.id.rl_main),
                                    " Some technical issues.", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })

                } catch (e: Exception) {
                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(
                        findViewById(R.id.rl_main),
                        " Some technical issues.", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }
            }
            false -> {
                val mySnackbar = Snackbar.make(
                    findViewById(R.id.rl_main),
                    "No Internet Connection!!",
                    Snackbar.LENGTH_SHORT
                )
                mySnackbar.show()
            }
        }

    }



    private fun gettnxHistory() {
        try {
            val builder = AlertDialog.Builder(this)
            val inflater1 =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater1.inflate(R.layout.tnxhistory_popup, null)
            list_viewtnx = layout.findViewById(R.id.list_view)
            etxtsearchtnx  = layout.findViewById(R.id.etsearch)

            builder.setView(layout)
            val alertDialog = builder.create()
            getTnxList(alertDialog)
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTnxList(dialog: AlertDialog) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
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
//                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
//                        var Imei = DeviceAppDetails.imei
//                        if (Imei != null && !Imei.isEmpty()) {
//                        }else{
//                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
//                            Imei = DeviceAppDetails1.imei
//                        }

                        val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                        var Imei = DeviceAppDetails1.imei


                        val AgentIdSP = applicationContext.getSharedPreferences(
                            BizcoreApplication.SHARED_PREF1,
                            0
                        )
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put(
                            "Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(
                                Imei
                            )
                        )
                        requestObject1.put(
                            "BankKey", BizcoreApplication.encryptMessage(
                                getResources().getString(
                                    R.string.BankKey
                                )
                            )
                        )
                        requestObject1.put(
                            "BankHeader", BizcoreApplication.encryptMessage(
                                getResources().getString(
                                    R.string.BankHeader
                                )
                            )
                        )
                        requestObject1.put(
                            "BankVerified",
                            "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg="
                        )
                        requestObject1.put(
                            "Module",
                            BizcoreApplication.encryptMessage(strSubModule)
                        )
                        requestObject1.put(
                            "AccountNumber", BizcoreApplication.encryptMessage(
                                edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString()
                            )
                        )
                        requestObject1.put("FK_Account", BizcoreApplication.encryptMessage(strfkaccount))
                        requestObject1.put("LoginMode", BizcoreApplication.encryptMessage("2"))


                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(
                            findViewById(R.id.rl_main),
                            " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }

                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        requestObject1.toString()
                    )
                    val call = apiService.getTransactionhistory(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                            call: retrofit2.Call<String>, response:
                            Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {

                                    val jobjt = jObject.getJSONObject("CustomerSearchTransactionDetails")
                                    if (jobjt.getString("CustomerSearchTransactionDetailsList") == "null") {
                                    } else {
                                        val jarray = jobjt.getJSONArray("CustomerSearchTransactionDetailsList")
                                        array_sorttnx = java.util.ArrayList<TnxModel>()
                                        searchTnxArrayList = ArrayList<TnxModel>()
                                        for (k in 0 until jarray.length()) {
                                            val jsonObject = jarray.getJSONObject(k)

                                            searchTnxArrayList.add(
                                                TnxModel(
                                                    jsonObject.getString("Time"),
                                                    jsonObject.getString("Amount"),
                                                    jsonObject.getString("ReferenceNumber")
                                                )
                                            )
                                            array_sorttnx.add(
                                                TnxModel(
                                                    jsonObject.getString("Time"),
                                                    jsonObject.getString("Amount"),
                                                    jsonObject.getString("ReferenceNumber")
                                                )
                                            )
                                        }

                                        sadaptertnx =TnxListAdapter(
                                            this@CustomerSearchActivity,
                                            array_sorttnx
                                        )
                                        list_viewtnx!!.setAdapter(sadaptertnx)
                                        /*    list_view!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                                             array_sort.get(position).ModuleName
                                             edt_txt_module!!.setText(array_sort[position].ModuleName)
                                             strSubModule = array_sort[position].SubModule
                                             strID_ModuleSettings =
                                                 array_sort[position].ID_ModuleSettings
                                             //strBranchTypecode=array_sort[position].BranchCode
                                             dialog.dismiss()
                                         })*/
                                    }
/*
                                    etxtsearch!!.addTextChangedListener(object : TextWatcher {
                                        override fun afterTextChanged(p0: Editable?) {
                                        }

                                        override fun beforeTextChanged(
                                            p0: CharSequence?,
                                            p1: Int,
                                            p2: Int,
                                            p3: Int
                                        ) {
                                        }

                                        override fun onTextChanged(
                                            p0: CharSequence?,
                                            p1: Int,
                                            p2: Int,
                                            p3: Int
                                        ) {

                                            list_view!!.setVisibility(View.VISIBLE)
                                            textlength = etxtsearch!!.text.length
                                            array_sort.clear()
                                            for (i in searchModuleArrayList.indices) {
                                                if (textlength <= searchModuleArrayList[i].ModuleName!!.length) {
                                                    if (searchModuleArrayList[i].ModuleName!!.toLowerCase()
                                                            .trim().contains(
                                                                etxtsearch!!.text.toString()
                                                                    .toLowerCase()
                                                                    .trim { it <= ' ' })
                                                    ) {
                                                        array_sort.add(searchModuleArrayList[i])
                                                    }
                                                }
                                            }
                                            sadapter = ModuleListAdapter(
                                                this@CustomerSearchActivity,
                                                array_sort
                                            )
                                            list_view!!.adapter = sadapter
                                        }
                                    })
*/


                                } else if (jObject.getString("StatusCode") == "-12") {
                                    val jobjt = jObject.getJSONObject("CustomerSearchTransactionDetails")
                                    val dialogBuilder = AlertDialog.Builder(
                                        this@CustomerSearchActivity,
                                        R.style.MyDialogTheme
                                    )
                                    dialogBuilder.setMessage(jobjt.getString("ResponseMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "OK",
                                            DialogInterface.OnClickListener { dialog, id ->
                                                dialog.dismiss()

                                            })
                                        .setCancelable(false);
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                } else {
                                    // val jobjt = jObject.getJSONObject("LogInfo")
                                    val dialogBuilder = AlertDialog.Builder(
                                        this@CustomerSearchActivity,
                                        R.style.MyDialogTheme
                                    )
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
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                val mySnackbar = Snackbar.make(
                                    findViewById(R.id.rl_main),
                                    " Some technical issues.", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })

                } catch (e: Exception) {
                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(
                        findViewById(R.id.rl_main),
                        " Some technical issues.", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }
            }
            false -> {
                val mySnackbar = Snackbar.make(
                    findViewById(R.id.rl_main),
                    "No Internet Connection!!",
                    Snackbar.LENGTH_SHORT
                )
                mySnackbar.show()
            }
        }

    }



    private fun getGroupPopup(Loginmode: String) {
        try {
            val builder = AlertDialog.Builder(this)
            val inflater1 = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater1.inflate(R.layout.grouplist_popup, null)
            list_view2 = layout.findViewById(R.id.list_view1)
            val tv_popuptitle1 = layout.findViewById(R.id.tv_popuptitle1) as TextView
            tv_popuptitle1.setText("Group List")
            builder.setView(layout)
            val alertDialog = builder.create()
            dogroupSearch(alertDialog,Loginmode)
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dogroupSearch(layoutdialog: AlertDialog, Loginmode: String) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
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

                        val AgentIdSP = applicationContext.getSharedPreferences(
                            BizcoreApplication.SHARED_PREF1,
                            0
                        )
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("LoginMode", BizcoreApplication.encryptMessage(Loginmode))
                        requestObject1.put("CustomerId", BizcoreApplication.encryptMessage("0"))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(
                            R.string.BankKey
                        )))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(
                            R.string.BankHeader
                        )))
                        requestObject1.put(
                            "BankVerified",
                            "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg="
                        )
                        requestObject1.put(
                            "Module",
                            BizcoreApplication.encryptMessage(strSubModule)
                        )
//                        requestObject1.put(
//                            "Name",
//                            BizcoreApplication.encryptMessage(edt_txt_name!!.text.toString())
//                        )
                        requestObject1.put(
                            "Name",
                            BizcoreApplication.encryptMessage(edt_txt_grp_name!!.text.toString())
                        )
                        requestObject1.put(
                            "MobileNumber", BizcoreApplication.encryptMessage(
                                edt_txt_mobile!!.text.toString()
                            )
                        )
                        requestObject1.put(
                            "AccountNumber", BizcoreApplication.encryptMessage(
                                edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString()
                            )
                        )

                        Log.e(TAG,"requestObject1    1854    "+requestObject1)


                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(
                            findViewById(R.id.rl_main),
                            " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }

                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        requestObject1.toString()
                    )
                    val call = apiService.getCustomerSerachDetails(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                            call: retrofit2.Call<String>, response:
                            Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {
                                    val jobjt = jObject.getJSONObject("CustomerSerachDetails")
                                    if (jobjt.getString("CustomerSerachDetailsList") != "null") {
                                        val jarray = jobjt.getJSONArray("CustomerSerachDetailsList")
                                        array_sortGroup = java.util.ArrayList<GroupModel>()
                                        searchGroupArrayList = ArrayList<GroupModel>()
                                        for (k in 0 until jarray.length()) {
                                            val jsonObject = jarray.getJSONObject(k)

                                            searchGroupArrayList.add(
                                                GroupModel(
                                                    jsonObject.getString("GroupName"),
                                                    jsonObject.getString("GroupId"),
                                                    jsonObject.getString("TLLock")
                                                )
                                            )

                                            array_sortGroup.add(
                                                GroupModel(
                                                    jsonObject.getString("GroupName"),
                                                    jsonObject.getString("GroupId"),
                                                    jsonObject.getString("TLLock")
                                                )
                                            )
                                        }

                                        sadapterGroup = GroupListAdapter(
                                            this@CustomerSearchActivity,
                                            array_sortGroup
                                        )
                                        list_view2!!.setAdapter(sadapterGroup)
                                        list_view2!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->

                                            input_grploan_remarks!!.setText("")
                                            dogroupSearchlisting((array_sortGroup[position].GroupId).toString(),(array_sortGroup[position].GroupName).toString())
                                            fk_acc_grp = (array_sortGroup[position].GroupId).toString()

                                            layoutdialog.dismiss()
                                        })
                                    }
                                }
                                else{
                                    val dialogBuilder = AlertDialog.Builder(
                                        this@CustomerSearchActivity,
                                        R.style.MyDialogTheme
                                    )
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
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                val mySnackbar = Snackbar.make(
                                    findViewById(R.id.rl_main),
                                    " Some technical issues.", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })

                } catch (e: Exception) {
                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(
                        findViewById(R.id.rl_main),
                        " Some technical issues.", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }
            }
            false -> {
                val mySnackbar = Snackbar.make(
                    findViewById(R.id.rl_main),
                    "No Internet Connection!!",
                    Snackbar.LENGTH_SHORT
                )
                mySnackbar.show()
            }
        }

    }

    private fun dogroupSearchlisting(custId:String,grpName:String) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
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


                        val AgentIdSP = applicationContext.getSharedPreferences(
                            BizcoreApplication.SHARED_PREF1,
                            0
                        )
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("LoginMode", BizcoreApplication.encryptMessage("3"))
                        requestObject1.put("CustomerId", BizcoreApplication.encryptMessage(custId))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(
                            R.string.BankKey
                        )))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(
                            R.string.BankHeader
                        )))
                        requestObject1.put(
                            "BankVerified",
                            "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg="
                        )
                        requestObject1.put(
                            "Module",
                            BizcoreApplication.encryptMessage(strSubModule)
                        )
                        requestObject1.put(
                            "Name",
                            BizcoreApplication.encryptMessage(edt_txt_name!!.text.toString())
                        )
                        requestObject1.put(
                            "MobileNumber", BizcoreApplication.encryptMessage(
                                edt_txt_mobile!!.text.toString()
                            )
                        )
                        requestObject1.put(
                            "AccountNumber", BizcoreApplication.encryptMessage(
                                edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString()
                            )
                        )


                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(
                            findViewById(R.id.rl_main),
                            " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }

                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        requestObject1.toString()
                    )
                    val call = apiService.getCustomerSerachDetails(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                            call: retrofit2.Call<String>, response:
                            Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {
                                    val jobjt = jObject.getJSONObject("CustomerSerachDetails")
                                    if (jobjt.getString("CustomerSerachDetailsList") == "null") {
                                    }
                                    else {
                                        val jarray = jobjt.getJSONArray("CustomerSerachDetailsList")
                                        dbHelper.deleteallCollection()
                                        //collection
                                        if (from!!.equals("Collection")) {
                                            for (i in 0..jarray.length()-1) {
                                                print(i)
                                                val jsonObject = jarray.getJSONObject(i)
                                                result = dbHelper.insertcollection(
                                                    CollectionModel(
                                                        Module = jsonObject.getString("Module"),
                                                        GroupName = jsonObject.getString("GroupName"),
                                                        GroupId = jsonObject.getString("GroupId"),
                                                        Name = jsonObject.getString("Name"),
                                                        MobileNumber = jsonObject.getString("MobileNumber"),
                                                        Address = jsonObject.getString("Address"),
                                                        CustomerId = jsonObject.getString("CustomerId"),
                                                        AccountNumber = jsonObject.getString("AccountNumber"),
                                                        FK_Account = jsonObject.getString("FK_Account"),
                                                        DueAmount = jsonObject.getString("DueAmount"),
                                                        TLLock = jsonObject.getString("TLLock"),
                                                        LastTransId = jsonObject.getString("LastTransactionId")
                                                    )
                                                )
                                            }
//                                                if(result==true){
//                                            Toast.makeText(applicationContext,"Success",Toast.LENGTH_LONG).show()
//                                        }else{
//                                            Toast.makeText(applicationContext,"Fail",Toast.LENGTH_LONG).show()}
//                                            }
                                            tv_send_group!!.visibility = View.VISIBLE
                                            tv_send_remark!!.visibility = View.VISIBLE

                                            cv_groupcollection!!.visibility=View.VISIBLE
                                            txt_grpname!!.text =grpName
                                            val grpobj_adapter = Grouplistadaptor(applicationContext!!, jarray)
                                            rv_grpcustlist!!.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                                            rv_grpcustlist!!.adapter = grpobj_adapter

                                        }
                                        else {
                                            val jarray = jobjt.getJSONArray("CustomerSerachDetailsList")
                                            array_sort1 = java.util.ArrayList<CustomerModel>()
                                            searchCustomerArrayList = ArrayList<CustomerModel>()
                                            for (k in 0 until jarray.length()) {
                                                val jsonObject = jarray.getJSONObject(k)

                                                searchCustomerArrayList.add(
                                                    CustomerModel(
                                                        jsonObject.getString("Module"),
                                                        jsonObject.getString("Name"),
                                                        jsonObject.getString("CustomerId"),
                                                        jsonObject.getString("FK_Account"),
                                                        jsonObject.getString("AccountNumber"),
                                                        jsonObject.getString("Address"),
                                                        jsonObject.getString("MobileNumber"),
                                                        jsonObject.getString("LastTransactionId"))

                                                )

                                                array_sort1.add(
                                                    CustomerModel(
                                                        jsonObject.getString("Module"),
                                                        jsonObject.getString("Name"),
                                                        jsonObject.getString("CustomerId"),
                                                        jsonObject.getString("FK_Account"),
                                                        jsonObject.getString("AccountNumber"),
                                                        jsonObject.getString("Address"),
                                                        jsonObject.getString("MobileNumber"),
                                                        jsonObject.getString("LastTransactionId"))

                                                )
                                            }

                                            sadapter1 = CustomerListAdapter(this@CustomerSearchActivity,array_sort1)

                                            val builder = AlertDialog.Builder(this@CustomerSearchActivity)
                                            val inflater1 = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                                            val layout = inflater1.inflate(R.layout.customerlist_popup, null)
                                            list_view1 = layout.findViewById(R.id.list_view1)
                                            etxtsearch1  = layout.findViewById(R.id.etsearch1)
                                            val tv_popuptitle1 = layout.findViewById(R.id.tv_popuptitle1) as TextView
                                            tv_popuptitle1.setText("Customer List")
                                            builder.setView(layout)
                                            val alertDialog = builder.create()
                                            alertDialog.show()
                                            list_view1!!.setAdapter(sadapter1)
                                            list_view1!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->

                                                fk_acc_ind = (array_sort1.get(position).FK_Account).toString()
                                                array_sort1.get(position).Name
                                                strfkaccount = array_sort1.get(position).FK_Account
                                                strCustName = array_sort1.get(position).Name + ", " + array_sort1.get(position).Address
                                                strCusName = array_sort1.get(position).Name


                                                if (from!!.equals("DemandList")){
                                                    LoadRetrofitDemandlist(strfkaccount.toString())
                                                }
                                                if (from!!.equals("CusStatement")) {

                                                    txt_min_name!!.text = array_sort1.get(position).Name
                                                    txt_min_mob!!.text = array_sort1.get(position).MobileNumber
                                                    txt_min_acno!!.text = array_sort1.get(position).AccountNumber
                                                    Loadministatementlisting(array_sort1.get(position).AccountNumber.toString())
                                                }
                                                if (from!!.equals("BalanceEnq")) {

                                                    txt_blnc_name!!.text = array_sort1.get(position).Name
                                                    txt_blnc_mob!!.text = array_sort1.get(position).MobileNumber
                                                    txt_blnc_acno!!.text = array_sort1.get(position).AccountNumber
                                                    getAccountDetails(array_sort1.get(position).AccountNumber.toString())
                                                }
//                                                if (from!!.equals("CusStatement")) {
//                                                    Loadministatementlisting(array_sort1.get(position).AccountNumber.toString())
//                                                }
//                                                if (from!!.equals("BalanceEnq")) {
//                                                    getAccountDetails(array_sort1.get(position).AccountNumber.toString())
//                                                }
                                                alertDialog.dismiss()

                                            })

                                        }
                                    }
                                }
                                else{
                                    val dialogBuilder = AlertDialog.Builder(
                                        this@CustomerSearchActivity,
                                        R.style.MyDialogTheme
                                    )
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
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                val mySnackbar = Snackbar.make(
                                    findViewById(R.id.rl_main),
                                    " Some technical issues.", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })

                } catch (e: Exception) {
                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(
                        findViewById(R.id.rl_main),
                        " Some technical issues.", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }
            }
            false -> {
                val mySnackbar = Snackbar.make(
                    findViewById(R.id.rl_main),
                    "No Internet Connection!!",
                    Snackbar.LENGTH_SHORT
                )
                mySnackbar.show()
            }
        }

    }






    private fun numClick(){
        edt_acc_second!!.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val msg: String = edt_acc_first!!.text.toString()
                        if (msg.length == 1) {
                            edt_acc_first!!.setText("00" + edt_acc_first!!.text.toString())
                        } else if (msg.length == 2) {
                            edt_acc_first!!.setText("0" + edt_acc_first!!.text.toString())
                        }
                        if (msg.equals("") || msg.equals("0") || msg.equals("00") || msg.equals("000"))    //size as per your requirement
                        {
                            edt_acc_first!!.setText("000")
                        }
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })
        edt_acc_third!!.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val msg: String = edt_acc_second!!.text.toString()
                        val msg0: String = edt_acc_first!!.text.toString()
                        if (msg0.length == 0) {
                            edt_acc_first!!.setText("000")
                        }
                        if (msg0.length == 1) {
                            edt_acc_first!!.setText("00" + edt_acc_first!!.text.toString())
                        }
                        if (msg0.length == 2) {
                            edt_acc_first!!.setText("0" + edt_acc_first!!.text.toString())
                        }
                        if (msg.length == 1) {
                            edt_acc_second!!.setText("00" + edt_acc_second!!.text.toString())
                        }
                        if (msg.length == 2) {
                            edt_acc_second!!.setText("0" + edt_acc_second!!.text.toString())
                        }
                        if (msg.equals("") || msg.equals("0") || msg.equals("00") || msg.equals("000")) {
                            edt_acc_second!!.setText("000" + edt_acc_second!!.text.toString())
                        }
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })
        edt_acc_first?.setOnKeyListener { v, keyCode, event ->
            if((event.action == KeyEvent.ACTION_DOWN)
                && (event.keyCode == KeyEvent.KEYCODE_ENTER)){
                val msg: String = edt_acc_first!!.text.toString()
                if(msg.length==1)
                {
                    edt_acc_first!!.setText("00" + edt_acc_first!!.text.toString())
                    edt_acc_second!!.requestFocus()
                }
                else if(msg.length==2)
                {
                    edt_acc_first!!.setText("0" + edt_acc_first!!.text.toString())
                    edt_acc_second!!.requestFocus()
                }
                if(msg.equals("")||msg.equals("0")||msg.equals("00")||msg.equals("000"))    //size as per your requirement
                {
                    edt_acc_first!!.setText("000")
                    edt_acc_second!!.requestFocus()
                }
                else if(msg.length==3)
                {
                    edt_acc_second!!.requestFocus()
                }
                return@setOnKeyListener true
            }
            false
        }
        edt_acc_second?.setOnKeyListener { v, keyCode, event ->
            if((event.action == KeyEvent.ACTION_DOWN)
                && (event.keyCode == KeyEvent.KEYCODE_ENTER)){
                val msg: String = edt_acc_second!!.text.toString()
                if(msg.length==1)
                {
                    edt_acc_second!!.setText("00" + edt_acc_second!!.text.toString())
                    edt_acc_third!!.requestFocus()
                }
                else if(msg.length==2)
                {
                    edt_acc_second!!.setText("0" + edt_acc_second!!.text.toString())
                    edt_acc_third!!.requestFocus()
                }
                else if(msg.equals("")||msg.equals("0")||msg.equals("00")||msg.equals("000"))
                {
                    edt_acc_second!!.setText("000" + edt_acc_second!!.text.toString())
                    edt_acc_third!!.requestFocus()
                }
                else if(msg.length==3)
                {
                    edt_acc_third!!.requestFocus()
                }
                return@setOnKeyListener true
            }
            false
        }



        edt_acc_third?.setOnKeyListener { v, keyCode, event ->
            if((event.action == KeyEvent.ACTION_DOWN)
                && (event.keyCode == KeyEvent.KEYCODE_ENTER)){
                //Do something, such as loadJob()
                val msg: String = edt_acc_third!!.text.toString()
                if(msg.length==4){
                    edt_acc_third!!.setText("00" + msg)
                    edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)

                    //    val intent = Intent(this, BalanceEnq1Activity::class.java)
                    //   startActivity(intent)

                }


                return@setOnKeyListener true
            }

            false
        }
    }

    private fun proceedForAccountDetails() {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
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
                        val AgentIdSP = applicationContext.getSharedPreferences(
                            BizcoreApplication.SHARED_PREF1,
                            0
                        )
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss.SSS",
                            Locale.ENGLISH
                        )
                        val dateTime = simpleDateFormat.format(calendar.time)
                        val mAccountNo =
                            edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString()
                        val tokenSP = applicationContext.getSharedPreferences(
                            BizcoreApplication.SHARED_PREF4,
                            0
                        )
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
                        requestObject1.put(
                            "Processing_Code",
                            BizcoreApplication.encryptMessage("090000")
                        )
                        requestObject1.put(
                            "Customer_Number",
                            BizcoreApplication.encryptMessage("000000000000")
                        )
                        requestObject1.put(
                            "Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage(
                                "0000000000000000"
                            )
                        )
                        requestObject1.put(
                            "From_Module", BizcoreApplication.encryptMessage(
                                strSubModule
                            )
                        )
                        requestObject1.put(
                            "AccountIdentification2", BizcoreApplication.encryptMessage(
                                mAccountNo
                            )
                        )
                        requestObject1.put(
                            "RequestMessage",
                            BizcoreApplication.encryptMessage("temporary")
                        )
                        requestObject1.put(
                            "SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(
                                randomNumber
                            )
                        )
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject1.put("CardLess", BizcoreApplication.encryptMessage("1"))
                        requestObject1.put(
                            "CurrentDate",
                            BizcoreApplication.encryptMessage(dateTime)
                        )
                        requestObject1.put(
                            "Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(
                                Imei
                            )
                        )
                        requestObject1.put(
                            "BankKey", BizcoreApplication.encryptMessage(
                                getResources().getString(
                                    R.string.BankKey
                                )
                            )
                        )
                        requestObject1.put(
                            "BankHeader", BizcoreApplication.encryptMessage(
                                getResources().getString(
                                    R.string.BankHeader
                                )
                            )
                        )
                        requestObject1.put(
                            "BankVerified",
                            "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg="
                        )//encrypted value for zero
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(
                            findViewById(R.id.rl_main),
                            " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }
                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        requestObject1.toString()
                    )
                    val call = apiService.getVerificationCall(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                            call: retrofit2.Call<String>, response:
                            Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                val jobjt = jObject.getJSONObject("verInfo")
                                if (jObject.getString("StatusCode") == "0") {


                                    amountValidation(jobjt.getString("CusName"))


                                } else {
                                    val dialogBuilder = AlertDialog.Builder(
                                        this@CustomerSearchActivity,
                                        R.style.MyDialogTheme
                                    )
                                    dialogBuilder.setMessage(jobjt.getString("ResponseMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "OK",
                                            DialogInterface.OnClickListener { dialog, id ->
                                                dialog.dismiss()
                                                //doLogout()
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

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })
                } catch (e: Exception) {
                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(
                        findViewById(R.id.rl_main),
                        " Some technical issues.", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }
            }
            false -> {
                val mySnackbar = Snackbar.make(
                    findViewById(R.id.rl_main),
                    "No Internet Connection!!",
                    Snackbar.LENGTH_SHORT
                )
                mySnackbar.show()
            }
        }
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

    private fun amountValidation(cName: String?) {

            strAmount= input_amount!!.text?.toString()
            strAmount = strAmount!!.replace(",","")
            strMsg= input_grploan_remarks!!.text?.toString()
            submitgroupCollection(strAmount,strMsg,cName)
    }

    private fun amountValidation1(cName: String?,transid:String?) {
        if (input_amount!!.text.toString() == null || input_amount!!.text.toString().isEmpty()) {
            input_amount!!.setError("Please enter collction amount")
        }
        else{
            strAmount= input_amount!!.text?.toString()
            strAmount = strAmount!!.replace(",","")
            strMsg= input_msg!!.text?.toString()

            submitDeposit(strAmount,strMsg,cName,transid)
        }
    }



    private fun submitDeposit(strAmount: String?, strMsg: String?, strCustname: String?, transid: String?) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
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
                        hashList.add( edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString())//a/c no
                        hashList.add(strAmount!!)//amount
                        hashString = CryptoGraphy.getInstance().hashing( hashList )
                        hashString = "76"+hashString+token

                        val processingCode      = "760"+strSubModule+"11"
                        Log.e(TAG,"LATITUDE    "+Latitude)


                        requestObject1.put("Processing_Code", BizcoreApplication.encryptMessage(processingCode))
                        requestObject1.put("Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage("0000000000000000"))
                        requestObject1.put("Customer_Number", BizcoreApplication.encryptMessage("000000000000"))
                        requestObject1.put("AccountIdentification1", BizcoreApplication.encryptMessage( edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString()))
                        requestObject1.put("From_Module", BizcoreApplication.encryptMessage(strSubModule))
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
                      /*  requestObject1.put("Latitude", BizcoreApplication.encryptMessage("11.87654"))
                        requestObject1.put("Longitude", BizcoreApplication.encryptMessage("76.87654"))
                        requestObject1.put("AddressLine", BizcoreApplication.encryptMessage("Ambadi"))
                        requestObject1.put("Locality", BizcoreApplication.encryptMessage("nadakvu"))
                        requestObject1.put("Area", BizcoreApplication.encryptMessage("kerala"))
                        requestObject1.put("Country", BizcoreApplication.encryptMessage("india"))
                        requestObject1.put("PostalCode", BizcoreApplication.encryptMessage("673011"))*/
                        requestObject1.put("LastTransactionId",BizcoreApplication.encryptMessage(transid))
                        requestObject1.put("Latitude", BizcoreApplication.encryptMessage(Latitude))
                        requestObject1.put("Longitude", BizcoreApplication.encryptMessage(Longitude))
                        requestObject1.put("AddressLine", BizcoreApplication.encryptMessage(addresLine))
                        requestObject1.put("Locality", BizcoreApplication.encryptMessage(Locality))
                        requestObject1.put("Area", BizcoreApplication.encryptMessage(Area))
                        requestObject1.put("Country", BizcoreApplication.encryptMessage(Country))
                        requestObject1.put("PostalCode", BizcoreApplication.encryptMessage(PostalCode))

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
                                if (jObject.getString("StatusCode") == "0") {
                                    val jobjt = jObject.getJSONObject("TransInfo")
                                    showSuccessDialog(": "+jObject.getString("EXMessage"),
                                        ": "+strCustname,
                                        ": "+ edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString()+"("+strSubModule+")",
                                        ": ₹ "+jobjt.getString("NetAmount")+" /-",
                                        ": "+jobjt.getString("ReferanceNumber"),
                                        jobjt.getString("BalanceAmount"),
                                        jobjt.getString("NetAmount"))

                                } else {
                                    val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity, R.style.MyDialogTheme)
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

    private fun submitgroupCollection(strAmount: String?, strMsg: String?, strCustname: String?) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
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


                        val jsonArray = JSONArray()
                        val db = DBHandler(this)
                        var fAmount: Float =0.0F
                        val cursor = db.select("collectiontable")
                        var i = 0
                        if(cursor!=null){
                            if (cursor.moveToFirst()){
                                do {
                                    fAmount = fAmount+cursor.getString(cursor.getColumnIndex("collect_amnt")).toFloat()
                                    val jsonObject1 = JSONObject()
                                    jsonObject1.put(
                                        "GroupId",BizcoreApplication.encryptMessage(cursor.getString(cursor.getColumnIndex("GroupId")))
                                    )
                                    jsonObject1.put(
                                        "CustomerId",BizcoreApplication.encryptMessage(cursor.getString(cursor.getColumnIndex("CustomerId")))
                                    )
                                    jsonObject1.put(
                                        "FK_Account",BizcoreApplication.encryptMessage(cursor.getString(cursor.getColumnIndex("FK_Account")))
                                    )
                                    jsonObject1.put(
                                        "AccountNumber",BizcoreApplication.encryptMessage(cursor.getString(cursor.getColumnIndex("AccountNumber")))
                                    )
                                    jsonObject1.put(
                                        "Amount",BizcoreApplication.encryptMessage(cursor.getString(cursor.getColumnIndex("collect_amnt")))
                                    )
                                    jsonObject1.put(
                                        "LastTransactionId",BizcoreApplication.encryptMessage(cursor.getString(cursor.getColumnIndex("Last_Trans_ID")))
                                    )


                                    try {
                                        jsonArray.put(i, jsonObject1)
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }

                                    i++

                                }while (cursor.moveToNext())
                            }
                        }

                        strAmnt = fAmount.toString()

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
                        Log.e(TAG,"strMsg  2960   "+strMsg);

                        val hashList = ArrayList<String>()
                        hashList.add( Imei )
                        hashList.add( dateTime )
                        hashList.add( randomNumber )
                        hashList.add( agentId!! )
                        hashList.add( "0000000000000000" )//card no
                        hashList.add( "000000000000" )//cus no
                       // hashList.add( )//a/c no
                        hashList.add(strAmnt!!)//amount
                        hashString = CryptoGraphy.getInstance().hashing( hashList )
                        hashString = "76"+hashString+token

                        val processingCode      = "760"+strSubModule+"11"

                        requestObject1.put("Processing_Code", BizcoreApplication.encryptMessage(processingCode))
                        requestObject1.put("Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage("0000000000000000"))
                        requestObject1.put("Customer_Number", BizcoreApplication.encryptMessage("000000000000"))
                        requestObject1.put("AccountIdentification1", BizcoreApplication.encryptMessage(""))
                        requestObject1.put("From_Module", BizcoreApplication.encryptMessage(strSubModule))
                        requestObject1.put("RequestMessage", BizcoreApplication.encryptMessage(strMsg))
                        requestObject1.put("Narration", BizcoreApplication.encryptMessage(strMsg))
                        requestObject1.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("TransDate", BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject1.put("CardLess", BizcoreApplication.encryptMessage(1.toString()))
                        requestObject1.put("TransType", BizcoreApplication.encryptMessage("RECEIPT"))
                        requestObject1.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode",BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero

                        requestObject1.put("Latitude", BizcoreApplication.encryptMessage(Latitude))
                        requestObject1.put("Longitude", BizcoreApplication.encryptMessage(Longitude))
                        requestObject1.put("AddressLine", BizcoreApplication.encryptMessage(addresLine))
                        requestObject1.put("Locality", BizcoreApplication.encryptMessage(Locality))
                        requestObject1.put("Area", BizcoreApplication.encryptMessage(Area))
                        requestObject1.put("Country", BizcoreApplication.encryptMessage(Country))
                        requestObject1.put("PostalCode", BizcoreApplication.encryptMessage(PostalCode))
                        requestObject1.put("Amount", BizcoreApplication.encryptMessage(strAmnt))


                        cursor.close()
                        requestObject1.put("GPData", jsonArray)

                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                    }
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.doGroupLoanCollection(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                if (jObject.getInt("StatusCode")==0) {
                                    val jobjt = jObject.getJSONObject("GroupLoanCollection")

                                    showGroupSuccessDialog(": "+jObject.getString("EXMessage"),
                                        ": "+txt_grpname!!.text,
                                        ": "+ edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString()+"("+strSubModule+")",
                                        ": ₹ "+strAmnt+" /-",
                                        ": "+jobjt.getString("ReferanceNumber"),
                                        jobjt.getString("BalanceAmount"),
                                        jobjt.getString("NetAmount"))

                                }
                                else {
                                    val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity, R.style.MyDialogTheme)
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
    private fun showGroupSuccessDialog(deposit: String, recName: String, recAc: String, amount: String, reffNo: String, BalAmount: String, netAmount: String) {
        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.success_deposit_layout)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val ll_avail_balance = dialog.findViewById(R.id.ll_avail_balance)as LinearLayout
        ll_avail_balance.visibility = View.GONE
        val ll_opening_blnc = dialog.findViewById(R.id.ll_opening_blnc)as LinearLayout
        ll_opening_blnc.visibility = View.GONE

        val txtDeposit = dialog.findViewById(R.id.txtDeposit)as TextView
        txtDeposit.text = deposit

        val txtReceiverName = dialog.findViewById(R.id.txtReceiverName)as TextView
        txtReceiverName.text = recName

        val txtReceiverAC = dialog.findViewById(R.id.txtReceiverAC)as TextView
        txtReceiverAC.text = recAc

            avlBal=(BalAmount.replace("C","")).toDouble()
            netAmt=(netAmount).toDouble()
            opBal= avlBal!! + netAmt!!

        val txtAOpeningBal = dialog.findViewById(R.id.txtAOpeningBal)as TextView
        txtAOpeningBal.text = ": ₹ "+opBal.toString()+" Dr"

        val txtAvbal = dialog.findViewById(R.id.txtAvbal)as TextView
        txtAvbal.text = ": ₹ "+BalAmount+"r"


        val txtAmount = dialog.findViewById(R.id.txtAmount)as TextView
        if(strSubModule!!.equals("TLGP")){
            txtAmount.text = amount
        }
        else{
            txtAmount.text = amount+"\n[ "+tv_rupees!!.text.toString()+" ]"
        }

        val txtRefferenceNo = dialog.findViewById(R.id.txtRefferenceNo)as TextView
        txtRefferenceNo.text = reffNo

        val okBtn = dialog .findViewById(R.id.btnOK) as Button
        okBtn.setOnClickListener { dialog .dismiss()
            doReset()
        }
        val printBtn = dialog.findViewById(R.id.btnprint) as Button
        printBtn.setOnClickListener{
            showPrintDialog()
        }
        dialog .show()
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

        avlBal=(BalAmount.replace(" D","")).toDouble()
        netAmt=(netAmount).toDouble()
        opBal= avlBal!! + netAmt!!

        val txtAOpeningBal = dialog.findViewById(R.id.txtAOpeningBal)as TextView
        txtAOpeningBal.text = ": ₹ "+opBal.toString()+"0 Dr"

        val txtAvbal = dialog.findViewById(R.id.txtAvbal)as TextView
            txtAvbal.text = ": ₹ "+BalAmount+"r"



        if (avlBal == 0.0) {

            val txtAmount = dialog.findViewById(R.id.txtAmount)as TextView
            txtAmount.text = ": ₹ "+strAmount+ " /-\n[ "+tv_rupees!!.text.toString()+" ]"
            val ll_balance = dialog.findViewById(R.id.ll_balance)as LinearLayout
                ll_balance.visibility = View.VISIBLE
            var ttlAmount = (strAmount)?.toDouble()
            var balance = ttlAmount!! - netAmt!!
            val txtBalance = dialog.findViewById(R.id.txtBalance)as TextView
                txtBalance.text = ": ₹ "+balance+"0 /-"
        }
        else{
            val txtAmount = dialog.findViewById(R.id.txtAmount)as TextView
            txtAmount.text = amount+"\n[ "+tv_rupees!!.text.toString()+" ]"
        }

        val txtRefferenceNo = dialog.findViewById(R.id.txtRefferenceNo)as TextView
        txtRefferenceNo.text = reffNo

        val okBtn = dialog .findViewById(R.id.btnOK) as Button
        okBtn.setOnClickListener {
            dialog .dismiss()
            doReset()

        }
        val printBtn = dialog.findViewById(R.id.btnprint) as Button
        printBtn.setOnClickListener{
            showPrintDialog()
        }
        dialog .show()
    }

    /*Place comma seperator on edit text and display amount in words on a text view*/
    fun setEdtTxtAmountCommaSeperator(editText: EditText, txtAmt: TextView?, isDecimalAllowed: Boolean) {

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

    fun LoadRetrofitDemandlist(value:String){
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
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

                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                    }
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getDemandlist(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {
                                    cv_demandlist!!.visibility  = View.VISIBLE

                                    if(strSubModule!!.equals("TLGP")) {
                                        ll_grp_name!!.visibility  = View.VISIBLE
                                    }
                                    else{
                                        ll_grp_name!!.visibility  = View.GONE
                                    }

                                    val jobjt   = jObject.getJSONObject("DemandList")
                                    val jarray  = jobjt.getJSONArray("DemandListDetails")


                                    var jsonObject: JSONObject? = null
                                    for (i in 0 until jarray.length()) {
                                        jsonObject = jarray.getJSONObject(0)

                                        val GroupName :String = jsonObject.getString("GroupName")
                                        val CustomerName :String = jsonObject.getString("CustomerName")
                                        val CustomerNumber :String = jsonObject.getString("CustomerNumber")
                                        val MObileNo :String = jsonObject.getString("MObileNo")
                                        val SchemeName :String = jsonObject.getString("SchemeName")
                                        val ShortName :String = jsonObject.getString("ShortName")
                                        val LoanNumber :String = jsonObject.getString("LoanNumber")
                                        val SanctionDate :String = jsonObject.getString("SanctionDate")
                                        val DueDate :String = jsonObject.getString("DueDate")
                                        val LoanAmount :String = jsonObject.getString("LoanAmount")
                                        val PaidAmt :String = jsonObject.getString("PaidAmt")
                                        val Outstand :String = jsonObject.getString("Outstand")
                                        val Transdate :String = jsonObject.getString("Transdate")
                                        val ArrearAmt :String = jsonObject.getString("ArrearAmt")
                                        val DemandAmt :String = jsonObject.getString("DemandAmt")
                                        val Advance :String = jsonObject.getString("Advance")
                                        val others :String = jsonObject.getString("Others")
                                        val TotalDue :String = jsonObject.getString("TotalDue")
                                        totel_due.setText("₹ "+TotalDue)
                                        other_charges.setText("₹ "+others)
                                        lone_number.setText(LoanNumber)
                                        name.setText(CustomerName)
                                        short_name.setText(ShortName)
                                        grouup_name.setText(GroupName)
                                        mobile.setText(MObileNo)
                                        cust_no.setText(CustomerNumber)
                                        scheme_name.setText(SchemeName)
                                        sanction_date.setText(SanctionDate)
                                        due_date.setText(DueDate)
                                        transaction_date.setText(Transdate)
                                        outstand_amount.setText("₹ "+Outstand)
                                        loan_amount.setText("₹ "+LoanAmount)
                                        paid_amount.setText("₹ "+PaidAmt)
                                        arrier_amount.setText("₹ "+ArrearAmt)
                                        demand_amount.setText("₹ "+DemandAmt)
                                        advance.setText("₹ "+Advance)


                                    }





                                } else {
                                    cv_demandlist!!.visibility  = View.GONE

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

    fun Loadministatementlisting(accountno: String){
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
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

                        val cardNo: String
                        val agentId: String
                        val hashString: String
                        val token: String
                        val randomNumber: String

                        val hashList: MutableList<String>

                        val calendar :Calendar = Calendar.getInstance()
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
                            agentId = AgentIdSP.getString("Agent_ID", null)!!
                        val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
                            token = tokenSP.getString("token", null)!!
                            randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                            cardNo = BizcoreApplication.TEMP_CARD_NO
                        val customNo = BizcoreApplication.TEMP_CUST_NO



                        hashList = ArrayList()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(agentId)
                        hashList.add(cardNo)
                        hashList.add(customNo)
                        hashList.add(accountno)
                        hashString = CryptoGraphy.getInstance().hashing(hashList)

                        ///////////////////////live
                        requestObject1.put("Processing_Code", BizcoreApplication.encryptMessage("381000"))
                        requestObject1.put("Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage(cardNo)) //card no
                        requestObject1.put("Customer_Number", BizcoreApplication.encryptMessage(customNo))
                        requestObject1.put("AccountIdentification1", BizcoreApplication.encryptMessage(accountno))
                        requestObject1.put("From_Module", BizcoreApplication.encryptMessage(strSubModule))
                        requestObject1.put("RequestMessage", BizcoreApplication.encryptMessage("hloooo"))
                        requestObject1.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(BizcoreApplication.REQUEST_MINISTATEMENT.toString() + hashString + token))
                        requestObject1.put("CardLess", BizcoreApplication.encryptMessage("1"))
                        requestObject1.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey",BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                        requestObject1.put("BankHeader",BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))


                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                    }

                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getMiniStatement(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {
                                    cv_ministatement!!.visibility = View.VISIBLE
                                    ll_ministatement!!.visibility = View.VISIBLE

                                    val jobjt = jObject.getJSONObject("MiniInfo")
                                    val jarray = jobjt.getJSONArray("MiniList")
                                    val balance_amount =   jobjt.getString("BalanceAmount")
                                    available_balance!!.setText("₹ "+balance_amount)
                                    val due_amount =   jobjt.getString("DueAmount")
                                    Due_balance!!.setText("₹ "+due_amount)
                                    if(jobjt!!.getString("DueMode").equals("CR")){
                                        tv_due_mode!!.setTextColor(Color.parseColor("#4CAF50"))
                                        tv_due_mode!!.setText(" Cr")
                                    }else if (jobjt!!.getString("DueMode").equals("DR")){
                                        tv_due_mode!!.setTextColor(Color.parseColor("#FC0303"))
                                        tv_due_mode!!.setText(" Dr")
                                    }
                                    if(jobjt!!.getString("BalanceMode").equals("CR")){
                                        tv_blnc_mode!!.setTextColor(Color.parseColor("#4CAF50"))
                                        tv_blnc_mode!!.setText(" Cr")
                                    }else if (jobjt!!.getString("BalanceMode").equals("DR")){
                                        tv_blnc_mode!!.setTextColor(Color.parseColor("#FC0303"))
                                        tv_blnc_mode!!.setText(" Dr")
                                    }

                                    val Principal_amount =   jobjt.getString("Principal")
                                    Principal_balance !!.setText("₹ "+Principal_amount)
                                    val interest_amount =   jobjt.getString("InterestAmount")
                                    Interest_Amount !!.setText("₹ "+interest_amount)
                                    val obj_adapter = Ministatementadaptor(applicationContext!!, jarray)
                                    recyclerview_ministatement!!.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                                    recyclerview_ministatement!!.adapter = obj_adapter
                                }
                                else {
                                    ll_ministatement!!.visibility = View.GONE
                                    cv_ministatement!!.visibility = View.GONE

                                    val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity , R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jObject.getString("EXMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton("CANCEL", DialogInterface.OnClickListener {
                                                dialog, id -> dialog.dismiss()
                                        })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                                    nbutton.setTextColor(Color.MAGENTA)
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                ll_ministatement!!.visibility = View.GONE

                                e.printStackTrace()
                            }
                        }
                        override fun onFailure(call: retrofit2.Call<String>, t:Throwable) {
                            progressDialog!!.dismiss()
                            ll_ministatement!!.visibility = View.GONE

                            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                " Some technical issues.1", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })
                } catch (e: Exception) {
                    progressDialog!!.dismiss()
                    ll_ministatement!!.visibility = View.GONE

                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                        " Some technical issues.2", Snackbar.LENGTH_SHORT
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

    private fun getAccountDetails(accvalue: String) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
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
                        var acc = accvalue
                        if (acc.equals(""))
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
                        requestObject2.put("From_Module", BizcoreApplication.encryptMessage(strSubModule))
                        requestObject2.put("RequestMessage", BizcoreApplication.encryptMessage("hloooo"))
                        requestObject2.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject2.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject2.put("ResponseType", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject2.put("CardLess", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
//                        requestObject2.put("Auth_ID", BizcoreApplication.encryptMessage(authid))
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
                        Response<String>) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject1 = JSONObject(response1.body())
                                if (jObject1.getString("StatusCode") == "0") {
                                    val jobjt = jObject1.getJSONObject("BalInfo")
                                    cv_balanceenq!!.visibility = View.VISIBLE
                                    val amount =jobjt.getString("BalanceAmount")
                                    val due =jobjt.getString("DueAmount")
                                    if("C" in amount ){
                                        txtv_viewbal.setText("₹ "+amount+" (Cr)")
                                    }else if(amount.contains("null")) {

                                    }else {
                                        txtv_viewbal.setText("₹ "+amount+"r")
                                    }
                                    if(due.contains("null"))
                                    {
                                        ll_dueamnt?.visibility  = View.INVISIBLE
                                    }
                                    else if ("C" in due && !due.startsWith("0") )
                                    {
                                        ll_dueamnt?.visibility  = View.VISIBLE
                                        txtv_dueam1.setText(" ₹ "+due+" (Cr)")
                                    }
                                    else if (!due.startsWith("0")&& due!=null)
                                    {
                                        ll_dueamnt?.visibility  = View.VISIBLE
                                        txtv_dueam1.setText(" ₹ "+due+"r")
                                    }
                                }
                                else if (jObject1.getString("StatusCode") == "-1") {
                                    cv_balanceenq!!.visibility = View.GONE

                                    val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jObject1.getString("EXMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", DialogInterface.OnClickListener {
                                                dialog, id -> dialog.dismiss()
//                                            doLogout()
                                        })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }
                                else  {
                                    val jobjt = jObject1.getJSONObject("BalInfo")
                                    cv_balanceenq!!.visibility = View.GONE
                                    val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jobjt.getString("ResponseMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", DialogInterface.OnClickListener {
                                                dialog, id -> dialog.dismiss()
//                                            doLogout()
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
                            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main), " Some technical issues.", Snackbar.LENGTH_SHORT)
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


    private fun edtFocus() {
        edt_acc_first!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val msg: String = edt_acc_first!!.text.toString()
            }
        })
        edt_acc_second!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val msg: String = edt_acc_second!!.text.toString()
            }
        })
        edt_acc_third!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
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

    @Throws(CertificateException::class, KeyStoreException::class, IOException::class, NoSuchAlgorithmException::class, KeyManagementException::class)
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


    private fun locationDetails(mode: String) {
        Latitude = sharedpreferences!!.getString("Latitude", "").toString()
        Longitude = sharedpreferences!!.getString("Longitude", "").toString()
        addresLine = sharedpreferences!!.getString("AddressLine", "").toString()
        Locality = sharedpreferences!!.getString("Locality", "").toString()
        Area = sharedpreferences!!.getString("Area", "").toString()
        Country = sharedpreferences!!.getString("Country", "").toString()
        PostalCode = sharedpreferences!!.getString("PostalCode", "").toString()


        if (!addresLine.equals("")){
            Log.e(TAG,"AddressLine 4053  "+addresLine)
            Log.e(TAG,"Longitude 4054  "+Longitude)


            if (mode.equals("1")){
                if(remark == 2){
                    sendGroups()
                }
                else if (remark == 3){
                    submitRemark("0",input_grploan_remarks!!.text.toString(),"0",fk_acc_grp)
                }
            }
            if (mode.equals("2")){
                if(remark == 0){
                    SendInd()
                }
                else if (remark == 1){
                    submitRemark("0",input_msg!!.text.toString(),lasttransactionid,fk_acc_ind)
                }
            }

        }else{
            checkGpsOn();
            Log.e(TAG,"AddressLine   null   "+addresLine)
            //Toast.makeText(applicationContext,"Try Again",Toast.LENGTH_SHORT).show()
        }

    }

    private fun sendGroups() {

        dbHelper.checkValid()
        val result: String = dbHelper.CheckLockValidation1()
        Log.e(TAG,"CheckLockValidation1   "+result)

//                var result1 = dbHelper.CheckLockValidation()
//                Log.e(TAG,"CheckLockValidation1   "+result1)
        if(result.equals("0")){
            Toast.makeText(applicationContext,"Please Enter Valid collection amount for all required fields ",Toast.LENGTH_LONG).show()
        }else{
            if(edt_acc_first!!.text.toString() == null||edt_acc_second!!.text.toString() == null||edt_acc_third!!.text.toString() == null){

                val dialogBuilder = AlertDialog.Builder(
                    this@CustomerSearchActivity,
                    R.style.MyDialogTheme
                )
                dialogBuilder.setMessage("Please verify & select an account.")
                    .setCancelable(false)
                    .setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.dismiss()
                            //doLogout()
                        })
                val alert = dialogBuilder.create()
                alert.show()
                val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                pbutton.setTextColor(Color.MAGENTA)
            }
            else {
                amountValidation(strCusName)

            }
        }
    }

    private fun SendInd() {
        if(edt_acc_first!!.text.toString() == null||edt_acc_second!!.text.toString() == null||edt_acc_third!!.text.toString() == null){

            val dialogBuilder = AlertDialog.Builder(
                this@CustomerSearchActivity,
                R.style.MyDialogTheme
            )
            dialogBuilder.setMessage("Please verify & select an account.")
                .setCancelable(false)
                .setPositiveButton(
                    "OK",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                        //doLogout()
                    })
            val alert = dialogBuilder.create()
            alert.show()
            val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
            pbutton.setTextColor(Color.MAGENTA)
        }
        else {
            amountValidation1(strCusName,lasttransactionid)

        }
    }

    private fun checkGpsOn() {
        if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }else{
            showGPSDisabledAlertToUser();
        }
    }

    private fun showGPSDisabledAlertToUser() {
        val alertDialogBuilder =
            AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Goto Settings Page To Enable GPS"
            ) { dialog, id ->
                val callGPSSettingIntent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                startActivity(callGPSSettingIntent)
            }
        alertDialogBuilder.setNegativeButton(
            "Cancel"
        ) { dialog, id -> dialog.cancel() }
        val alert = alertDialogBuilder.create()
        alert.show()
    }

    fun hideKeyboard(v:View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun submitRemark(strAmount: String?, strMsg: String?, transid: String?,FK_Account:String) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
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
                        hashList.add( edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString())//a/c no
                        hashList.add(strAmount!!)//amount
                        hashString = CryptoGraphy.getInstance().hashing( hashList )
                        hashString = "76"+hashString+token

                        val processingCode      = "760"+strSubModule+"11"
                        Log.e(TAG,"LATITUDE    "+Latitude)


                        requestObject1.put("Processing_Code", BizcoreApplication.encryptMessage(processingCode))
                        requestObject1.put("Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage("0000000000000000"))
                        requestObject1.put("Customer_Number", BizcoreApplication.encryptMessage("000000000000"))
                        requestObject1.put("AccountIdentification1", BizcoreApplication.encryptMessage( edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString()))
                        requestObject1.put("From_Module", BizcoreApplication.encryptMessage(strSubModule))
                        requestObject1.put("RequestMessage", BizcoreApplication.encryptMessage(strMsg))
                        requestObject1.put("Narration", BizcoreApplication.encryptMessage(strMsg))
                        requestObject1.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("FK_Account", BizcoreApplication.encryptMessage(FK_Account))
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
                        requestObject1.put("LastTransactionId",BizcoreApplication.encryptMessage(transid))
                        requestObject1.put("Latitude", BizcoreApplication.encryptMessage(Latitude))
                        requestObject1.put("Longitude", BizcoreApplication.encryptMessage(Longitude))
                        requestObject1.put("AddressLine", BizcoreApplication.encryptMessage(addresLine))
                        requestObject1.put("Locality", BizcoreApplication.encryptMessage(Locality))
                        requestObject1.put("Area", BizcoreApplication.encryptMessage(Area))
                        requestObject1.put("Country", BizcoreApplication.encryptMessage(Country))
                        requestObject1.put("PostalCode", BizcoreApplication.encryptMessage(PostalCode))

                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                    }
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getCollectionRemark(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {
                                    val jobjt = jObject.getJSONObject("TransInfo")
                                    val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jobjt.getString("ResponseMessage"))
                                            .setCancelable(false)
                                            .setPositiveButton("OK", DialogInterface.OnClickListener {
                                                dialog, id -> dialog.dismiss()
                                                doReset()
                                            })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)


                                } else {

                                    val mySnackbar = Snackbar.make(
                                            findViewById(R.id.rl_main),
                                            jObject.getString("EXMessage"), Snackbar.LENGTH_SHORT

                                    )
                                    mySnackbar.show()
                                }
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                e.printStackTrace()
                                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                    " Some technical issues.", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
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


}