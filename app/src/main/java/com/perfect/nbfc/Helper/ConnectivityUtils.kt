package com.perfect.nbfc.Helper

import android.content.Context
import android.net.ConnectivityManager

object ConnectivityUtils {
    fun isConnected(context: Context): Boolean {
        val connectivityManager = context.applicationContext.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}