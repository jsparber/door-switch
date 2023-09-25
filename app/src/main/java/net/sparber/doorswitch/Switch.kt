package net.sparber.doorswitch

import android.content.SharedPreferences
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager

class Switch : ComponentActivity() {
    private val network = Network()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        val prefs: SharedPreferences? = PreferenceManager.getDefaultSharedPreferences(this)
        prefs ?: return
        val key = prefs.getString("deviceKey", "")
        key ?: return
        val address = prefs.getString("deviceAddress", "")
        address ?: return
        val port = prefs.getString("devicePort", "")
        port ?: return
        val device = Device(address, port.toInt(), key)

        network.switch(device, true)

       finish()
    }
}