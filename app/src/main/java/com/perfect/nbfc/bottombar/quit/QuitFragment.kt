package com.perfect.nbfc.bottombar.quit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.R
import com.perfect.nbfc.launchingscreens.MainHome.HomeActivity


class QuitFragment : Fragment(), View.OnClickListener {

    lateinit var agent:String
    private var tvUsername: TextView? = null

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnlogout->{
                quit()
            }
            R.id.btncancel->{
               var intent = Intent(context, HomeActivity::class.java)
                startActivity(intent)
                activity!!.finish()
            }
        }
    }

    private lateinit var dashboardViewModel: QuitModel

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dashboardViewModel =ViewModelProviders.of(this).get(QuitModel::class.java)
        (activity as AppCompatActivity).supportActionBar!!.hide()
        val root = inflater.inflate(R.layout.fragment_quit, container, false)
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

    private fun quit() {
        try {
                activity!!.finish()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    activity!!.finishAffinity()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}