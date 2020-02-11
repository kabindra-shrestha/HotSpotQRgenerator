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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DashBoardActivity extends AppCompatActivity {

    private ConstraintLayout hotspotInfoLayout;
    private TextView hotspotErrorMessage;
    private ImageView hotspotQRCode;
    private TextView hotspotUsernamePassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        hotspotInfoLayout = findViewById(R.id.hotspot_info_layout);
        hotspotErrorMessage = findViewById(R.id.hotspot_error_message);
        hotspotQRCode = findViewById(R.id.hotspot_qr_code);
        hotspotUsernamePassword = findViewById(R.id.hotspot_username_password);

        hotspotInfoLayout.setVisibility(View.GONE);
        hotspotErrorMessage.setVisibility(View.GONE);

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

                        String keyMgmt = "";
                        String allowedkeyMgmt = wifiConfig.allowedKeyManagement + "";
                        for (int i = 0; i < WifiConfiguration.KeyMgmt.strings.length; i++) {
                            String keys = WifiConfiguration.KeyMgmt.strings[Integer.parseInt(allowedkeyMgmt.replace("{", "").replace("}", ""))];
                            if (keys.contains("_"))
                                keyMgmt = keys.split("_")[0];
                            else
                                keyMgmt = keys;
                        }

                        generateQRCode(keyMgmt, wifiConfig.SSID, wifiConfig.preSharedKey);
                    } else if (WifiManager.WIFI_STATE_DISABLED == state % 10) {
                        Toast.makeText(DashBoardActivity.this, "hotspotStateReceiver: disabled - " + state, Toast.LENGTH_SHORT).show();

                        hotspotErrorMessage.setText("Hotspot Disabled");

                        hotspotInfoLayout.setVisibility(View.GONE);
                        hotspotErrorMessage.setVisibility(View.VISIBLE);
                    }
                }
            }

        };

        registerReceiver(hotspotStateReceiver, filter);
    }

    private void generateQRCode(String keyMgmt, String ssid, String preSharedKey) {
        String text = "WIFI:T:" + keyMgmt + ";S:" + ssid + ";P:" + preSharedKey + ";";
        Log.e("hotspotStateReceiver ", "info: " + text);

        QRCodeUtils qrCodeUtils = new QRCodeUtils();
        hotspotQRCode.setImageBitmap(qrCodeUtils.generateQRCode(text, 98, 98));
        hotspotUsernamePassword.setText(ssid + " / " + preSharedKey);

        hotspotInfoLayout.setVisibility(View.VISIBLE);
        hotspotErrorMessage.setVisibility(View.GONE);
    }

}
