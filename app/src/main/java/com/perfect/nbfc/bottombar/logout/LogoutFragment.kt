package com.perfect.nbfc.bottombar.logout

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.perfect.nbfc.DB.DBHandler
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.R
import com.perfect.nbfc.launchingscreens.Login.LoginActivity
import com.perfect.nbfc.launchingscreens.MainHome.HomeActivity


class LogoutFragment : Fragment(), View.OnClickListener {

    lateinit var agent:String
    private var tvUsername: TextView? = null
    lateinit var dbHelper : DBHandler

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnlogout->{
                val dialogBuilder = AlertDialog.Builder(context!!)
                dialogBuilder.setTitle("Are you sure? Do you want to logout ? ")
//                    .setMessage("It will clear your all saved datas and your account details.")
                    .setCancelable(false)
                    .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, which ->
                        doLogout()
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                    })
                val alert = dialogBuilder.create()
                alert.show()
            }
            R.id.btncancel->{
                var intent = Intent(context, HomeActivity::class.java)
                startActivity(intent)
                activity!!.finish()
            }
        }
    }

    private lateinit var dashboardViewModel: LogoutViewModel

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dashboardViewModel =ViewModelProviders.of(this).get(LogoutViewModel::class.java)
        (activity as AppCompatActivity).supportActionBar!!.hide()
        val root = inflater.inflate(R.layout.fragment_logout, container, false)
        val btnlogout: Button = root.findViewById(R.id.btnlogout)
        val btncancel: Button = root.findViewById(R.id.btncancel)
        tvUsername =root.findViewById(R.id.tvUsername)
        btnlogout.setOnClickListener(this)
        btncancel.setOnClickListener(this)
        val AgentName    = context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF2,0)
        agent   = AgentName.getString("Agent_Name", null)!!
        if(agent!=null){
            tvUsername!!.text = "Hello "+agent+" !"
        }
        return root
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