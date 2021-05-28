package com.natozzo_san.gpsdemo7;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements  OnMapReadyCallback {

    LatLng marketLatLng;

    GoogleMap mMap;
    String firebaseId;
    TextView text;
    ListView listChoices;
    ListView listShops;
    public ArrayAdapter<String> adapter;
    public ArrayAdapter<String> adapterChoices;
    ArrayList<String> shops = new ArrayList<String>();
    ArrayList<String> justChoice = new ArrayList<String>();
    ArrayList<String> marketsAround = new ArrayList<>();
    ArrayList<String> marketsA = new ArrayList<>();
    protected int selected;
    String TAG = "pomogite";
    boolean globalBoolean=true;
    boolean[] flag = new boolean[3];

    HashMap<String, LatLng> adressOfMarkets = new HashMap<String, LatLng>();
    HashMap<String, String> offersMap = new HashMap<>();
    HashMap<String, Integer> choicesMap = new HashMap<>();
    ArrayList<HashMap<String, String>> arrayHasMap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Geocoder geocoder = new Geocoder(this);
        listShops = findViewById(R.id.lstShops);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shops);
        listShops.setAdapter(adapter);
        listChoices = findViewById(R.id.listChoises);
        adapterChoices = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, justChoice);
        listChoices.setAdapter(adapterChoices);
        for (int i = 0; i < 3; i++) {
            flag[i] = false;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("markets")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                firebaseId = String.valueOf(document.getData());
                                justChoice.clear();
                                Log.d(TAG, firebaseId);
                                String[] offers = firebaseId.split(", ");
                                for (String word : offers) {
                                    System.out.println(word);
                                    String[] miniOffers = word.split("=");
                                    String key, value;
                                    key = miniOffers[0];
                                    if (key.contains("{")) {
                                        key = key.replace("{", "");
                                    }
                                    value = miniOffers[1];
                                    if (value.contains("}")) {
                                        value = value.replace("}", "");
                                    }
                                    justChoice.add(key);
                                }
                                arrayHasMap.add(offersMap);
                                try {
                                    List<Address> address;
                                    address = geocoder.getFromLocationName(document.getId(), 1);
                                    Address loc = address.get(0);
                                    loc.getLatitude();
                                    loc.getLongitude();
                                    marketLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                                    adressOfMarkets.put(document.getId(), marketLatLng);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                choicesMap.put(document.getId(), arrayHasMap.indexOf(offersMap));
                                offersMap.clear();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                        adapterChoices.notifyDataSetChanged();
                    }
                });


        if(globalBoolean==true){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listChoices.setOnItemClickListener((parent, v, position, id) -> {

            ArrayList<String> list = new ArrayList<>();
            String string;

            TextView textView = (TextView) v;
            String strText = textView.getText().toString();

            db.collection("markets")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    firebaseId = String.valueOf(document.getData());
                                    justChoice.clear();
                                    Log.d(TAG, firebaseId);
                                    String[] offers = firebaseId.split(", ");
                                    for (String word : offers) {
                                        System.out.println(word);
                                        String[] miniOffers = word.split("=");
                                        String key, value;
                                        key = miniOffers[0];
                                        String s = "dkdbd&&dgs", sub = "&&";
                                        if (key.contains("{")) {
                                            key = key.replace("{", "");
                                        }
                                        value = miniOffers[1];
                                        if (value.contains("}")) {
                                            value = value.replace("}", "");
                                        }
                                        Log.d("aaaqer", strText + " + " + key + " __" + value);
                                        if (key.equals(strText) && value.equals("1")) {
                                            Log.d("uuuuuuuuuua", "11");
                                            String sGetId = document.getId();
                                            if (sGetId.equals("Большая Якиманка ул. 22, Москва")) {
                                                flag[0] = true;
                                            }
                                            if (sGetId.equals("Ленинский пр. , 75, Москва")) {
                                                flag[1] = true;
                                            }
                                            if (sGetId.equals("Радио ул. , 22, Москва")) {
                                                flag[2] = true;
                                            }
                                            marketsAround.add(sGetId);
                                            marketsA.add(sGetId);
                                            Log.d("kkkkkkkk", marketsAround.get(marketsAround.indexOf(sGetId)));
                                            list.add(sGetId);
                                            shops.clear();
                                            Log.d("iiiiii", sGetId + "___" + list.size());
                                            String smarkets = "";
                                            if (marketsAround.size() != 0) {
                                                for (int i = 0; i < marketsAround.size(); i++) {
                                                    String sma = marketsAround.get(i);
                                                    shops.add(sma);
                                                    smarkets = smarkets + "_" + marketsAround.get(i);

                                                    Log.d("hhhhhhhhhhr", marketsAround.get(i));
                                                }
                                                adapter.notifyDataSetChanged();
                                            }
                                            int sizeOfMarketsAround = marketsAround.size();
                                        }
                                    }
                                }
                            }
                        }
                    });
            selected = position;
        });
                globalBoolean = false;}
        else if(globalBoolean==false){
            listShops.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id)
                {
                    selected = position;
                }
            });
        }


        listChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                selected = position;
                TextView textView = (TextView) v;
                String strText = textView.getText().toString();
                Log.d("selectedByMe", strText);
                text.setText(strText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (adressOfMarkets == null) {
            Log.d("nnnnnn", "aaaa");
        }
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);


        if (provider == null) {
            //Toast.makeText(this, "Включите GPS", Toast.LENGTH_LONG).show();

        }
        Location location = locationManager.getLastKnownLocation(provider);

        LatLng marll = null;
        String adr = "";
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double r = 0;
            Log.d("hqqi", "q");
            for (Map.Entry entry : adressOfMarkets.entrySet()) {
                LatLng ll = (LatLng) entry.getValue();
                Log.d("hiii", String.valueOf(ll));
                double llat = ll.latitude;
                double llng = ll.longitude;
                double r1 = Math.sqrt((latitude - llat) * (latitude - llat) + (longitude - llng) * (longitude - llng));
                googleMap.addMarker(new MarkerOptions().position(ll).title("Market is here"));
                if (r1 > r) {
                    marll = ll;
                    adr = (String) entry.getKey();
                }
            }
            LatLng myPosition = new LatLng(latitude, longitude);
            if (flag[0] = true) {
                double la = 55.738366299999996;
                double ln = 37.6145557;
                LatLng ll = new LatLng(la, ln);
                googleMap.addMarker(new MarkerOptions().position(ll).title("Большая Якиманка ул. 22"))
                        .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_4));
            }
            if (flag[1] = true) {
                double la = 55.686558999999995;
                double ln = 37.54468;
                LatLng ll = new LatLng(la, ln);
                googleMap.addMarker(new MarkerOptions().position(ll).title("Ленинский пр. , 75"))
                        .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_4));
            }
            if (flag[2] = true) {
                double la = 55.7621344;
                double ln = 37.681111099999995;
                LatLng ll = new LatLng(la, ln);
                googleMap.addMarker(new MarkerOptions().position(ll).title("Радио ул. , 22"))
                        .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_4));
            }
            if (marll != null) {
                googleMap.addMarker(new MarkerOptions().position(marll).title("Market is here"));
            }
            googleMap.addMarker(new MarkerOptions().position(myPosition).title("You are here"))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icons8));
            double la = 55.738366299999996;
            double ln = 37.6145557;
            LatLng ll = new LatLng(la, ln);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll,9));

            //Polyline line = mMap.addPolyline(new PolylineOptions()
            //.add(new LatLng(latitude+2, longitude-2), new LatLng(latitude-2, longitude+2))
            //.width(5)
            //.color(Color.RED));
            //Double north, west, south, east;
            //south = latitude - 10;
            //north = latitude + 10;
            //west = longitude - 50;
            //east = longitude + 50;
            //LatLngBounds australiaBounds = new LatLngBounds(
            //new LatLng(south, west), // SW bounds
            //new LatLng(north, east)  // NE bounds
            //);
            //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(australiaBounds, 0));

        }
    }
}