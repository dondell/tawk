package com.dmj.tawktodevtest.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dmj.tawktodevtest.utils.NetworkUtil.getConnectivityStatusString

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val status = context?.let { getConnectivityStatusString(it) }
        if ("android.net.conn.CONNECTIVITY_CHANGE" == intent.action) {
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            } else {
            }
        }
    }
}