package com.perfect.nbfc.launchingscreens.IntroSlides

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.perfect.nbfc.Helper.BizcoreApplication
import com.perfect.nbfc.R
import com.perfect.nbfc.launchingscreens.Login.LoginActivity
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        llGetstarted!!.setOnClickListener(){
            checkGps()
//            intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
        }

        val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
        var Imei = DeviceAppDetails1.imei
        ll_androidid!!.setOnClickListener(){
            val dialogBuilder = AlertDialog.Builder(this@WelcomeActivity, R.style.MyDialogTheme)
            dialogBuilder.setTitle("Android ID : "+ Imei)
//                .setMessage("Android ID : "+ Imei)
                .setCancelable(false)
                .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, id ->
                    dialog.dismiss()
                })
            val alert = dialogBuilder.create()
            alert.show()
            val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
            pbutton.setTextColor(Color.MAGENTA)
        }
    }
    private fun checkGps() {

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                101
            )
            Log.e("TAG", "checkGps  if   49")
        } else {
            Log.e("TAG", "checkGps  else   51")
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
}
