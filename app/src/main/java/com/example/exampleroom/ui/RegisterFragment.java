package com.example.exampleroom.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exampleroom.IO.PxApiAdapter;
import com.example.exampleroom.R;
import com.example.exampleroom.database.AppDataBase;
import com.example.exampleroom.database.DAO.EntryDAO;
import com.example.exampleroom.database.DAO.ItemDAO;
import com.example.exampleroom.database.Entity.Item;
import com.example.exampleroom.database.repository.EntryRepository;
import com.example.exampleroom.database.repository.EntryRepositoryImpl;
import com.example.exampleroom.database.repository.ItemRepository;
import com.example.exampleroom.database.repository.ItemRepositoryImpl;
import com.example.exampleroom.models.Entry;
import com.example.exampleroom.database.Entity.EntryDB;
import com.example.exampleroom.models.PublicApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment implements RvEntryAdapter.EntryAdapterListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int sumSend = 0, sumPending = 0;
    private boolean isConnected = true;
    private ItemRepository repoItem;
    private EntryRepository repoEntry;
    TextView stateWifi,btnSearch,tvSendItem,tvPendingItem;
    private View v;
    private ArrayList<Entry> entries = new ArrayList<>();
    private RecyclerView list;
    private int page = 1;
    RvEntryAdapter adapter;
    EditText etSearch;
    private boolean isLoading = false;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_register, container, false);
        AppDataBase db = AppDataBase.getInstance(getActivity().getApplicationContext());
        ItemDAO daoItem = db.itemDAO();
        EntryDAO daoEntry = db.entryDAO();
        repoItem = new ItemRepositoryImpl(daoItem);
        repoEntry = new EntryRepositoryImpl(daoEntry);

        entries = new ArrayList<>();
        list = (RecyclerView) v.findViewById(R.id.list_entries);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        list.setLayoutManager(mLayoutManager);
        list.setItemAnimator(new DefaultItemAnimator());
        adapter = new RvEntryAdapter(getContext(),entries,this::onEntrySelected);
        list.setAdapter(adapter);
        tvSendItem = v.findViewById(R.id.tvSendItems);
        tvPendingItem = v.findViewById(R.id.tvPendingItems);
        /*
        tvAddItem.setOnClickListener(view -> {
            if(isConnected) {
                sumSend++;
                // simulated send to ws
                String message = getString(R.string.txtSendItems) + sumSend;
                tvSumSend.setText(message);
            }
            else{
                sumPending++;
                insertItem(sumPending);
                String message = getString(R.string.txtPendingItems) + sumPending;
                tvSumPending.setText(message);
            }
        });

         */

        btnSearch = v.findViewById(R.id.btnSearch);
        etSearch = v.findViewById(R.id.etSearch);
        stateWifi = v.findViewById(R.id.tvStateWifi);
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled())
        {
            stateWifi.setText(getString(R.string.txtConnected));
            isConnected = true;
            getAllEntries();
        }
        else{
            stateWifi.setText(getString(R.string.txtUnConnected));
            verifyItems();
            isConnected = false;
        }

        btnSearch.setOnClickListener(view ->{
            String search = etSearch.getText().toString();
            if(!search.isEmpty()){
                if(isConnected)
                    getEntries(search);
                else
                    getEntriesFromRoom(search);
            }
        });

        return v;
    }

    private void getAllEntries() {

        List<EntryDB> entries = repoEntry.getAll();
        if(entries.size() == 0)
        {
            Log.d("Message :::", "getAllEntries start ");
            String url = getResources().getString(R.string.endpointBase) + "entries";
            Call<PublicApi> call = PxApiAdapter.getApiService(getResources().getString(R.string.endpointBase)).getEntries(url);

            call.enqueue(new Callback<PublicApi>(){
                @Override
                public void onResponse(Call<PublicApi> call, Response<PublicApi> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            ArrayList<Entry> entriesObj =  response.body().getEntries();
                            for (Entry e : entriesObj) {
                                EntryDB entryDB = new EntryDB();
                                entryDB.setAPI(e.getAPI());
                                entryDB.setAuth(e.getAuth());
                                entryDB.setCategory(e.getCategory());
                                entryDB.setCors(e.getCors());
                                entryDB.setDescription(e.getDescription());
                                entryDB.setHTTPS(e.getHTTPS());
                                entryDB.setLink(e.getLink());
                                repoEntry.insert(entryDB);
                            }
                        }
                        Log.d("Message :::", "size: " + entries.size());

                    } else {
                        Log.d("Message :::", "code: "+response.code());
                    }
                }
                @Override
                public void onFailure(Call<PublicApi> call, Throwable t) {
                    Log.d("Message ::", t.getMessage());
                }
            });
        }
        else{
            Log.d("Message :::", "EntriesDB exists: " + entries.size());
        }
    }

    private void getEntriesFromRoom(String search) {
        List<EntryDB> entriesDb = repoEntry.getLikeApi(search);
        if(entriesDb.size() > 0)
        {
            for (EntryDB e : entriesDb) {
                Entry entry = new Entry();
                entry.setAPI(e.getAPI());
                entry.setAuth(e.getAuth());
                entry.setCategory(e.getCategory());
                entry.setCors(e.getCors());
                entry.setDescription(e.getDescription());
                entry.setHTTPS(e.getHTTPS());
                entry.setLink(e.getLink());
                entries.add(entry);
            }
            adapter.notifyDataSetChanged();
            isLoading = false;
        }
    }

    private void getEntries(String search) {
        String url = getResources().getString(R.string.endpointBase) + "entries?description=" + search;
        Call<PublicApi> call = PxApiAdapter.getApiService(getResources().getString(R.string.endpointBase)).getEntries(url);

        call.enqueue(new Callback<PublicApi>(){
            @Override
            public void onResponse(Call<PublicApi> call, Response<PublicApi> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        entries.addAll(response.body().getEntries());
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                    }
                    Log.d("Message :::", "size: " + entries.size());

                } else {
                    Log.d("Message :::", "code: "+response.code());
                }
            }
            @Override
            public void onFailure(Call<PublicApi> call, Throwable t) {
                Log.d("Message ::", t.getMessage());
            }
        });

    }

    @Override
    public void onEntrySelected(Entry entry) {
        new AlertDialog.Builder(getContext())
                .setTitle("Agregar")
                .setMessage("¿Está seguro de agregar el siguiente item? " + entry.getAPI())
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendEntry(entry);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .show();
    }


    private void sendEntry(Entry entry) {
        if(isConnected){
            //TODO send entry to WS
            sumSend++;
            String message = getString(R.string.txtSendItems) + sumSend;
            tvSendItem.setText(message);
        }
        else{
            sumPending++;
            insertItem(sumPending,entry);
            String message = getString(R.string.txtPendingItems) + sumPending;
            tvPendingItem.setText(message);
        }
        entries.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(getActivity().getBaseContext(), "Item agregado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        IntentFilter intent = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(wifiStateReceiver,intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(wifiStateReceiver);
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra  = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:
                    stateWifi.setText(getString(R.string.txtConnected));
                    verifyItemsForSend();
                    isConnected = true;
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    stateWifi.setText(getString(R.string.txtUnConnected));
                    isConnected = false;
                    break;
            }
        }
    };

    private void verifyItems() {
        List<Item> items = repoItem.getAll();
        Log.d("Message ::","Items pending size: " + items.size());

        if (items.size() > 0) {
            sumPending = items.size();
            String message = getString(R.string.txtPendingItems) + sumPending;
            tvPendingItem.setText(message);
        }
    }

    private void verifyItemsForSend() {
        List<Item> items = repoItem.getAll();
        Log.d("Message ::","Items size: " + items.size());

        if (items.size() > 0) {
            sumPending = items.size();
            sendData(items);
        }
    }

    private void sendData(List<Item> items)
    {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("Message ::", "run ");

                    for (Item item : items) {
                        Log.d("Message ::", "Item send: " + item.getId());
                        // simulate send to ws
                        repoItem.delete(item);
                        sumSend++;
                        sumPending--;
                    }
                    String messageSend = getString(R.string.txtSendItems) + sumSend;
                    tvSendItem.setText(messageSend);
                    String message = getString(R.string.txtPendingItems) + sumPending;
                    tvPendingItem.setText(message);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void insertItem(int num,Entry entry)
    {
        Item item = new Item();
        item.setName(entry.getAPI());
        item.setChecked(false);
        Log.d("Message ::","Item for insert: " + num);

        repoItem.insert(item);
    }
}
