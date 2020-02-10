package com.amitgupta.hotspotqrgenerator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DashBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        final TextView tv = findViewById(R.id.textview);
        tv.setText("Changed");

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        BroadcastReceiver hotspotStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
//                    ipAddress = GetMac.LanIpAddress(true);
//                    Log.e("hotspotStateReceiver ", "ip: " + ipAddress);

                    // get Wi-Fi Hotspot state here
                    int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    Toast.makeText(DashBoardActivity.this,"inside:state:" + state,Toast.LENGTH_SHORT).show();
                    Log.e("hotspotStateReceiver ", "state: " + state + " check: " + state % 10);

                    if (WifiManager.WIFI_STATE_ENABLED == state % 10) {

                        Toast.makeText(DashBoardActivity.this,"enabled" + state,Toast.LENGTH_SHORT).show();
                        WifiConfiguration wifiConfig = null;
                        try {
                            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
                            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
                            wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        }

                        Log.e("hotspotStateReceiver ", "info: " + wifiConfig.allowedKeyManagement + " - " + wifiConfig.SSID + " - " + wifiConfig.preSharedKey + " - " );

                        String keyMgmt = "";
                        String allowedkeyMgmt = wifiConfig.allowedKeyManagement + "";
                        for (int i = 0; i < WifiConfiguration.KeyMgmt.strings.length; i++) {
                            String keys = WifiConfiguration.KeyMgmt.strings[Integer.parseInt(allowedkeyMgmt.replace("{", "").replace("}", ""))];
                            if (keys.contains("_"))
                                keyMgmt = keys.split("_")[0];
                            else
                                keyMgmt = keys;
                        }

                        Log.d("creds",wifiConfig.SSID +":" +wifiConfig.preSharedKey);
                        String text = wifiConfig.SSID +":" +wifiConfig.preSharedKey;
                        tv.setText(text);

                        Toast.makeText(DashBoardActivity.this,wifiConfig.SSID +":" +wifiConfig.preSharedKey,Toast.LENGTH_SHORT).show();

//                        QRCodeData qrCodeData = new QRCodeData();
//                        qrCodeData.setIpAddress(ipAddress);
//                        qrCodeData.setHotspotKeyMgmt(keyMgmt);
//                        qrCodeData.setHotspotSSID(wifiConfig.SSID);
//                        qrCodeData.setHotspotPASS(wifiConfig.preSharedKey);
//
//                        generateHotspot(qrCodeData);
//                        generateQRCode(qrCodeData);
                    } else if (WifiManager.WIFI_STATE_DISABLED == state % 10) {
//                        QRCodeData qrCodeData = new QRCodeData();
//                        qrCodeData.setIpAddress(ipAddress);
//                        qrCodeData.setHotspotKeyMgmt("");
//                        qrCodeData.setHotspotSSID("");
//                        qrCodeData.setHotspotPASS("");
//
//                        generateHotspot(qrCodeData);
//                        generateQRCode(qrCodeData);
                    }
                }
            }

        };
        registerReceiver(hotspotStateReceiver,filter);
    }
}
