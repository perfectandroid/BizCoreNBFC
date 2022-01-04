package com.perfect.nbfc.Offline.Activity

import android.app.AlertDialog
import android.app.Dialog
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
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.perfect.nbfc.Api.ApiInterface
import com.perfect.nbfc.Api.ApiService
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Deposit.DepositActivity
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.Helper.ConnectivityUtils
import com.perfect.nbfc.Helper.CryptoGraphy
import com.perfect.nbfc.Helper.NumberToWord
import com.perfect.nbfc.Offline.Adapter.SearchCustomerAdapter
import com.perfect.nbfc.Offline.Model.AccountModel
import com.perfect.nbfc.Offline.Model.SearchCustomerModel
import com.perfect.nbfc.Offline.Model.TransactionModel
import com.perfect.nbfc.R
import com.perfect.nbfc.balanceEnquiry.SelectAccountActivity
import com.perfect.nbfc.launchingscreens.MPIN.MPINActivity
import com.perfect.nbfc.launchingscreens.MainHome.HomeActivity
import kotlinx.android.synthetic.main.activity_deposit.*
import kotlinx.android.synthetic.main.activity_new_collection.*
import kotlinx.android.synthetic.main.activity_new_collection.input_amount
import kotlinx.android.synthetic.main.activity_new_collection.input_msg
import kotlinx.android.synthetic.main.activity_new_collection.tv_rupees
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.activity_select.*
import kotlinx.android.synthetic.main.activity_select.et_otp1
import kotlinx.android.synthetic.main.activity_select.et_otp2
import kotlinx.android.synthetic.main.activity_select.et_otp3
import kotlinx.android.synthetic.main.activity_select.et_otp4
import kotlinx.android.synthetic.main.activity_select.et_otp5
import kotlinx.android.synthetic.main.activity_select.et_otp6
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class NewCollectionActivity : AppCompatActivity(), View.OnClickListener {

    private var etsearch: EditText? = null
    private var rv_Custlist: RecyclerView? = null
    internal var accountlist = ArrayList<AccountModel>()
    lateinit var dbHelper : DBHandler
    private var adapter: SearchCustomerAdapter? = null
    internal var textlength = 0
    private var result:Boolean? = null
    private var custList: Array<String>? = null

    private var transactionid:String? = null
    private var masterid:String? = null
    private var depositno:String? = null
    private var customername:String? = null
    private var depositamount:String? = null
    private var transactionbalance:String? = null
    private var depositdate:String? = null
    private var uniqueid:String? = null
    private var remark:String? = null
    private var intId: Int?= null
    lateinit var handler: Handler
    lateinit var r: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_collection)
        val ll_selectCustomer: LinearLayout = findViewById(R.id.ll_selectCustomer)
        ll_selectCustomer.setOnClickListener(this)
        val btnSubmit: Button = findViewById(R.id.btnSubmit)
        val imback: ImageView = findViewById(R.id.imback)
        btnSubmit.setOnClickListener(this)
        //System.currentTimeMillis()
        setEdtTxtAmountCommaSeperator(input_amount,tv_rupees,true)
        imback!!.setOnClickListener { onBackPressed() }
        handler = Handler()
        r = Runnable {
            val intent= Intent(this, MPINActivity::class.java)
            startActivity(intent)
            finish()
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

    private fun getCustomer() {
        try {
            val builder = AlertDialog.Builder(this)
            val inflater1 =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater1.inflate(R.layout.customer_popup, null)
            rv_Custlist = layout.findViewById(R.id.rv_Custlist) /*as ListView*/
            etsearch = layout.findViewById(R.id.etsearch)/* as EditText*/
            builder.setView(layout)
            val alertDialog = builder.create()
            getCustomerList(alertDialog)
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getCustomerList(alertDialog: AlertDialog) {
        try {
            dbHelper = DBHandler(this)
            accountlist = ArrayList(dbHelper.readAllUsers())
            val gson = Gson()
            val listString = gson.toJson(accountlist, object : TypeToken<ArrayList<AccountModel>>() {}.type)
            val jarray = JSONArray(listString)


             custList = Array(jarray.length()) {
                jarray.getString(it)
            }

            customerNamesArrayList = populateList()
            adapter = SearchCustomerAdapter(this, customerNamesArrayList)
            rv_Custlist!!.adapter = adapter
            rv_Custlist!!.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            array_sort = ArrayList<SearchCustomerModel>()
            array_sort = populateList()
            rv_Custlist!!.addOnItemTouchListener(
                RecyclerTouchListener(
                    applicationContext,
                    rv_Custlist!!,
                    object : ClickListener {
                        override fun onClick(view: View, position: Int) {
                            var json_objectdetail: String =array_sort[position].getNames()
                            val jobject = JSONObject(json_objectdetail)
                            tvAccount.text=jobject.getString("customername")+"\n("+jobject.getString("depositno")+")"
                            masterid=jobject.getString("accountid")
                            customername=jobject.getString("customername")
                            depositno=jobject.getString("depositno")
                            transactionbalance=jobject.getString("balance")
                            alertDialog.dismiss()
                        }
                        override fun onLongClick(view: View?, position: Int) {
                        }
                    })
            )
            etsearch!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    textlength = etsearch!!.text.length
                    array_sort.clear()
                    for (i in customerNamesArrayList.indices) {
                        if (textlength <= customerNamesArrayList[i].getNames().length) {
                            if (customerNamesArrayList[i].getNames().toLowerCase().trim().contains(
                                    etsearch!!.text.toString().toLowerCase().trim { it <= ' ' })
                            ) {
                                array_sort.add(customerNamesArrayList[i])
                            }
                        }
                    }
                    adapter = SearchCustomerAdapter(this@NewCollectionActivity, array_sort)
                    rv_Custlist!!.adapter = adapter
                    rv_Custlist!!.layoutManager =
                        LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun populateList(): ArrayList<SearchCustomerModel> {
        val list = ArrayList<SearchCustomerModel>()
        for (i in 0..(accountlist.size)-1) {
            val imageModel = SearchCustomerModel()
            imageModel.setNames(custList!![i])
            list.add(imageModel)
        }
        return list
    }

    interface ClickListener {
        fun onClick(view: View, position: Int)
        fun onLongClick(view: View?, position: Int)
    }

    internal class RecyclerTouchListener(context: Context, recyclerView: RecyclerView,
                                         private val clickListener: ClickListener?) : RecyclerView.OnItemTouchListener {
        private val gestureDetector: GestureDetector
        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }
                override fun onLongPress(e: MotionEvent) {
                    val child = recyclerView.findChildViewUnder(e.x, e.y)
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child))
                    }
                }
            })
        }
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            val child = rv.findChildViewUnder(e.x, e.y)
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child))
            }
            return false
        }
        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        }
    }

    companion object {
        lateinit var customerNamesArrayList: ArrayList<SearchCustomerModel>
        lateinit var array_sort: ArrayList<SearchCustomerModel>
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



    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ll_selectCustomer->{
                getCustomer()
            }
            R.id.btnSubmit->{
                validation()
            }
        }
    }

    private fun showSuccessDialog(recAc: String,amount: String,reffNo: String) {

        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.success_ooflinecollection_layout)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val txtReceiverAC = dialog.findViewById(R.id.txtReceiverAC)as TextView
        txtReceiverAC.text = recAc

        val txtAmount = dialog.findViewById(R.id.txtAmount)as TextView
        txtAmount.text = amount+"\n[ "+tv_rupees.text.toString()+" ]"

        val txtRefferenceNo = dialog.findViewById(R.id.txtRefferenceNo)as TextView
        txtRefferenceNo.text = reffNo

        val okBtn = dialog .findViewById(R.id.btnOK) as Button
        okBtn.setOnClickListener {
            dialog .dismiss()
            val intent= Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
        }
        val printBtn = dialog .findViewById(R.id.btnprint) as Button
        printBtn.setOnClickListener {
           // dialog .dismiss()
        }
        dialog .show()
    }

    private fun validation(){
        if (tvAccount!!.text.toString() == null || tvAccount!!.text.toString().isEmpty()) {

            val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
            dialogBuilder.setMessage("Please Select Account.")
                .setCancelable(false)
                .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, id -> dialog.dismiss()
                })
            val alert = dialogBuilder.create()
            alert.show()
             val nbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                nbutton.setTextColor(Color.MAGENTA)
        }
        else if (input_amount!!.text.toString() == null || input_amount!!.text.toString().isEmpty()) {
            input_amount.setError("Please enter collection amount")
        }
        else{

            when(ConnectivityUtils.isConnected(this)) {
                true -> {
                    val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
                    dialogBuilder.setMessage("You internet connection established, you can collect amount in online mode.")
                        .setCancelable(false)
                        .setPositiveButton("COLLECT ONLINE", DialogInterface.OnClickListener {
                                dialog, id -> doDeposit()
                        })
                        .setNegativeButton("CANCEL", DialogInterface.OnClickListener {
                                dialog, id -> dialog.dismiss()
                        })
                    val alert = dialogBuilder.create()
                    alert.show()
                    val nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                    nbutton.setTextColor(Color.MAGENTA)
                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                    pbutton.setTextColor(Color.MAGENTA)
                }
                false -> {
                   doCollection()
                }
            }
        }
    }

    private fun doDeposit() {
        val intent= Intent(this, SelectAccountActivity::class.java)
        intent.putExtra("from", "Deposit")
        startActivity(intent)
        finish()
    }

    private fun doCollection() {
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val dateTime = simpleDateFormat.format(calendar.time)
        val transIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF6,0)
        if (transIdSP.getString("Transaction_ID", null) == null) {
            val transIdEditor = transIdSP.edit()
            transIdEditor.putString("Transaction_ID", "1")
            transIdEditor.commit()
            transactionid=1.toString()
        }else {
            intId = Integer.parseInt(transIdSP.getString("Transaction_ID", null)!!)
            if (intId != null) {
                transactionid=((intId!! +1).toString())
                val transIdEditor = transIdSP.edit()
                transIdEditor.putString("Transaction_ID", transactionid)
                transIdEditor.commit()
            }
        }
        depositamount=input_amount.text.toString()
        depositamount = depositamount!!.replace(",","")
        depositdate=dateTime
        uniqueid= System.currentTimeMillis().toString()
        remark=input_msg.text.toString()

        dbHelper = DBHandler(this)
        result =  dbHelper.inserttransaction(
            TransactionModel(transactionid!!,masterid!!,customername!!,depositno!!,depositamount!!,transactionbalance!!,depositdate!!,uniqueid!!,remark!!)
        )
        if(result==true){
            showSuccessDialog(depositno!!, depositamount!!, uniqueid!!)
        }else{
            Toast.makeText(applicationContext,"Some Technical Issues, please try later.",Toast.LENGTH_LONG).show()
        }
    }

}


