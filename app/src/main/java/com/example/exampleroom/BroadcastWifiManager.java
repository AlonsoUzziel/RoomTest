package com.example.exampleroom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class BroadcastWifiManager {

    private BroadcastListener listener;
    public BroadcastWifiManager(BroadcastListener listener)
    {
        this.listener = listener;
    }

    public BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra  = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);
            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:
                    listener.isConnected(true);
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    listener.isConnected(false);
                    break;
            }
        }
    };

    public interface BroadcastListener {
        void isConnected(boolean status);
    }
}
