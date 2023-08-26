package com.kabindra.hotspotqrgenerator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import com.kabindra.hotspotqrgenerator.base.BaseActivity
import com.kabindra.hotspotqrgenerator.databinding.ActivityDashboardBinding

class DashBoardActivity :
    BaseActivity<ActivityDashboardBinding>(ActivityDashboardBinding::inflate) {

    var networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)
            //            Toast.makeText(DashBoardActivity.this, "hotspotStateReceiver: state - " + state + " check - " + state % 10, Toast.LENGTH_SHORT).show();

            /*if (WifiManager.WIFI_STATE_ENABLED == state % 10) {
                try {
//                Toast.makeText(DashBoardActivity.this, "networkReceiver: enabled - ", Toast.LENGTH_SHORT).show();
                    List<WifiConfiguration> wifiConfig = null;
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    wifiConfig = wifiManager.getConfiguredNetworks();


                    for (WifiConfiguration items : wifiConfig) {
                        String keyMgmt = "";
                        String allowedkeyMgmt = items.allowedKeyManagement + "";
                        for (int i = 0; i < WifiConfiguration.KeyMgmt.strings.length; i++) {
                            String keys = WifiConfiguration.KeyMgmt.strings[Integer.parseInt(allowedkeyMgmt.replace("{", "").replace("}", ""))];
                            if (keys.contains("_"))
                                keyMgmt = keys.split("_")[0];
                            else
                                keyMgmt = keys;
                        }
                        Log.e("networkReceiver: ", "wifiConfig: " + keyMgmt + items.SSID + " - " + items.preSharedKey);
                    }

//                generateQRCode("", wifiConfig.getSSID(), "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else*/if (WifiManager.WIFI_STATE_ENABLED == state % 10) {
                try {
                    if (Build.VERSION.SDK_INT <= 27) {
//                Toast.makeText(DashBoardActivity.this, "hotspotStateReceiver: enabled - " + state, Toast.LENGTH_SHORT).show();
                        var wifiConfig: WifiConfiguration? = null
                        val wifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager
                        val getConfigMethod =
                            wifiManager.javaClass.getMethod("getWifiApConfiguration")
                        wifiConfig = getConfigMethod.invoke(wifiManager) as WifiConfiguration

//                Toast.makeText(DashBoardActivity.this, "hotspotStateReceiver: info - " + wifiConfig.allowedKeyManagement + wifiConfig.SSID + " - " + wifiConfig.preSharedKey, Toast.LENGTH_SHORT).show();
                        var keyMgmt = ""
                        val allowedkeyMgmt = wifiConfig.allowedKeyManagement.toString() + ""
                        for (i in WifiConfiguration.KeyMgmt.strings.indices) {
                            val keys =
                                WifiConfiguration.KeyMgmt.strings[allowedkeyMgmt.replace("{", "")
                                    .replace("}", "").toInt()]
                            keyMgmt = if (keys.contains("_")) keys.split("_".toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()[0] else keys
                        }
                        generateQRCode(keyMgmt, wifiConfig!!.SSID, wifiConfig.preSharedKey)
                    } else {
                        views.hotspotErrorMessage!!.text =
                            "This app doesn't support for this android version."
                        views.hotspotInfoLayout!!.visibility = View.GONE
                        views.hotspotErrorMessage!!.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (WifiManager.WIFI_STATE_DISABLED == state % 10) {
//                Toast.makeText(DashBoardActivity.this, "hotspotStateReceiver: disabled - " + state, Toast.LENGTH_SHORT).show();
                views.hotspotErrorMessage!!.text = "Hotspot Disabled"
                views.hotspotInfoLayout!!.visibility = View.GONE
                views.hotspotErrorMessage!!.visibility = View.VISIBLE
            } else {
//                Toast.makeText(DashBoardActivity.this, "hotspotStateReceiver: disabled - " + state, Toast.LENGTH_SHORT).show();
                views.hotspotErrorMessage!!.text = "WIFI Disabled"
                views.hotspotInfoLayout!!.visibility = View.GONE
                views.hotspotErrorMessage!!.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        views.hotspotInfoLayout.setVisibility(View.GONE)
        views.hotspotErrorMessage.setVisibility(View.GONE)
        val networkFilter = IntentFilter()
        networkFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        networkFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED")
        registerReceiver(networkReceiver, networkFilter)
    }

    private fun generateQRCode(keyMgmt: String, ssid: String, preSharedKey: String) {
        val text = "WIFI:T:$keyMgmt;S:$ssid;P:$preSharedKey;"
        Log.e("hotspotStateReceiver ", "info: $text")
        val qrCodeUtils = QRCodeUtils()
        views.hotspotQrCode!!.setImageBitmap(qrCodeUtils.generateQRCode(text, 150, 150))
        views.hotspotUsernamePassword!!.text = "$ssid / $preSharedKey"
        views.hotspotInfoLayout!!.visibility = View.VISIBLE
        views.hotspotErrorMessage!!.visibility = View.GONE
    }

    override fun onDestroy() {
        unregisterReceiver(networkReceiver)
        super.onDestroy()
    }
}