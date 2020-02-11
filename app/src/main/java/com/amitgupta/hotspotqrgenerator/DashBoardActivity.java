package com.amitgupta.hotspotqrgenerator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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
                    // get Wi-Fi Hotspot state here
                    int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    Toast.makeText(DashBoardActivity.this, "hotspotStateReceiver: state - " + state + " check - " + state % 10, Toast.LENGTH_SHORT).show();

                    if (WifiManager.WIFI_STATE_ENABLED == state % 10) {
                        Toast.makeText(DashBoardActivity.this, "hotspotStateReceiver: enabled - " + state, Toast.LENGTH_SHORT).show();

                        WifiConfiguration wifiConfig = null;
                        try {
                            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
                            wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(DashBoardActivity.this, "hotspotStateReceiver: info - " + wifiConfig.allowedKeyManagement + wifiConfig.SSID + " - " + wifiConfig.preSharedKey, Toast.LENGTH_SHORT).show();
                        tv.setText("hotspotStateReceiver: info - " + wifiConfig.allowedKeyManagement + wifiConfig.SSID + " - " + wifiConfig.preSharedKey);
                    } else if (WifiManager.WIFI_STATE_DISABLED == state % 10) {
                        Toast.makeText(DashBoardActivity.this, "hotspotStateReceiver: disabled - " + state, Toast.LENGTH_SHORT).show();
                        tv.setText("hotspotStateReceiver: disabled - " + state);
                    }
                }
            }

        };

        registerReceiver(hotspotStateReceiver, filter);
    }
}