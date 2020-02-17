package com.amitgupta.hotspotqrgenerator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.Method;

public class DashBoardActivity extends AppCompatActivity {

    private ConstraintLayout hotspotInfoLayout;
    private TextView hotspotErrorMessage;
    private ImageView hotspotQRCode;
    private TextView hotspotUsernamePassword;
    BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
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
            } else*/
            if (WifiManager.WIFI_STATE_ENABLED == state % 10) {
                try {
                    if (Build.VERSION.SDK_INT >= 27) {
//                Toast.makeText(DashBoardActivity.this, "hotspotStateReceiver: enabled - " + state, Toast.LENGTH_SHORT).show();

                        WifiConfiguration wifiConfig = null;
                        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
                        wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

//                Toast.makeText(DashBoardActivity.this, "hotspotStateReceiver: info - " + wifiConfig.allowedKeyManagement + wifiConfig.SSID + " - " + wifiConfig.preSharedKey, Toast.LENGTH_SHORT).show();

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
                    } else {
                        hotspotErrorMessage.setText("This app doesn't support for this android version.");

                        hotspotInfoLayout.setVisibility(View.GONE);
                        hotspotErrorMessage.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (WifiManager.WIFI_STATE_DISABLED == state % 10) {
//                Toast.makeText(DashBoardActivity.this, "hotspotStateReceiver: disabled - " + state, Toast.LENGTH_SHORT).show();

                hotspotErrorMessage.setText("Hotspot Disabled");

                hotspotInfoLayout.setVisibility(View.GONE);
                hotspotErrorMessage.setVisibility(View.VISIBLE);
            } else {
//                Toast.makeText(DashBoardActivity.this, "hotspotStateReceiver: disabled - " + state, Toast.LENGTH_SHORT).show();

                hotspotErrorMessage.setText("WIFI Disabled");

                hotspotInfoLayout.setVisibility(View.GONE);
                hotspotErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    };

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

        IntentFilter networkFilter = new IntentFilter();
        networkFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");

        registerReceiver(networkReceiver, networkFilter);
    }

    private void generateQRCode(String keyMgmt, String ssid, String preSharedKey) {
        String text = "WIFI:T:" + keyMgmt + ";S:" + ssid + ";P:" + preSharedKey + ";";
        Log.e("hotspotStateReceiver ", "info: " + text);

        QRCodeUtils qrCodeUtils = new QRCodeUtils();
        hotspotQRCode.setImageBitmap(qrCodeUtils.generateQRCode(text, 150, 150));
        hotspotUsernamePassword.setText(ssid + " / " + preSharedKey);

        hotspotInfoLayout.setVisibility(View.VISIBLE);
        hotspotErrorMessage.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(networkReceiver);

        super.onDestroy();
    }

}
