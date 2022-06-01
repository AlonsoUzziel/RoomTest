package com.example.exampleroom;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.exampleroom.database.AppDataBase;
import com.example.exampleroom.database.DAO.ItemDAO;
import com.example.exampleroom.database.Entity.Item;
import com.example.exampleroom.database.repository.ItemRepository;
import com.example.exampleroom.database.repository.ItemRepositoryImpl;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView stateWifi,tvSumSend,tvSumPending,tvAddItem;
    int sumSend = 0, sumPending = 0;
    boolean isConnected = true;
    ItemRepository repo;
    private WifiManager wifiManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppDataBase db = AppDataBase.getInstance(this.getApplicationContext());
        ItemDAO dao = db.itemDAO();
        repo = new ItemRepositoryImpl(dao);

        tvSumSend = findViewById(R.id.tvSumSend);
        tvSumPending = findViewById(R.id.tvSumPending);
        tvAddItem = findViewById(R.id.tvAddItem);

        tvAddItem.setOnClickListener(view -> {
            if(isConnected) {
                sumSend++;
                // simulated send to ws
                tvSumSend.setText("Items enviados: " + sumSend);
            }
            else{
                sumPending++;
                insertItem(sumPending);
                tvSumPending.setText("Items pendientes: " + sumPending);
            }
        });


        stateWifi = findViewById(R.id.tvStateWifi);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled())
        {
            stateWifi.setText("Conectado");
            isConnected = true;
        }
        else{
            stateWifi.setText("Sin conexión a internet");
            verifyItems();
            isConnected = false;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        IntentFilter intent = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver,intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiStateReceiver);
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra  = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:
                    stateWifi.setText("Conectado");
                    verifyItemsForSend();
                    isConnected = true;
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    stateWifi.setText("Sin conexión a internet");
                    isConnected = false;
                    break;
            }
        }
    };

    private void verifyItems() {
        List<Item> items = repo.getAll();
        Log.d("Message ::","Items size: " + items.size());

        if (items.size() > 0) {
            sumPending = items.size();
            tvSumPending.setText("Items pendientes: " + sumPending);
        }
    }

    private void verifyItemsForSend() {
        List<Item> items = repo.getAll();
        Log.d("Message ::","Items size: " + items.size());

        if (items.size() > 0) {
            sumPending = items.size();
            sendData(items);
        }
    }

    private void sendData(List<Item> items)
    {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("Message ::", "run ");

                    for (Item item : items) {
                        Log.d("Message ::", "Item send: " + item.getId());
                        repo.delete(item);
                        sumSend++;
                        sumPending--;
                    }
                    tvSumSend.setText("Items enviados: " + sumSend);
                    tvSumPending.setText("Items pendientes: " + sumPending);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void insertItem(int num)
    {
        Item item = new Item();
        item.setName("Item " + num);
        item.setChecked(false);
        Log.d("Message ::","Item for insert: " + num);

        repo.insert(item);
    }
}